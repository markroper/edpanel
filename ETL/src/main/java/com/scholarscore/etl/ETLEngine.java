package com.scholarscore.etl;

import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.powerschool.api.model.*;
import com.scholarscore.etl.powerschool.api.model.assignment.PGAssignment;
import com.scholarscore.etl.powerschool.api.model.assignment.PGAssignments;
import com.scholarscore.etl.powerschool.api.response.SchoolsResponse;
import com.scholarscore.etl.powerschool.api.response.SectionEnrollmentsResponse;
import com.scholarscore.etl.powerschool.api.response.SectionResponse;
import com.scholarscore.etl.powerschool.api.response.TermResponse;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.models.*;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.Section;
import com.scholarscore.models.Term;
import com.scholarscore.models.user.Administrator;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.Teacher;
import com.scholarscore.models.user.User;

import java.util.*;

import org.apache.commons.lang3.tuple.MutablePair;

/**
 * This is the E2E flow for powerschool import to scholarScore export - we have references to both clients and
 * can invoke get API's from powerschool and POST (create) API's from scholarScore.  We assume for now that we'll seek
 * out entities from the scholarscore database before inserting them into the database rather than assuming a trash
 * and burn strategy.
 *
 * Created by mattg on 7/3/15.
 */
public class ETLEngine implements IETLEngine {

    private IPowerSchoolClient powerSchool;
    private IAPIClient scholarScore;
    private List<com.scholarscore.models.School> schools;
    private List<com.scholarscore.models.SchoolYear> schoolYears;
    
    //Collections are by sourceSystemSchoolId and if there are nested maps, 
    //the keys are always sourceSystemIds of sub-entities
    private Map<Long, Map<Long, com.scholarscore.models.Term>> terms;
    private Map<Long, Map<Long, com.scholarscore.models.Section>> sections;
    private Map<Long, List<Course>> courses;
    private Map<Long, List<User>> staff;
    private Map<Long, Map<Long, Student>> students;

    public void setPowerSchool(IPowerSchoolClient powerSchool) {
        this.powerSchool = powerSchool;
    }

    public IPowerSchoolClient getPowerSchool() {
        return powerSchool;
    }

    public void setScholarScore(IAPIClient scholarScore) {
        this.scholarScore = scholarScore;
    }

    public IAPIClient getScholarScore() {
        return scholarScore;
    }

    @Override
    public MigrationResult migrateDistrict() {
        MigrationResult result = new MigrationResult();
        this.schools = createSchools();
        migrateSchoolYearsAndTerms();  
        this.staff = createStaff();
        this.students = createStudents();
        this.courses = createCourses();
        migrateSections();
        return result;
    }
    
    /**
     * For each school in this.schools, resolve all the sections and create an EdPanel Section instance for each.
     * For each EdPanel Section instance, resolve and set the appropriate enrolled student IDs, course ID, teacher(s) ID,
     * Term ID, Assignments, and GradeFormula.  After these dependencies are resolve, call the EdPanel API to create the Section
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
                    List<com.scholarscore.etl.powerschool.api.model.Section> powerSchoolSections 
                            = sr.sections.section;
                    for(com.scholarscore.etl.powerschool.api.model.Section powerSection: powerSchoolSections) {
                        //Create an EdPanel section
                        com.scholarscore.models.Section edpanelSection = new com.scholarscore.models.Section();
                        edpanelSection.setSourceSystemId(powerSection.getId().toString());  
                        //Resolve the EdPanel CourseID and set it on the EdPanel section
                        //TODO: use a map of maps for O(1) lookup
                        for(Course c: this.courses.get(new Long(s.getSourceSystemId()))) {
                            if(c.getSourceSystemId().equals(powerSection.getCourse_id())) {
                                edpanelSection.setCourse(c);
                                edpanelSection.setName(c.getName());
                                break;
                            }
                        }     
                        //Resolve the EdPanel TermID and set it on the Section
                        Term sectionTerm = this.terms.get(sourceSystemSchoolId).get(new Long(powerSection.getTerm_id()));
                        edpanelSection.setTerm(sectionTerm);
                        edpanelSection.setStartDate(sectionTerm.getStartDate());
                        edpanelSection.setEndDate(sectionTerm.getEndDate());
                        //Resolve the EdPanel teacher(s) for the section and set it on the Section
                        //TODO: use a map of maps to make this an O(1) lookup
                        for(User u : this.staff.get(sourceSystemSchoolId)) {
                            if(u instanceof Teacher && 
                                    u.getSourceSystemId().equals(powerSection.getStaff_id().toString())) {
                                HashSet<Teacher> teachers = new HashSet<>();
                                teachers.add((Teacher)u);
                                edpanelSection.setTeachers(teachers);
                                break;
                            }
                        }
                 
                        //TODO: Resolve the grade formula and set it on the Section
                        //CREATE THE SECTION WITHIN EDPANEL:
                        Section createdSection = scholarScore.createSection(
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
                        SectionEnrollmentsResponse enrollments = powerSchool.getEnrollmentBySectionId(powerSection.getId());
                        List<StudentSectionGrade> ssgs = new ArrayList<>();
                        if(null != enrollments && null != enrollments.section_enrollments 
                                && null != enrollments.section_enrollments.section_enrollment) {
                            for(SectionEnrollment se : enrollments.section_enrollments.section_enrollment) {
                                Student edpanelStudent = this.students.get(new Long(s.getSourceSystemId())).get(se.getStudent_id());
                                if(null != se) {
                                    StudentSectionGrade ssg = new StudentSectionGrade();
                                    ssg.setStudent(edpanelStudent);
                                    ssg.setSection(edpanelSection);
                                    if(createdSection.getEndDate().compareTo(new Date()) < 0) {
                                        //TODO: figure out if the section is over and if so, resolve the student grade
                                        ssg.setComplete(true);
                                    } else {
                                        ssg.setComplete(false);
                                    }
                                    //CREATE THE ENROLLMENT IN EDPANEL
//                                    scholarScore.createStudentSectionGrade(
//                                            s.getId(), 
//                                            sectionTerm.getSchoolYear().getId(),
//                                            sectionTerm.getId(),
//                                            createdSection.getId(),
//                                            edpanelStudent.getId(),
//                                            ssg);
                                    ssgs.add(ssg);
                                }
                            }
                        }
                        edpanelSection.setStudentSectionGrades(ssgs);
                        
                        //CREATE THE ASSIGNMENTS FOR THE SECTION:
                        //TODO: first resolve the assignment categories, so we can construct the appropriate EdPanel assignment subclass
                        //Now iterate over all the assignments and construct the correct type of EdPanel assignment
                        PGAssignments powerAssignments = powerSchool.getAssignmentsBySectionId(powerSection.getId());
                        if(null != powerAssignments && null != powerAssignments.records) {
                            for(PGAssignment powerAssignment : powerAssignments.records) {
                                com.scholarscore.etl.powerschool.api.model.assignment.Assignment pa = 
                                        powerAssignment.tables.pgassignments;
                                //TODO: impl toEdPanelModel()...
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
                    Map<Long, List<com.scholarscore.models.Term>> yearToTerms = new HashMap<>();
                    Map<Long, com.scholarscore.models.SchoolYear> edPanelYears = new HashMap<>();
                    List<com.scholarscore.etl.powerschool.api.model.Term> terms = tr.terms.term;
                    //First we build up the term and deduce from this, how many school years there are...
                    for(com.scholarscore.etl.powerschool.api.model.Term t : terms) {
                        com.scholarscore.models.Term edpanelTerm = new com.scholarscore.models.Term();
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
                        SchoolYear createdSchoolYear = scholarScore.createSchoolYear(s.getId(), schoolYear);
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
                            Term createdTerm = scholarScore.createTerm(s.getId(), y.getId(), t);
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
    
    private Map<Long, List<Course>> createCourses() {

        Map<Long, List<Course>> result = new HashMap<>();
        for (School school : schools) {
            Long schoolId = Long.valueOf(school.getSourceSystemId());
            Courses response = powerSchool.getCoursesBySchool(schoolId);
            Collection<Course> apiListOfCourses = response.toInternalModel();
            result.put(schoolId, new ArrayList<>());
            
            apiListOfCourses.forEach(course -> {
                result.get(schoolId).add(scholarScore.createCourse(school.getId(), course));
            });
        }
        return result;
    }

    private Map<Long, Map<Long, Student>> createStudents() {
        Map<Long, Map<Long, Student>> studentsBySchoolAndId = new HashMap<>();
        for (School school : schools) {
            Long schoolId = Long.valueOf(school.getSourceSystemId());
            Students response = powerSchool.getStudentsBySchool(schoolId);
            Collection<Student> apiListOfStudents = response.toInternalModel();

            List<Student> students = new ArrayList<>();
            studentsBySchoolAndId.put(schoolId, new HashMap<>());
            apiListOfStudents.forEach(student -> {
                student.setCurrentSchoolId(school.getId());
                Student createdStudent = scholarScore.createStudent(student);
                student.setId(createdStudent.getId());
                studentsBySchoolAndId.get(schoolId).put(new Long(student.getSourceSystemId()), student);
            });
        }
        return studentsBySchoolAndId;
    }

    /**
     * Create the user entry along side the teacher and administrator entries
     * @return
     */
    public Map<Long, List<User>> createStaff() {
        Map<Long, List<User>> staffBySchool = new HashMap<>();
        for (School school : schools) {
            Staffs response = powerSchool.getStaff(Long.valueOf(school.getSourceSystemId()));
            List<User> apiListOfStaff = response.toInternalModel();
            apiListOfStaff.forEach(staff -> {
                staff.setPassword(UUID.randomUUID().toString());
                try {
                    User result = scholarScore.createUser(staff);
                    staff.setId(result.getId());
                }
                catch (Exception e) {
                }
            });

            staffBySchool.put(Long.valueOf(school.getSourceSystemId()), apiListOfStaff);
        }
        return staffBySchool;
    }

    public List<School> createSchools() {
        SchoolsResponse powerSchools = powerSchool.getSchools();
        List<School> schools = (List<School>) powerSchools.toInternalModel();
        for (School school : schools) {
            School response = scholarScore.createSchool(school);
            school.setId(response.getId());
        }
        return schools;
    }
}
