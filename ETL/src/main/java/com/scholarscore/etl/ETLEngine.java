package com.scholarscore.etl;

import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.powerschool.api.model.PsCourses;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.etl.powerschool.sync.SchoolSync;
import com.scholarscore.etl.powerschool.sync.StaffSync;
import com.scholarscore.etl.powerschool.sync.StudentSync;
import com.scholarscore.etl.powerschool.sync.TermSync;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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
    //Collections are by sourceSystemSchoolId and if there are nested maps, 
    //the keys are always sourceSystemIds of sub-entities
    private ConcurrentHashMap<Long, ConcurrentHashMap<Long, Term>> terms;
    private ConcurrentHashMap<Long, ConcurrentHashMap<Long, Section>> sections;
    private ConcurrentHashMap<Long, ConcurrentHashMap<Long, Course>> courses;

    //Student and staff maps map local ID to User|Student. Elsewhere, we need
    //to map source system id (SSID) to the local IDs. For this purpose we also maintain
    //a mapping of SSID to localId
    private ConcurrentHashMap<Long, Long> ssidToLocalIdStudent = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, Long> ssidToLocalIdStaff = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, Student> students;
    private ConcurrentHashMap<Long, User> staff;

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
//
//        createCourses();
//        long courseCreationComplete = (System.currentTimeMillis() - endTime)/1000;
//        endTime = System.currentTimeMillis();
//
//        migrateSections();
//        long sectionCreationComplete = (System.currentTimeMillis() - endTime)/1000;
//        endTime = System.currentTimeMillis();
//
//        System.out.println("Total runtime: " + (startTime-endTime)/1000 +
//                " seconds, schools: " + schoolCreationTime +
//                " seconds, Years + Terms: " + yearsAndTermsComplete +
//                " seconds, staff: " + staffCreationComplete +
//                " seconds, students: " + studentCreationComplete +
//                " seconds, courses: " + courseCreationComplete +
//                " seconds, sections: " + sectionCreationComplete);
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
            //TODO: transform staff & student collections to be keyed on sourceSystemId instead of sourceSystemUserId
            SectionETLRunnable sectionRunnable = new SectionETLRunnable(
                    powerSchool,
                    edPanel,
                    school,
                    this.courses.get(sourceSystemSchoolId),
                    this.terms.get(sourceSystemSchoolId),
                    this.staff,
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
     * and this.sourceSchoolYears as part of execution.
     */
    private void migrateSchoolYearsAndTerms() {
        if(null != schools) {
            this.terms = new ConcurrentHashMap<>();
            for(School school: schools) {
                TermSync tSync = new TermSync(edPanel, powerSchool, school);
                this.terms.put(
                        Long.valueOf(school.getSourceSystemId()),
                        tSync.synchCreateUpdateDelete()
                );
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
            StudentSync sync = new StudentSync(edPanel, powerSchool, school, studentsBySchoolAndId, ssidToLocalIdStudent);
            studentsBySchoolAndId.putAll(sync.synchCreateUpdateDelete());
        }
        this.students = studentsBySchoolAndId;
    }

    /**
     * Create the user entry along side the teacher and administrator entries
     * @return
     */
    public void createStaff() {
        ConcurrentHashMap<Long, User> staffBySchool = new ConcurrentHashMap<>();
        for (School school : schools) {
            Long psSchoolId = new Long(school.getSourceSystemId());
            StaffSync sync = new StaffSync(edPanel, powerSchool, school, staffBySchool, ssidToLocalIdStaff);
            staffBySchool.putAll(sync.synchCreateUpdateDelete());
        }
        this.staff = staffBySchool;
    }

    public void createSchools() {
        SchoolSync sync = new SchoolSync(edPanel, powerSchool);
        Map<Long, School> result = sync.synchCreateUpdateDelete();
        this.schools = result.entrySet()
                        .stream()
                        .map(Map.Entry::getValue)
                        .collect(Collectors.toList());
    }
}
