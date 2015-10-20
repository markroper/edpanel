package com.scholarscore.etl;

import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.powerschool.api.model.*;
import com.scholarscore.etl.powerschool.api.response.SchoolsResponse;
import com.scholarscore.etl.powerschool.api.response.TermResponse;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.models.*;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
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
    private List<com.scholarscore.models.Term> terms;

    // Collections by schoolId
    private Map<Long, List<Course>> courses;
    private Map<Long, List<User>> staff;
    private Map<Long, List<Student>> students;

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
        createSchoolYearsAndTerms();  
        this.staff = createStaff();
        this.students = createStudents();
        this.courses = createCourses();
        return result;
    }
    
    /**
     * Creates the all school years and terms for each of the schools on the instance
     * collection this.schools.  Returns void but populated the collections this.terms
     * and this.schoolYears as part of execution.
     */
    private void createSchoolYearsAndTerms() {
        if(null != schools) {
            this.terms = new ArrayList<Term>();
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
                            this.terms.add(createdTerm);
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

            apiListOfCourses.forEach(course -> {
                scholarScore.createCourse(school.getId(), course);
            });
        }
        return result;
    }

    private Map<Long, List<Student>> createStudents() {
        Map<Long, List<Student>> studentsBySchool = new HashMap<>();
        for (School school : schools) {
            Long schoolId = Long.valueOf(school.getSourceSystemId());
            Students response = powerSchool.getStudentsBySchool(schoolId);
            Collection<Student> apiListOfStudents = response.toInternalModel();

            List<Student> students = new ArrayList<>();
            apiListOfStudents.forEach(student -> {
                student.setCurrentSchoolId(school.getId());
                Student createdStudent = scholarScore.createStudent(student);
                student.setId(createdStudent.getId());
                students.add(student);
            });
            studentsBySchool.put(schoolId, students);
        }
        return studentsBySchool;
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

            staffBySchool.put(school.getId(), apiListOfStaff);
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
