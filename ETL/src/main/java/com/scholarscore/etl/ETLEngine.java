package com.scholarscore.etl;

import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.powerschool.api.model.PsCourses;
import com.scholarscore.etl.powerschool.api.model.PsStaffs;
import com.scholarscore.etl.powerschool.api.model.PsStudents;
import com.scholarscore.etl.powerschool.api.model.PsTerm;
import com.scholarscore.etl.powerschool.api.response.SchoolsResponse;
import com.scholarscore.etl.powerschool.api.response.TermResponse;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.Term;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This is the E2E flow for powerschool import to edPanel export - we have references to both clients and
 * can invoke get API's from powerschool and POST (create) API's from edPanel.  We assume for now that we'll seek
 * out entities from the scholarscore database before inserting them into the database rather than assuming a trash
 * and burn strategy.
 *
 * Created by mattg on 7/3/©5.
 */
public class ETLEngine implements IETLEngine {
    public static final int THREAD_POOL_SIZE = 5;
    private IPowerSchoolClient powerSchool;
    private IAPIClient edPanel;
    private List<School> schools;
    private List<SchoolYear> schoolYears;
    //SourceSystemStudentId to student
    private ConcurrentHashMap<Long, Student> students;
    //Collections are by sourceSystemSchoolId and if there are nested maps, 
    //the keys are always sourceSystemIds of sub-entities
    private ConcurrentHashMap<Long, ConcurrentHashMap<Long, Term>> terms;
    private ConcurrentHashMap<Long, ConcurrentHashMap<Long, Section>> sections;
    private ConcurrentHashMap<Long, ConcurrentHashMap<Long, Course>> courses;
    private ConcurrentHashMap<Long, ConcurrentHashMap<Long, User>> staff;

    //Error state collections
    private List<Long> unresolvablePowerStudents = Collections.synchronizedList(new ArrayList<>());

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
        long startTime = System.currentTimeMillis();
        MigrationResult result = new MigrationResult();
        createSchools();
        long endTime = System.currentTimeMillis();
        long schoolCreationTime = (startTime - startTime)/1000;

        migrateSchoolYearsAndTerms();
        long yearsAndTermsComplete = (System.currentTimeMillis() - endTime)/1000;
        endTime = System.currentTimeMillis();

        createStaff();
        long staffCreationComplete = (System.currentTimeMillis() - endTime)/1000;
        endTime = System.currentTimeMillis();

        createStudents();
        long studentCreationComplete = (System.currentTimeMillis() - endTime)/1000;
        endTime = System.currentTimeMillis();

        createCourses();
        long courseCreationComplete = (System.currentTimeMillis() - endTime)/1000;
        endTime = System.currentTimeMillis();

        migrateSections();
        long sectionCreationComplete = (System.currentTimeMillis() - endTime)/1000;
        endTime = System.currentTimeMillis();

        System.out.println("Total runtime: " + (startTime-endTime)/1000 +
                " seconds, schools: " + schoolCreationTime +
                " seconds, Years + Terms: " + yearsAndTermsComplete +
                " seconds, staff: " + staffCreationComplete +
                " seconds, students: " + studentCreationComplete +
                " seconds, courses: " + courseCreationComplete +
                " seconds, sections: " + sectionCreationComplete);
        return result;
    }

    /**
     * For each school in this.schools, resolve all the sections and create an EdPanel Section instance for each.
     * For each EdPanel Section instance, resolve and set the appropriate enrolled student IDs, course ID, teacher(s) ID,
     * PsTerm ID, Assignments, and GradeFormula.  After these dependencies are resolve, call the EdPanel API to create the Section
     * and the assignments.  Returns void but populates this.sections with all sections created and includes the collection of
     * assignments on each section.
     */
    private void migrateSections() {
        this.sections = new ConcurrentHashMap<>();
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        for(School school : this.schools) {
            Long sourceSystemSchoolId = Long.valueOf(school.getSourceSystemId());
            sections.put(sourceSystemSchoolId, new ConcurrentHashMap<>());
            SectionETLRunnable sectionRunnable = new SectionETLRunnable(
                    powerSchool,
                    edPanel,
                    school,
                    this.courses.get(sourceSystemSchoolId),
                    this.terms.get(sourceSystemSchoolId),
                    this.staff.get(sourceSystemSchoolId),
                    this.students,
                    this.sections.get(sourceSystemSchoolId),
                    unresolvablePowerStudents);
            executor.execute(sectionRunnable);
        }
        executor.shutdown();
        //Spin while we wait for all the threads to complete
        while(!executor.isTerminated()){}
    }

    /**
     * Creates the all school years and terms for each of the schools on the instance
     * collection this.schools.  Returns void but populates the collections this.terms
     * and this.schoolYears as part of execution.
     */
    private void migrateSchoolYearsAndTerms() {
        if(null != schools) {
            this.terms = new ConcurrentHashMap<>();
            this.schoolYears = Collections.synchronizedList(new ArrayList<SchoolYear>());
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
                            yearToTerms.put(t.getStart_year(), Collections.synchronizedList(new ArrayList<>()));
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
                                this.terms.put(sourceSystemSchoolId, new ConcurrentHashMap<>());
                            }
                            this.terms.get(sourceSystemSchoolId).put(new Long(createdTerm.getSourceSystemId()), createdTerm);
                        }
                    }
                }
            }
        }
    }
    
    private void createCourses() {

        ConcurrentHashMap<Long, ConcurrentHashMap<Long, Course>> result = new ConcurrentHashMap<>();
        for (School school : schools) {
            Long schoolId = Long.valueOf(school.getSourceSystemId());
            result.put(schoolId, new ConcurrentHashMap<>());
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
        ConcurrentHashMap<Long, Student> studentsBySchoolAndId = new ConcurrentHashMap<>();
        for (School school : schools) {
            Long schoolId = Long.valueOf(school.getSourceSystemId());
            PsStudents response = powerSchool.getStudentsBySchool(schoolId);
            Collection<Student> apiListOfStudents = response.toInternalModel();

            List<Student> students = Collections.synchronizedList(new ArrayList<>());
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
        ConcurrentHashMap<Long, ConcurrentHashMap<Long, User>> staffBySchool = new ConcurrentHashMap<>();
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
                        staffBySchool.put(psSchoolId, new ConcurrentHashMap<>());
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
