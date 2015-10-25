package com.scholarscore.etl;

import com.google.gson.JsonSyntaxException;
import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.powerschool.api.model.PsCourses;
import com.scholarscore.etl.powerschool.api.model.PsSection;
import com.scholarscore.etl.powerschool.api.model.PsSectionEnrollment;
import com.scholarscore.etl.powerschool.api.model.PsStaffs;
import com.scholarscore.etl.powerschool.api.model.PsStudents;
import com.scholarscore.etl.powerschool.api.model.PsTerm;
import com.scholarscore.etl.powerschool.api.model.assignment.PGAssignment;
import com.scholarscore.etl.powerschool.api.model.assignment.PGAssignments;
import com.scholarscore.etl.powerschool.api.model.assignment.PsAssignment;
import com.scholarscore.etl.powerschool.api.model.assignment.PsAssignmentFactory;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PsAssignmentScores;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PsScore;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PsSectionScoreId;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PsSectionScoreIds;
import com.scholarscore.etl.powerschool.api.model.assignment.type.PGAssignmentType;
import com.scholarscore.etl.powerschool.api.model.assignment.type.PGAssignmentTypes;
import com.scholarscore.etl.powerschool.api.model.assignment.type.PsAssignmentType;
import com.scholarscore.etl.powerschool.api.model.section.PsSectionGrade;
import com.scholarscore.etl.powerschool.api.model.section.PsSectionGrades;
import com.scholarscore.etl.powerschool.api.response.AssignmentScoresResponse;
import com.scholarscore.etl.powerschool.api.response.SchoolsResponse;
import com.scholarscore.etl.powerschool.api.response.SectionEnrollmentsResponse;
import com.scholarscore.etl.powerschool.api.response.SectionGradesResponse;
import com.scholarscore.etl.powerschool.api.response.SectionResponse;
import com.scholarscore.etl.powerschool.api.response.SectionScoreIdsResponse;
import com.scholarscore.etl.powerschool.api.response.StudentResponse;
import com.scholarscore.etl.powerschool.api.response.TermResponse;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.models.assignment.Assignment;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.assignment.StudentAssignment;
import com.scholarscore.models.StudentSectionGrade;
import com.scholarscore.models.Term;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.Teacher;
import com.scholarscore.models.user.User;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This is the E2E flow for powerschool import to edPanel export - we have references to both clients and
 * can invoke get API's from powerschool and POST (create) API's from edPanel.  We assume for now that we'll seek
 * out entities from the scholarscore database before inserting them into the database rather than assuming a trash
 * and burn strategy.
 *
 * Created by mattg on 7/3/©5.
 */
public class ETLEngine implements IETLEngine {

    private IPowerSchoolClient powerSchool;
    private IAPIClient edPanel;
    private List<School> schools;
    private List<SchoolYear> schoolYears;
    //SourceSystemStudentId to student
    private Map<Long, Student> students;
    //Collections are by sourceSystemSchoolId and if there are nested maps, 
    //the keys are always sourceSystemIds of sub-entities
    private Map<Long, Map<Long, Term>> terms;
    private Map<Long, Map<Long, Section>> sections;
    private Map<Long, Map<Long, Course>> courses;
    private Map<Long, Map<Long, User>> staff;

    //Error state collections
    private List<PsSectionScoreId> sectionScoreIdsUnresolvableStudent;
    private List<Long> unresolvablePowerStudents;

    public void setPowerSchool(IPowerSchoolClient powerSchool) {
        this.powerSchool = powerSchool;
    }

    public IPowerSchoolClient getPowerSchool() {
        return powerSchool;
    }

    public void setEdPanel(IAPIClient edPanel) {
        this.edPanel = edPanel;
    }

    public IAPIClient getEdPanel() {
        return edPanel;
    }

    @Override
    public MigrationResult migrateDistrict() {
        MigrationResult result = new MigrationResult();
        createSchools();
        migrateSchoolYearsAndTerms();
        createStaff();
        createStudents();
        createCourses();
        migrateSections();
        return result;
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
                unresolvablePowerStudents = new ArrayList<>();
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
            this.students.put(powerSchoolStudentId, createdStudent);
            return createdStudent;
        }
        return null;
    }

    /**
     * For each school in this.schools, resolve all the sections and create an EdPanel Section instance for each.
     * For each EdPanel Section instance, resolve and set the appropriate enrolled student IDs, course ID, teacher(s) ID,
     * PsTerm ID, Assignments, and GradeFormula.  After these dependencies are resolve, call the EdPanel API to create the Section
     * and the assignments.  Returns void but populates this.sections with all sections created and includes the collection of
     * assignments on each section.
     */
    private void migrateSections() {
        if(null != schools) {
            this.sections = new HashMap<>();
            for(School s: schools) {
                String sourceSystemIdString = s.getSourceSystemId();
                Long sourceSystemSchoolId = new Long(sourceSystemIdString);
                SectionResponse sr = powerSchool.getSectionsBySchoolId(sourceSystemSchoolId);
                if(null != sr && null != sr.sections && null != sr.sections.section) {
                    List<PsSection> powerSchoolSections
                            = sr.sections.section;
                    for(PsSection powerSection: powerSchoolSections) {
                        //Create an EdPanel section
                        Section edpanelSection = new Section();
                        edpanelSection.setSourceSystemId(powerSection.getId().toString());

                        //Resolve the EdPanel Course and set it on the EdPanel section
                        Course c = this.courses.
                                get(sourceSystemSchoolId).
                                get(Long.valueOf(powerSection.getCourse_id()));
                        if(null != c) {
                            edpanelSection.setCourse(c);
                            edpanelSection.setName(c.getName());
                        }

                        //Resolve the EdPanel Term and set it on the Section
                        Term sectionTerm = this.terms.get(sourceSystemSchoolId).get(powerSection.getTerm_id());
                        edpanelSection.setTerm(sectionTerm);
                        edpanelSection.setStartDate(sectionTerm.getStartDate());
                        edpanelSection.setEndDate(sectionTerm.getEndDate());

                        //Resolve the EdPanel Teacher(s) and set on the Section
                        User t = this.staff.get(sourceSystemSchoolId).get(powerSection.getStaff_id());
                        if(null != t && t instanceof Teacher) {
                            HashSet<Teacher> teachers = new HashSet<>();
                            teachers.add((Teacher) t);
                            edpanelSection.setTeachers(teachers);
                        }

                        //CREATE THE SECTION WITHIN EDPANEL:
                        //TODO: Resolve the grade formula and set it on the Section
                        Section createdSection = edPanel.createSection(
                                s.getId(), 
                                edpanelSection.getTerm().getSchoolYear().getId(),
                                edpanelSection.getTerm().getId(), 
                                edpanelSection);
                        if(null == this.sections.get(sourceSystemSchoolId)) {
                            this.sections.put(sourceSystemSchoolId, new HashMap<>());
                        }
                        this.sections.get(sourceSystemSchoolId).put(new Long(createdSection.getSourceSystemId()), createdSection);
                        
                        //CREATE ENROLLED STUDENTS' STUDENTSECTIONGRADE INSTANCES
                        //Resolve enrolled students & Create an EdPanel StudentSectionGrade for each
                        SectionEnrollmentsResponse enrollments = null;
                        try {
                            enrollments = powerSchool.getEnrollmentBySectionId(powerSection.getId());
                        } catch(JsonSyntaxException e) {
                            //TODO: if a single record comes back, PowerSchool doesn't send an array and the marshalling fails :(
                            System.out.println("failed to unmarshall section enrollments: " + e.getMessage());
                        }
                        List<StudentSectionGrade> ssgs = new ArrayList<>();
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
                                Student edpanelStudent = this.students.get(se.getStudent_id());
                                if(null == edpanelStudent) {
                                    edpanelStudent = migrateMissingStudent(s.getId(), se.getStudent_id());
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
                                            s.getId(), 
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
                        edpanelSection.setStudentSectionGrades(ssgs);
                        
                        //CREATE THE ASSIGNMENTS FOR THE SECTION:
                        //first resolve the assignment categories, so we can construct the appropriate EdPanel assignment subclass
                        PGAssignmentTypes powerTypes = powerSchool.getAssignmentTypesBySectionId(powerSection.getId());
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
                        PGAssignments powerAssignments = powerSchool.getAssignmentsBySectionId(powerSection.getId());

                        //Get the association between student section score ID and student ID
                        SectionScoreIdsResponse ssids = powerSchool.getStudentScoreIdsBySectionId(
                                Long.valueOf(createdSection.getSourceSystemId()));
                        Map<Long, MutablePair<Student, PsSectionScoreId>> ssidToStudent = new HashMap<>();
                        if(null != ssids && null != ssids.record) {
                            for(PsSectionScoreIds ssid: ssids.record) {
                                PsSectionScoreId i = ssid.tables.sectionscoresid;
                                Long ssidId = Long.valueOf(i.getDcid());
                                Student stud = this.students.get(Long.valueOf(i.getStudentid()));
                                if(null == stud) {
                                    stud = migrateMissingStudent(s.getId(), Long.valueOf(i.getStudentid()));
                                }
                                if(null != stud) {
                                    ssidToStudent.put(ssidId, new MutablePair<>(stud, i));
                                } else {
                                    //Cache the sectionscoreID that has an unresolvable student for error reporting
                                    if(null == sectionScoreIdsUnresolvableStudent) {
                                        sectionScoreIdsUnresolvableStudent = new ArrayList<>();
                                    }
                                    sectionScoreIdsUnresolvableStudent.add(i);
                                }
                            }
                        }
                        //Create each assignment in EdPanel and all student scores for each assignment
                        if(null != powerAssignments && null != powerAssignments.record) {
                            for(PGAssignment powerAssignment : powerAssignments.record) {
                                PsAssignment pa = powerAssignment.tables.pgassignments;
                                Assignment edpanelAssignment = PsAssignmentFactory.fabricate(
                                        pa,
                                        typeIdToType.get(Long.valueOf(pa.getPgcategoriesid())));
                                edpanelAssignment.setSection(createdSection);
                                edpanelAssignment.setSectionFK(createdSection.getId());
                                Assignment createdAssignment = edPanel.createSectionAssignment(
                                        s.getId(),
                                        sectionTerm.getSchoolYear().getId(),
                                        sectionTerm.getId(),
                                        createdSection.getId(),
                                        edpanelAssignment);
                                //Retrieve students' scores
                                AssignmentScoresResponse assScores =
                                        powerSchool.getStudentScoresByAssignmentId(Long.valueOf(pa.getDcid()));
                                if(null != assScores && null != assScores.record) {

                                    List<StudentAssignment> studentAssignmentsToCreate = new ArrayList<>();

                                    for(PsAssignmentScores sc : assScores.record) {
                                        PsScore score = sc.tables.sectionscoresassignments;
                                        StudentAssignment studAss = new StudentAssignment();
                                        studAss.setAssignment(createdAssignment);
                                        //Resolve the student, or move on
                                        MutablePair<Student, PsSectionScoreId> sectionScoreIdAndStudent =
                                                ssidToStudent.get(Long.valueOf(score.getFdcid()));
                                        if(null != sectionScoreIdAndStudent) {
                                            studAss.setStudent(sectionScoreIdAndStudent.getLeft());
                                        } else {
                                            continue;
                                        }
                                        //Resolve the points
                                        Double awardedPoints = null;
                                        try {
                                            awardedPoints = Double.valueOf(score.getScore());
                                        } catch (NumberFormatException e) {
                                            //NO OP
                                        }
                                        if(null == awardedPoints) {
                                            studAss.setCompleted(false);
                                        } else {
                                            studAss.setAwardedPoints(awardedPoints);
                                            studAss.setCompleted(true);
                                        }
                                        studentAssignmentsToCreate.add(studAss);
                                    }
                                    //We've now generated an EdPanel StudentAssignment for each PS student assignment
                                    //Call the bulk create for student assignments for this assignment.
                                    if(!studentAssignmentsToCreate.isEmpty()) {
                                        edPanel.createStudentAssignments(
                                                s.getId(),
                                                sectionTerm.getSchoolYear().getId(),
                                                sectionTerm.getId(),
                                                createdSection.getId(),
                                                createdAssignment.getId(),
                                                studentAssignmentsToCreate);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Creates the all school years and terms for each of the schools on the instance
     * collection this.schools.  Returns void but populates the collections this.terms
     * and this.schoolYears as part of execution.
     */
    private void migrateSchoolYearsAndTerms() {
        if(null != schools) {
            this.terms = new HashMap<>();
            this.schoolYears = new ArrayList<SchoolYear>();
            for(School s: schools) {
                //Get all the terms from PowerSchool for the current School
                String sourceSystemIdString = s.getSourceSystemId();
                Long sourceSystemSchoolId = new Long(sourceSystemIdString);
                TermResponse tr = powerSchool.getTermsBySchoolId(sourceSystemSchoolId);
                if(null != tr && null != tr.terms && null != tr.terms.term) {
                    Map<Long, List<Term>> yearToTerms = new HashMap<>();
                    Map<Long, SchoolYear> edPanelYears = new HashMap<>();
                    List<PsTerm> terms = tr.terms.term;
                    //First we build up the term and deduce from this, how many school years there are...
                    for(PsTerm t : terms) {
                        Term edpanelTerm = new Term();
                        edpanelTerm.setStartDate(t.getStart_date());
                        edpanelTerm.setEndDate(t.getEnd_date());
                        edpanelTerm.setName(t.getName());
                        edpanelTerm.setSourceSystemId(t.getId().toString());
                        if(null == yearToTerms.get(t.getStart_year())) {
                            yearToTerms.put(t.getStart_year(), new ArrayList<>());
                        }
                        yearToTerms.get(t.getStart_year()).add(edpanelTerm);
                    }     
                    //Then we create the needed school years in EdPanel given the terms from PowerSchool
                    Iterator<Map.Entry<Long, List<Term>>> it = 
                            yearToTerms.entrySet().iterator();
                    while(it.hasNext()) {
                        Map.Entry<Long, List<Term>> entry = it.next();
                        SchoolYear schoolYear = new SchoolYear();
                        schoolYear.setSchool(s);
                        schoolYear.setName(entry.getKey().toString());
                        //For each school year, we need to set the start & end dates as the smallest 
                        //of the terms' start dates and the largest of the terms' end dates
                        for(Term t: entry.getValue()) {
                            if(null == schoolYear.getStartDate() || 
                                    schoolYear.getStartDate().compareTo(t.getStartDate()) > 0) {
                                schoolYear.setStartDate(t.getStartDate());
                            }
                            if(null == schoolYear.getEndDate() || 
                                    schoolYear.getEndDate().compareTo(t.getEndDate()) < 0) {
                                schoolYear.setEndDate(t.getEndDate());
                            }
                        }
                        //Create the school year in EdPanel!
                        SchoolYear createdSchoolYear = edPanel.createSchoolYear(s.getId(), schoolYear);
                        this.schoolYears.add(createdSchoolYear);
                        edPanelYears.put(entry.getKey(), createdSchoolYear);
                    }
                    //Finally, having created the EdPanel SchoolYears, we can create the terms in EdPanel
                    it = yearToTerms.entrySet().iterator();
                    while(it.hasNext()) {
                        Map.Entry<Long, List<Term>> entry = it.next();
                        for(Term t: entry.getValue()) {
                            //Now that the school Year has been created, cache it on the 
                            SchoolYear y = edPanelYears.get(entry.getKey());
                            t.setSchoolYear(y);
                            //Create the term in EdPanel!
                            Term createdTerm = edPanel.createTerm(s.getId(), y.getId(), t);
                            if(null == this.terms.get(sourceSystemSchoolId)) {
                                this.terms.put(sourceSystemSchoolId, new HashMap<>());
                            }
                            this.terms.get(sourceSystemSchoolId).put(new Long(createdTerm.getSourceSystemId()), createdTerm);
                        }
                    }
                }
            }
        }
    }
    
    private void createCourses() {

        Map<Long, Map<Long, Course>> result = new HashMap<>();
        for (School school : schools) {
            Long schoolId = Long.valueOf(school.getSourceSystemId());
            result.put(schoolId, new HashMap<>());
            PsCourses response = powerSchool.getCoursesBySchool(schoolId);
            Collection<Course> apiListOfCourses = response.toInternalModel();
            for(Course c: apiListOfCourses) {
                result.get(schoolId).put(
                        Long.valueOf(c.getSourceSystemId()),
                        edPanel.createCourse(school.getId(), c));
            }
        }
        this.courses = result;
    }

    private void createStudents() {
        Map<Long, Student> studentsBySchoolAndId = new HashMap<>();
        for (School school : schools) {
            Long schoolId = Long.valueOf(school.getSourceSystemId());
            PsStudents response = powerSchool.getStudentsBySchool(schoolId);
            Collection<Student> apiListOfStudents = response.toInternalModel();

            List<Student> students = new ArrayList<>();
            apiListOfStudents.forEach(student -> {
                student.setCurrentSchoolId(school.getId());
                Student createdStudent = edPanel.createStudent(student);
                student.setId(createdStudent.getId());
                studentsBySchoolAndId.put(new Long(student.getSourceSystemId()), student);
            });
        }
        this.students = studentsBySchoolAndId;
    }

    /**
     * Create the user entry along side the teacher and administrator entries
     * @return
     */
    public void createStaff() {
        Map<Long, Map<Long, User>> staffBySchool = new HashMap<>();
        for (School school : schools) {
            Long psSchoolId = new Long(school.getSourceSystemId());
            PsStaffs response = powerSchool.getStaff(Long.valueOf(school.getSourceSystemId()));
            List<User> apiListOfStaff = response.toInternalModel();
            apiListOfStaff.forEach(staff -> {
                staff.setPassword(UUID.randomUUID().toString());
                try {
                    User result = edPanel.createUser(staff);
                    staff.setId(result.getId());
                    if (null == staffBySchool.get(psSchoolId)) {
                        staffBySchool.put(psSchoolId, new HashMap<>());
                    }
                    staffBySchool.get(psSchoolId).put(
                            Long.valueOf(staff.getSourceSystemId()), staff);
                } catch (Exception e) {
                }
            });
        }
       this.staff = staffBySchool;
    }

    public void createSchools() {
        SchoolsResponse powerSchools = powerSchool.getSchools();
        List<School> schools = (List<School>) powerSchools.toInternalModel();
        for (School school : schools) {
            School response = edPanel.createSchool(school);
            school.setId(response.getId());
        }
        this.schools = schools;
    }
}
