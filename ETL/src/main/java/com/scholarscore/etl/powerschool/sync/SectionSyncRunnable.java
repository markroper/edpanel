package com.scholarscore.etl.powerschool.sync;

import com.google.gson.JsonSyntaxException;
import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.StudentAssignmentETLRunnable;
import com.scholarscore.etl.powerschool.api.model.PsSection;
import com.scholarscore.etl.powerschool.api.model.PsSectionEnrollment;
import com.scholarscore.etl.powerschool.api.model.PsStudents;
import com.scholarscore.etl.powerschool.api.model.assignment.PGAssignment;
import com.scholarscore.etl.powerschool.api.model.assignment.PGAssignments;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PsSectionScoreId;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PsSectionScoreIds;
import com.scholarscore.etl.powerschool.api.model.assignment.type.PGAssignmentType;
import com.scholarscore.etl.powerschool.api.model.assignment.type.PGAssignmentTypes;
import com.scholarscore.etl.powerschool.api.model.assignment.type.PsAssignmentType;
import com.scholarscore.etl.powerschool.api.model.section.PsSectionGrade;
import com.scholarscore.etl.powerschool.api.model.section.PsSectionGrades;
import com.scholarscore.etl.powerschool.api.response.SectionEnrollmentsResponse;
import com.scholarscore.etl.powerschool.api.response.SectionGradesResponse;
import com.scholarscore.etl.powerschool.api.response.SectionResponse;
import com.scholarscore.etl.powerschool.api.response.SectionScoreIdsResponse;
import com.scholarscore.etl.powerschool.api.response.StudentResponse;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.etl.powerschool.sync.associators.StaffAssociator;
import com.scholarscore.etl.powerschool.sync.associators.StudentAssociator;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.Section;
import com.scholarscore.models.StudentSectionGrade;
import com.scholarscore.models.Term;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.Teacher;
import com.scholarscore.models.user.User;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
            //TODO: enable and debug these ;)
//            migrateStudentSectionEnrollmentAndGrades(sourceSection.getTerm(), sourceSection);
//            migrateStudentAssignmentGrades(sourceSection.getTerm(), sourceSection);
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

    private void migrateStudentSectionEnrollmentAndGrades(Term sectionTerm, Section createdSection) {
        //Resolve enrolled students & Create an EdPanel StudentSectionGrade for each
        SectionEnrollmentsResponse enrollments = null;
        try {
            enrollments = powerSchool.getEnrollmentBySectionId(Long.valueOf(createdSection.getSourceSystemId()));
        } catch(JsonSyntaxException e) {
            //TODO: if a single record comes back, PowerSchool doesn't send an array and the marshalling fails :(
            System.out.println("failed to unmarshall section enrollments: " + e.getMessage());
        }
        List<StudentSectionGrade> ssgs = Collections.synchronizedList(new ArrayList<>());
        if(null != enrollments && null != enrollments.section_enrollments
                && null != enrollments.section_enrollments.section_enrollment) {

            //See if any final grades have been created for the section, and if so, retrieve them
            Map<Long, PsSectionGrade> studentIdToSectionScore = null;
            if(createdSection.getEndDate().compareTo(new Date()) < 0) {
                SectionGradesResponse sectScores = powerSchool.getSectionScoresBySectionId(
                        Long.valueOf(createdSection.getSourceSystemId()));
                if(null != sectScores && null != sectScores.record) {
                    studentIdToSectionScore = new HashMap<>();
                    for(PsSectionGrades ss: sectScores.record) {
                        PsSectionGrade score = ss.tables.storedgrades;
                        studentIdToSectionScore.put(
                                Long.valueOf(score.getStudentid()),
                                score);
                    }
                }
            }
            //Create an EdPanel StudentSectionGrade for each PowerSchool StudentEnrollment
            for(PsSectionEnrollment se : enrollments.section_enrollments.section_enrollment) {
                Student edpanelStudent = studentAssociator.findBySourceSystemId(se.getStudent_id());
                if(null == edpanelStudent) {
                    edpanelStudent = migrateMissingStudent(school.getId(), se.getStudent_id());
                }
                if(null != se && null != edpanelStudent) {
                    StudentSectionGrade ssg = new StudentSectionGrade();
                    ssg.setStudent(edpanelStudent);
                    ssg.setSection(createdSection);
                    if(null != studentIdToSectionScore) {
                        PsSectionGrade score = studentIdToSectionScore.get(
                                Long.valueOf(edpanelStudent.getSourceSystemId()));
                        Double pct = score.getPercent();
                        ssg.setGrade(pct);
                        ssg.setComplete(true);
                    } else {
                        ssg.setComplete(false);
                    }
                    //CREATE THE ENROLLMENT IN EDPANEL
                    StudentSectionGrade createdSsg = null;
                    try {
                        createdSsg = edPanel.createStudentSectionGrade(
                                school.getId(),
                                sectionTerm.getSchoolYear().getId(),
                                sectionTerm.getId(),
                                createdSection.getId(),
                                edpanelStudent.getId(),
                                ssg);
                        ssgs.add(createdSsg);
                    } catch (HttpClientException e) {
                        System.out.println("failed to create ssg!");
                    }
                }
            }
        }
        createdSection.setStudentSectionGrades(ssgs);
    }

    private void migrateStudentAssignmentGrades(Term sectionTerm, Section createdSection) {
        //first resolve the assignment categories, so we can construct the appropriate EdPanel assignment subclass
        PGAssignmentTypes powerTypes = powerSchool.getAssignmentTypesBySectionId(Long.valueOf(createdSection.getSourceSystemId()));
        Map<Long, PsAssignmentType> typeIdToType = new HashMap<>();
        if(null != powerTypes && null != powerTypes.record) {
            for (PGAssignmentType pat: powerTypes.record) {
                if(null != pat.tables && null != pat.tables.pgcategories) {
                    typeIdToType.put(
                            Long.valueOf(pat.tables.pgcategories.getId()),
                            pat.tables.pgcategories);
                }
            }
        }
        //Now iterate over all the assignments and construct the correct type of EdPanel assignment
        PGAssignments powerAssignments = powerSchool.getAssignmentsBySectionId(Long.valueOf(createdSection.getSourceSystemId()));

        //Get the association between student section score ID and student ID
        SectionScoreIdsResponse ssids = powerSchool.getStudentScoreIdsBySectionId(
                Long.valueOf(createdSection.getSourceSystemId()));
        Map<Long, MutablePair<Student, PsSectionScoreId>> ssidToStudent = new HashMap<>();
        if(null != ssids && null != ssids.record) {
            for(PsSectionScoreIds ssid: ssids.record) {
                PsSectionScoreId i = ssid.tables.sectionscoresid;
                Long ssidId = Long.valueOf(i.getDcid());
                Student stud = studentAssociator.findBySourceSystemId(Long.valueOf(i.getStudentid()));
                if(null == stud) {
                    stud = migrateMissingStudent(school.getId(), Long.valueOf(i.getStudentid()));
                }
                if(null != stud) {
                    ssidToStudent.put(ssidId, new MutablePair<>(stud, i));
                } else {
                    //TODO: log? no op?
                }
            }
        }

        //THREADING BU SECTION ASSIGNMENT -> STUDENT ASSIGNMENT
        this.sections = new ConcurrentHashMap<>();
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        if(null != powerAssignments && null != powerAssignments.record) {
            for (PGAssignment powerAssignment : powerAssignments.record) {
                StudentAssignmentETLRunnable runnable = new StudentAssignmentETLRunnable(
                        powerSchool,
                        edPanel,
                        school,
                        sectionTerm,
                        createdSection,
                        powerAssignment,
                        typeIdToType,
                        ssidToStudent
                );
                executor.execute(runnable);
            }
        }
        executor.shutdown();
        //Spin while we wait for all the threads to complete
        while(!executor.isTerminated()){}
    }

    /**
     * Sadly, it is possible for a student not returned by the PowerSchool API /schools/:id/students
     * to end up enrolled in a Section. In these cases, we need to go fetch the student and create
     * him/her/it/them ad hoc in edpanel in order to enroll them in the section.  Returns null if the
     * user cannot be retrieved from PowerSchool.
     * @param schoolId
     * @param powerSchoolStudentId
     */
    private Student migrateMissingStudent(Long schoolId, Long powerSchoolStudentId) {
        StudentResponse powerStudent = null;

        try {
            powerStudent = powerSchool.getStudentById(powerSchoolStudentId);
        } catch(HttpClientException e) {
            //Cache the unresolvable student ID for error reporting
            if(null == unresolvablePowerStudents) {
                unresolvablePowerStudents = Collections.synchronizedList(new ArrayList<>());
            }
            unresolvablePowerStudents.add(powerSchoolStudentId);
            return null;
        }
        PsStudents students = new PsStudents();
        students.add(powerStudent.student);
        Collection<Student> studs = students.toInternalModel();
        for(Student edpanelStudent : studs) {
            edpanelStudent.setCurrentSchoolId(schoolId);
            Student createdStudent = edPanel.createStudent(edpanelStudent);
            ConcurrentHashMap<Long, Student> studMap = new ConcurrentHashMap<>();
            studMap.put(powerSchoolStudentId, createdStudent);
            try {
                Long otherId = Long.valueOf(createdStudent.getSourceSystemUserId());
                Long ssid = Long.valueOf(createdStudent.getSourceSystemId());
                studentAssociator.associateIds(ssid, otherId);
            } catch(NumberFormatException e) {
                //NO OP
            }
            studentAssociator.addOtherIdMap(studMap);
            return createdStudent;
        }
        return null;
    }
}
