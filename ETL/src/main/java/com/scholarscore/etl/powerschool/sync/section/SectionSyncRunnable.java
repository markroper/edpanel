package com.scholarscore.etl.powerschool.sync.section;

import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.powerschool.api.model.PsSection;
import com.scholarscore.etl.powerschool.api.response.SectionResponse;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.etl.powerschool.sync.ISync;
import com.scholarscore.etl.powerschool.sync.assignment.SectionAssignmentSync;
import com.scholarscore.etl.powerschool.sync.associator.StaffAssociator;
import com.scholarscore.etl.powerschool.sync.associator.StudentAssociator;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.Section;
import com.scholarscore.models.Term;
import com.scholarscore.models.user.Teacher;
import com.scholarscore.models.user.User;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * For a single school within a district, this runnable can execute on its own thread and handles
 * extracting all sections for the school from PowerSchool and creating corresponding section in EdPanel.
 * Migration of a section includes enrolling the correct students, associating the correct teacher with the section,
 * migrating all the assignments for the section, migrating any student grades at the section or the assignment level.
 *
 * Created by markroper on 10/25/15.
 */
public class SectionSyncRunnable implements Runnable, ISync<Section> {
    private static final int THREAD_POOL_SIZE = 10;
    private IPowerSchoolClient powerSchool;
    private IAPIClient edPanel;
    private School school;
    private ConcurrentHashMap<Long, Course> courses;
    private ConcurrentHashMap<Long, Term> terms;
    private StudentAssociator studentAssociator;
    private StaffAssociator staffAssociator;
    private ConcurrentHashMap<Long, Section> sections;
    private List<Long> unresolvablePowerStudents;

    public SectionSyncRunnable(IPowerSchoolClient powerSchool,
                               IAPIClient edPanel,
                               School school,
                               ConcurrentHashMap<Long, Course> courses,
                               ConcurrentHashMap<Long, Term> terms,
                               StaffAssociator staffAssociator,
                               StudentAssociator studentAssociator,
                               ConcurrentHashMap<Long, Section> sections,
                               List<Long> unresolvablePowerStudents) {
        this.powerSchool = powerSchool;
        this.edPanel = edPanel;
        this.school = school;
        this.courses = courses;
        this.terms = terms;
        this.staffAssociator = staffAssociator;
        this.studentAssociator = studentAssociator;
        this.sections = sections;
        this.unresolvablePowerStudents = unresolvablePowerStudents;
    }

    @Override
    public void run() {
        synchCreateUpdateDelete();
    }

    @Override
    public ConcurrentHashMap<Long, Section> synchCreateUpdateDelete() {
        ConcurrentHashMap<Long, Section> source = resolveAllFromSourceSystem();
        ConcurrentHashMap<Long, Section> ed = resolveFromEdPanel();
        Iterator<Map.Entry<Long, Section>> sourceIterator = source.entrySet().iterator();
        //Find & perform the inserts and updates, if any
        while(sourceIterator.hasNext()) {
            Map.Entry<Long, Section> entry = sourceIterator.next();
            Section sourceSection = entry.getValue();
            Section edPanelSection = ed.get(entry.getKey());
            if(null == edPanelSection){
                Section created = edPanel.createSection(
                       school.getId(),
                        sourceSection.getTerm().getSchoolYear().getId(),
                        sourceSection.getTerm().getId(),
                        sourceSection);
                sourceSection.setId(created.getId());
                this.sections.put(new Long(sourceSection.getSourceSystemId()), sourceSection);
            } else {
                sourceSection.setId(edPanelSection.getId());
                if(!edPanelSection.equals(sourceSection)) {
                    edPanel.replaceSection(school.getId(),
                            sourceSection.getTerm().getSchoolYear().getId(),
                            sourceSection.getTerm().getId(),
                            sourceSection);
                    this.sections.put(new Long(sourceSection.getSourceSystemId()), sourceSection);
                }
            }
//            migrateStudentSectionEnrollmentAndGrades(sourceSection.getTerm(), sourceSection);
//            migrateStudentAssignmentGrades(sourceSection.getTerm(), sourceSection);
            StudentSectionGradeSync ssgSync = new StudentSectionGradeSync(
                    powerSchool,
                    edPanel,
                    school,
                    studentAssociator,
                    unresolvablePowerStudents,
                    sourceSection);
            ssgSync.synchCreateUpdateDelete();

            SectionAssignmentSync assignmentSync = new SectionAssignmentSync(
                    powerSchool,
                    edPanel,
                    school,
                    studentAssociator,
                    unresolvablePowerStudents,
                    sourceSection
            );
            assignmentSync.synchCreateUpdateDelete();
        }
        //Delete anything IN EdPanel that is NOT in source system
        Iterator<Map.Entry<Long, Section>> edpanelIterator = ed.entrySet().iterator();
        while(edpanelIterator.hasNext()) {
            Map.Entry<Long, Section> entry = edpanelIterator.next();
            if(!source.containsKey(entry.getKey())) {
                Section edPanelSection = entry.getValue();
                edPanel.deleteSection(school.getId(),
                        edPanelSection.getTerm().getSchoolYear().getId(),
                        edPanelSection.getTerm().getId(),
                        edPanelSection);
            }
        }
        return source;
    }

    protected ConcurrentHashMap<Long, Section> resolveAllFromSourceSystem() {
        ConcurrentHashMap<Long, Section> result = new ConcurrentHashMap<>();
        SectionResponse sr = powerSchool.getSectionsBySchoolId(Long.valueOf(school.getSourceSystemId()));
        if(null != sr && null != sr.sections && null != sr.sections.section) {
            List<PsSection> powerSchoolSections
                    = sr.sections.section;
            for (PsSection powerSection : powerSchoolSections) {
                Section edpanelSection = new Section();
                edpanelSection.setSourceSystemId(powerSection.getId().toString());
                //Resolve the EdPanel Course and set it on the EdPanel section
                Course c = this.courses.get(Long.valueOf(powerSection.getCourse_id()));
                if(null != c) {
                    edpanelSection.setCourse(c);
                    edpanelSection.setName(c.getName());
                }
                //Resolve the EdPanel Term and set it on the Section
                Term sectionTerm = this.terms.get(powerSection.getTerm_id());
                edpanelSection.setTerm(sectionTerm);
                edpanelSection.setStartDate(sectionTerm.getStartDate());
                edpanelSection.setEndDate(sectionTerm.getEndDate());
                //Resolve the EdPanel Teacher(s) and set on the Section
                User t = staffAssociator.findBySourceSystemId(powerSection.getStaff_id());
                if(null != t && t instanceof Teacher) {
                    HashSet<Teacher> teachers = new HashSet<>();
                    teachers.add((Teacher) t);
                    edpanelSection.setTeachers(teachers);
                }
                result.put(powerSection.getId(), edpanelSection);
            }
        }
        return result;
    }

    protected ConcurrentHashMap<Long, Section> resolveFromEdPanel() {
        Section[] sections = edPanel.getSections(school.getId());
        ConcurrentHashMap<Long, Section> sectionMap = new ConcurrentHashMap<>();
        for(Section s : sections) {
            Long id = null;
            String ssid = s.getSourceSystemId();
            if(null != ssid) {
                id = Long.valueOf(ssid);
                sectionMap.put(id, s);
            }
        }
        return sectionMap;
    }

//    private void migrateStudentAssignmentGrades(Term sectionTerm, Section createdSection) {
//        //first resolve the assignment categories, so we can construct the appropriate EdPanel assignment subclass
//        PGAssignmentTypes powerTypes = powerSchool.getAssignmentTypesBySectionId(Long.valueOf(createdSection.getSourceSystemId()));
//        Map<Long, PsAssignmentType> typeIdToType = new HashMap<>();
//        if(null != powerTypes && null != powerTypes.record) {
//            for (PGAssignmentType pat: powerTypes.record) {
//                if(null != pat.tables && null != pat.tables.pgcategories) {
//                    typeIdToType.put(
//                            Long.valueOf(pat.tables.pgcategories.getId()),
//                            pat.tables.pgcategories);
//                }
//            }
//        }
//        //Now iterate over all the assignments and construct the correct type of EdPanel assignment
//        PGAssignments powerAssignments = powerSchool.getAssignmentsBySectionId(Long.valueOf(createdSection.getSourceSystemId()));
//
//        //Get the association between student section score ID and student ID
//        SectionScoreIdsResponse ssids = powerSchool.getStudentScoreIdsBySectionId(
//                Long.valueOf(createdSection.getSourceSystemId()));
//        Map<Long, MutablePair<Student, PsSectionScoreId>> ssidToStudent = new HashMap<>();
//        if(null != ssids && null != ssids.record) {
//            for(PsSectionScoreIds ssid: ssids.record) {
//                PsSectionScoreId i = ssid.tables.sectionscoresid;
//                Long ssidId = Long.valueOf(i.getDcid());
//                Student stud = studentAssociator.findBySourceSystemId(Long.valueOf(i.getStudentid()));
//                if(null == stud) {
//                    stud = migrateMissingStudent(school.getId(), Long.valueOf(i.getStudentid()));
//                }
//                if(null != stud) {
//                    ssidToStudent.put(ssidId, new MutablePair<>(stud, i));
//                } else {
//                    //TODO: log? no op?
//                }
//            }
//        }
//
//        //THREADING BU SECTION ASSIGNMENT -> STUDENT ASSIGNMENT
//        this.sections = new ConcurrentHashMap<>();
//        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
//        if(null != powerAssignments && null != powerAssignments.record) {
//            for (PGAssignment powerAssignment : powerAssignments.record) {
//                StudentAssignmentSyncRunnable runnable = new StudentAssignmentSyncRunnable(
//                        powerSchool,
//                        edPanel,
//                        school,
//                        sectionTerm,
//                        createdSection,
//                        powerAssignment,
//                        typeIdToType,
//                        ssidToStudent
//                );
//                executor.execute(runnable);
//            }
//        }
//        executor.shutdown();
//        //Spin while we wait for all the threads to complete
//        while(!executor.isTerminated()){}
//    }

    /**
     * Sadly, it is possible for a student not returned by the PowerSchool API /schools/:id/students
     * to end up enrolled in a Section. In these cases, we need to go fetch the student and create
     * him/her/it/them ad hoc in edpanel in order to enroll them in the section.  Returns null if the
     * user cannot be retrieved from PowerSchool.
     * @param schoolId
     * @param powerSchoolStudentId
     */
//    private Student migrateMissingStudent(Long schoolId, Long powerSchoolStudentId) {
//        StudentResponse powerStudent = null;
//
//        try {
//            powerStudent = powerSchool.getStudentById(powerSchoolStudentId);
//        } catch(HttpClientException e) {
//            //Cache the unresolvable student ID for error reporting
//            if(null == unresolvablePowerStudents) {
//                unresolvablePowerStudents = Collections.synchronizedList(new ArrayList<>());
//            }
//            unresolvablePowerStudents.add(powerSchoolStudentId);
//            return null;
//        }
//        PsStudents students = new PsStudents();
//        students.add(powerStudent.student);
//        Collection<Student> studs = students.toInternalModel();
//        for(Student edpanelStudent : studs) {
//            edpanelStudent.setCurrentSchoolId(schoolId);
//            Student createdStudent = edPanel.createStudent(edpanelStudent);
//            ConcurrentHashMap<Long, Student> studMap = new ConcurrentHashMap<>();
//            try {
//                Long otherId = Long.valueOf(createdStudent.getSourceSystemUserId());
//                Long ssid = Long.valueOf(createdStudent.getSourceSystemId());
//                studentAssociator.associateIds(ssid, otherId);
//                studMap.put(otherId, createdStudent);
//                studentAssociator.addOtherIdMap(studMap);
//            } catch(NumberFormatException e) {
//                //NO OP
//            }
//            return createdStudent;
//        }
//        return null;
//    }
}
