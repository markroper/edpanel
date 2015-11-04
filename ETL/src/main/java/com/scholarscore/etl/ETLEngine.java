package com.scholarscore.etl;

import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.etl.powerschool.sync.CourseSync;
import com.scholarscore.etl.powerschool.sync.SchoolSync;
import com.scholarscore.etl.powerschool.sync.TermSync;
import com.scholarscore.etl.powerschool.sync.associator.StaffAssociator;
import com.scholarscore.etl.powerschool.sync.associator.StudentAssociator;
import com.scholarscore.etl.powerschool.sync.attendance.AttendanceSync;
import com.scholarscore.etl.powerschool.sync.attendance.SchoolDaySync;
import com.scholarscore.etl.powerschool.sync.section.SectionSyncRunnable;
import com.scholarscore.etl.powerschool.sync.user.StaffSync;
import com.scholarscore.etl.powerschool.sync.user.StudentSync;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.Section;
import com.scholarscore.models.Term;
import com.scholarscore.models.attendance.SchoolDay;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This is the E2E flow for PowerSchool import to edPanel export - we have references to both clients and
 * can invoke get APIs from PowerSchool and POST/PUT/DELETE APIs to EdPanel to synchronize.
 *
 * The syncDistrict() method on this implementation can be run to do an initial migration or to synchronize a
 * partially migrated or out of date set of entities between EdPanel and PowerSchool.  The method is idempotent,
 * if it fails partway through or completes successfully and is rerun, the end state it generates should
 * always be the same.
 *
 * Created by mattg on 7/3/©5.
 */
public class ETLEngine implements IETLEngine {
    public static final Long TOTAL_TTL_MINUTES = 120L;
    public static final int THREAD_POOL_SIZE = 8;
    //After a certain point in the past, we no longer want to sync expensive and large tables, like attendance
    //This date defines that cutoff point before which we will cease to sync updates.
    private Date syncCutoff;
    private SyncResult results = new SyncResult();
    private IPowerSchoolClient powerSchool;
    private IAPIClient edPanel;
    //The school_number attribute to school instance
    private ConcurrentHashMap<Long, School> schools;
    //Collections are by sourceSystemSchoolId and if there are nested maps, 
    //the keys are always sourceSystemIds of sub-entities
    private ConcurrentHashMap<Long, ConcurrentHashMap<Date, SchoolDay>> schoolDays;
    private ConcurrentHashMap<Long, ConcurrentHashMap<Long, Term>> terms;
    private ConcurrentHashMap<Long, ConcurrentHashMap<Long, Section>> sections;
    private ConcurrentHashMap<Long, ConcurrentHashMap<Long, Course>> courses = new ConcurrentHashMap<>();
    //Student and staff maps map local ID to User|Student. Elsewhere, we need
    //to map source system id (SSID) to the local IDs. For this purpose we also maintain
    //a mapping of SSID to localId, all of which is encapsulated in the associator below
    private StaffAssociator staffAssociator = new StaffAssociator();
    private StudentAssociator studentAssociator = new StudentAssociator();

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
    public SyncResult syncDistrict() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1); // to get previous year add -1
        this.syncCutoff = cal.getTime();
        this.powerSchool.setSyncCutoff(this.syncCutoff);

        long startTime = System.currentTimeMillis();
        createSchools();
        long endTime = System.currentTimeMillis();
        long schoolCreationTime = (endTime - startTime)/1000;
        System.out.println("School sync complete");

        migrateSchoolYearsAndTerms();
        long yearsAndTermsComplete = (System.currentTimeMillis() - endTime)/1000;
        endTime = System.currentTimeMillis();
        System.out.println("Term and year sync complete");

        createStaff();
        long staffCreationComplete = (System.currentTimeMillis() - endTime)/1000;
        endTime = System.currentTimeMillis();
        System.out.println("Staff sync complete");

        createStudents();
        long studentCreationComplete = (System.currentTimeMillis() - endTime)/1000;
        endTime = System.currentTimeMillis();
        System.out.println("Student sync complete");

        syncSchoolDaysAndAttendance();
        long schoolDayCreationComplete = (System.currentTimeMillis() - endTime)/1000;
        endTime = System.currentTimeMillis();
        System.out.println("School day & Attendance sync complete");

        createCourses();
        long courseCreationComplete = (System.currentTimeMillis() - endTime)/1000;
        endTime = System.currentTimeMillis();
        System.out.println("Course sync complete");

        migrateSections();
        long sectionCreationComplete = (System.currentTimeMillis() - endTime)/1000;
        endTime = System.currentTimeMillis();
        System.out.println("Section sync complete");

        System.out.println("Total runtime: " + (endTime - startTime)/1000 +
                " seconds, \nschools: " + schoolCreationTime +
                " seconds, \nYears + Terms: " + yearsAndTermsComplete +
                " seconds, \nstaff: " + staffCreationComplete +
                " seconds, \nstudents: " + studentCreationComplete +
                " seconds, \ndays & attendance: " + schoolDayCreationComplete +
                " seconds, \ncourses: " + courseCreationComplete +
                " seconds, \nsections: " + sectionCreationComplete +
                " seconds");
        outputResults(results);
        return results;
    }

    private void syncSchoolDaysAndAttendance() {
        this.schoolDays = new ConcurrentHashMap<>();
        for(Map.Entry<Long, School> school : this.schools.entrySet()) {
            SchoolDaySync s = new SchoolDaySync(edPanel, powerSchool, school.getValue(), syncCutoff);
            Long schoolSsid = Long.valueOf(school.getValue().getSourceSystemId());
            this.schoolDays.put(
                    schoolSsid,
                    s.syncCreateUpdateDelete(results));
            AttendanceSync a = new AttendanceSync(
                    edPanel,
                    powerSchool,
                    school.getValue(),
                    studentAssociator,
                    this.schoolDays.get(schoolSsid),
                    syncCutoff);
            a.syncCreateUpdateDelete(results);
        }

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
        for(Map.Entry<Long, School> school : this.schools.entrySet()) {
            Long sourceSystemSchoolId = Long.valueOf(school.getValue().getSourceSystemId());
            sections.put(sourceSystemSchoolId, new ConcurrentHashMap<>());
            SectionSyncRunnable sectionRunnable = new SectionSyncRunnable(
                    powerSchool,
                    edPanel,
                    school.getValue(),
                    this.courses.get(sourceSystemSchoolId),
                    this.terms.get(sourceSystemSchoolId),
                    staffAssociator,
                    studentAssociator,
                    this.sections.get(sourceSystemSchoolId),
                    results);
            executor.execute(sectionRunnable);
        }
        executor.shutdown();
        //Spin while we wait for all the threads to complete
        try {
            if (!executor.awaitTermination(TOTAL_TTL_MINUTES, TimeUnit.MINUTES)) { //optional *
                executor.shutdownNow(); //optional **/optional **
            }
        } catch(InterruptedException e) {
            System.out.println("Executor thread pool interrupted " + e.getMessage());
        }
    }

    private void migrateSchoolYearsAndTerms() {
        if(null != schools) {
            this.terms = new ConcurrentHashMap<>();
            for(Map.Entry<Long, School> school : this.schools.entrySet()) {
                TermSync tSync = new TermSync(edPanel, powerSchool, school.getValue());
                this.terms.put(
                        Long.valueOf(school.getValue().getSourceSystemId()),
                        tSync.syncCreateUpdateDelete(results)
                );
            }
        }
    }

    private void createCourses() {

        for (Map.Entry<Long, School> school : this.schools.entrySet()) {
            CourseSync sync = new CourseSync(edPanel, powerSchool, school.getValue());
            this.courses.put(Long.valueOf(school.getValue().getSourceSystemId()), sync.syncCreateUpdateDelete(results));
        }
    }

    private void createStudents() {
        for (Map.Entry<Long, School> school : this.schools.entrySet()) {
            StudentSync sync = new StudentSync(edPanel, powerSchool, school.getValue(), studentAssociator);
            studentAssociator.addOtherIdMap(sync.syncCreateUpdateDelete(results));
        }
    }

    public void createStaff() {
        for (Map.Entry<Long, School> school : this.schools.entrySet()) {
            StaffSync sync = new StaffSync(edPanel, powerSchool, school.getValue(), staffAssociator);
            staffAssociator.addOtherIdMap(sync.syncCreateUpdateDelete(results));
        }
    }

    public void createSchools() {
        SchoolSync sync = new SchoolSync(edPanel, powerSchool);
        Map<Long, School> result = sync.syncCreateUpdateDelete(results);
        this.schools = new ConcurrentHashMap<>();
        for (Map.Entry<Long, School> school : result.entrySet()) {
            School s = school.getValue();
            this.schools.put(s.getNumber(), s);
        }
    }

    /**
     * TODO: figure out how we actually want to output these results.  For now, its a series of sys outs
     * @param results
     */
    private static void outputResults(SyncResult results) {
        System.out.println("--");
        System.out.println("Created Schools: " + results.getSchools().getCreated().size());
        System.out.println("Failed school creations: " + results.getSchools().getFailedCreates().size());
        System.out.println("Failed school source gets: " + results.getSchools().getSourceGetFailed().size());
        System.out.println("Failed school edpanel gets: " + results.getSchools().getEdPanelGetFailed().size());
        System.out.println("--");
        System.out.println("Created Courses: " + results.getCourses().getCreated().size());
        System.out.println("Updated Courses: " + results.getCourses().getUpdated().size());
        System.out.println("Failed courses creations: " + results.getCourses().getFailedCreates().size());
        System.out.println("Failed courses source gets: " + results.getCourses().getSourceGetFailed().size());
        System.out.println("Failed courses edpanel gets: " + results.getCourses().getEdPanelGetFailed().size());
        System.out.println("--");
        System.out.println("Created Terms: " + results.getTerms().getCreated().size());
        System.out.println("Updated Terms: " + results.getTerms().getUpdated().size());
        System.out.println("Failed terms creations: " + results.getTerms().getFailedCreates().size());
        System.out.println("Failed terms source gets: " + results.getTerms().getSourceGetFailed().size());
        System.out.println("Failed terms edpanel gets: " + results.getTerms().getEdPanelGetFailed().size());
        System.out.println("--");
        System.out.println("Created staff: " + results.getStaff().getCreated().size());
        System.out.println("Updated staff: " + results.getStaff().getUpdated().size());
        System.out.println("Failed staff creations: " + results.getStaff().getFailedCreates().size());
        System.out.println("Failed staff source gets: " + results.getStaff().getSourceGetFailed().size());
        System.out.println("Failed staff edpanel gets: " + results.getStaff().getEdPanelGetFailed().size());
        System.out.println("--");
        System.out.println("Created students: " + results.getStudents().getCreated().size());
        System.out.println("Updated students: " + results.getStudents().getUpdated().size());
        System.out.println("Deleted students: " + results.getStudents().getDeleted().size());
        System.out.println("Failed students creations: " + results.getStudents().getFailedCreates().size());
        System.out.println("Failed students source gets: " + results.getStudents().getSourceGetFailed().size());
        System.out.println("Failed students edpanel gets: " + results.getStudents().getEdPanelGetFailed().size());
        System.out.println("--");
        System.out.println("Created sections: " + results.getSections().getCreated().size());
        System.out.println("Updated sections: " + results.getSections().getUpdated().size());
        System.out.println("Deleted sections: " + results.getSections().getUpdated().size());
        System.out.println("Failed sections creations: " + results.getSections().getFailedCreates().size());
        System.out.println("Failed sections source gets: " + results.getSections().getSourceGetFailed().size());
        System.out.println("Failed sections edpanel gets: " + results.getSections().getEdPanelGetFailed().size());
        System.out.println("--");
        Integer studAssignments = 0;
        Integer studUpdatedAssignments = 0;
        Integer studDeletedAssignments = 0;
        Integer studAssFailedCreates = 0;
        Integer studAssFailedSourceGets = 0;
        Integer studAssFailedEdPanelGets = 0;
        for(Map.Entry<Long, EntitySyncResult> sa : results.getSectionAssignments().entrySet()) {
            studAssignments += sa.getValue().getCreated().size();
            studUpdatedAssignments += sa.getValue().getUpdated().size();
            studDeletedAssignments += sa.getValue().getDeleted().size();
            studAssFailedCreates += sa.getValue().getFailedCreates().size();
            studAssFailedSourceGets += sa.getValue().getSourceGetFailed().size();
            studAssFailedEdPanelGets += sa.getValue().getEdPanelGetFailed().size();
        }
        System.out.println("Created section assignments: " + studAssignments);
        System.out.println("Updated section assignments: " + studUpdatedAssignments);
        System.out.println("Deleted section assignments: " + studDeletedAssignments);
        System.out.println("Failed section assignments creations: " + studAssFailedCreates);
        System.out.println("Failed section assignments source gets: " + studAssFailedSourceGets);
        System.out.println("Failed section assignments edpanel gets: " + studAssFailedEdPanelGets);
        System.out.println("--");

        Integer ssgs = 0;
        Integer ssgsUpdated = 0;
        Integer ssgsDeleted = 0;
        Integer ssgFailedCreates = 0;
        Integer ssgFailedSourceGets = 0;
        Integer ssgFailedEdPanelGets = 0;
        for(Map.Entry<Long, EntitySyncResult> sa : results.getStudentSectionGrades().entrySet()) {
            ssgs += sa.getValue().getCreated().size();
            ssgsUpdated += sa.getValue().getUpdated().size();
            ssgsDeleted += sa.getValue().getDeleted().size();
            ssgFailedCreates += sa.getValue().getFailedCreates().size();
            ssgFailedSourceGets += sa.getValue().getSourceGetFailed().size();
            ssgFailedEdPanelGets += sa.getValue().getEdPanelGetFailed().size();
        }
        System.out.println("Created section student grades: " + ssgs);
        System.out.println("Updated section student grades: " + ssgsUpdated);
        System.out.println("Deleted section student grades: " + ssgsDeleted);
        System.out.println("Failed ssg creations: " + ssgFailedCreates);
        System.out.println("Failed ssg source gets: " + ssgFailedSourceGets);
        System.out.println("Failed ssg edpanel gets: " + ssgFailedEdPanelGets);
        System.out.println("--");
        Integer sectAss = 0;
        Integer sectAssUpdated = 0;
        Integer sectAssDeleted = 0;
        Integer sectAssFailedCreates = 0;
        Integer sectAssFailedSourceGets = 0;
        Integer sectAssFailedEdPanelGets = 0;
        for(Map.Entry<Long, ConcurrentHashMap<Long, EntitySyncResult>> sa : results.getStudentAssignments().entrySet()) {
            for(Map.Entry<Long, EntitySyncResult> a : sa.getValue().entrySet()) {
                sectAss += a.getValue().getCreated().size();
                sectAssUpdated += a.getValue().getUpdated().size();
                sectAssDeleted += a.getValue().getDeleted().size();
                sectAssFailedCreates += a.getValue().getFailedCreates().size();
                sectAssFailedSourceGets += a.getValue().getSourceGetFailed().size();
                sectAssFailedEdPanelGets += a.getValue().getEdPanelGetFailed().size();
            }
        }
        System.out.println("Created student assignments: " + sectAss);
        System.out.println("Updated student assignments: " + sectAssUpdated);
        System.out.println("Deleted student assignments: " + sectAssDeleted);
        System.out.println("Failed student assignments creations: " + sectAssFailedCreates);
        System.out.println("Failed student assignments source gets: " + sectAssFailedSourceGets);
        System.out.println("Failed student assignments edpanel gets: " + sectAssFailedEdPanelGets);
    }
}
