package com.scholarscore.etl;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.powerschool.api.model.PsPeriod;
import com.scholarscore.etl.powerschool.api.model.assignment.type.PtAssignmentCategory;
import com.scholarscore.etl.powerschool.api.model.assignment.type.PtAssignmentCategoryWrapper;
import com.scholarscore.etl.powerschool.api.model.cycles.PsCycle;
import com.scholarscore.etl.powerschool.api.model.section.PsFinalGradeSetup;
import com.scholarscore.etl.powerschool.api.model.section.PsFinalGradeSetupWrapper;
import com.scholarscore.etl.powerschool.api.model.section.PtSectionMap;
import com.scholarscore.etl.powerschool.api.model.section.PtSectionMapWrapper;
import com.scholarscore.etl.powerschool.api.model.student.PtPsStudentMap;
import com.scholarscore.etl.powerschool.api.model.student.PtPsStudentMapWrapper;
import com.scholarscore.etl.powerschool.api.model.term.PsTermBin;
import com.scholarscore.etl.powerschool.api.model.term.PsTermBinWrapper;
import com.scholarscore.etl.powerschool.api.model.term.PtPsTermBinReportingTerm;
import com.scholarscore.etl.powerschool.api.model.term.PtPsTermBinReportingTermWrapper;
import com.scholarscore.etl.powerschool.api.model.term.PtPsTermMap;
import com.scholarscore.etl.powerschool.api.model.term.PtPsTermMapWrapper;
import com.scholarscore.etl.powerschool.api.model.term.TermAssociator;
import com.scholarscore.etl.powerschool.api.response.PsResponse;
import com.scholarscore.etl.powerschool.api.response.PsResponseInner;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.etl.powerschool.sync.CourseSync;
import com.scholarscore.etl.powerschool.sync.CycleSync;
import com.scholarscore.etl.powerschool.sync.PeriodSync;
import com.scholarscore.etl.powerschool.sync.SchoolSync;
import com.scholarscore.etl.powerschool.sync.TermSync;
import com.scholarscore.etl.powerschool.sync.associator.StaffAssociator;
import com.scholarscore.etl.powerschool.sync.associator.StudentAssociator;
import com.scholarscore.etl.powerschool.sync.attendance.AttendanceSync;
import com.scholarscore.etl.powerschool.sync.attendance.SchoolDaySync;
import com.scholarscore.etl.powerschool.sync.section.SectionSyncRunnable;
import com.scholarscore.etl.powerschool.sync.student.ellsped.SpedEllParser;
import com.scholarscore.etl.powerschool.sync.student.gpa.GpaSync;
import com.scholarscore.etl.powerschool.sync.user.StaffSync;
import com.scholarscore.etl.powerschool.sync.user.StudentSync;
import com.scholarscore.etl.runner.EtlSettings;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.Section;
import com.scholarscore.models.Term;
import com.scholarscore.models.attendance.SchoolDay;
import org.apache.commons.lang3.tuple.MutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
public class EtlEngine implements IEtlEngine {
    private final static Logger LOGGER = LoggerFactory.getLogger(EtlEngine.class);
    public static final Long TOTAL_TTL_MINUTES = 120L;
    public static final int THREAD_POOL_SIZE = 5;
    //After a certain point in the past, we no longer want to sync expensive and large tables, like attendance
    //This date defines that cutoff point before which we will cease to sync updates.
    private LocalDate syncCutoff;
    private PowerSchoolSyncResult results = new PowerSchoolSyncResult();
    private IPowerSchoolClient powerSchool;
    private IAPIClient edPanel;
    private Long dailyAbsenceTrigger;
    //The school_number attribute to school instance
    private ConcurrentHashMap<Long, School> schools;
    //Collections are by sourceSystemSchoolId and if there are nested maps, 
    //the keys are always sourceSystemIds of sub-entities
    private ConcurrentHashMap<Long, ConcurrentHashMap<LocalDate, SchoolDay>> schoolDays;
    private ConcurrentHashMap<Long, ConcurrentHashMap<Long, Term>> terms;
    private ConcurrentHashMap<Long, ConcurrentHashMap<Long, Section>> sections;
    private ConcurrentHashMap<Long, ConcurrentHashMap<Long, Course>> courses = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, ConcurrentHashMap<Long, PsCycle>> cycles = new ConcurrentHashMap<>();
    //TODO Make this a first calss model in EdPanel
    private ConcurrentHashMap<Long, ConcurrentHashMap<Long, PsPeriod>> periods = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, Set<Section>> studentClasses = new ConcurrentHashMap<>();
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

    public Long getDailyAbsenceTrigger() {
        return dailyAbsenceTrigger;
    }

    public void setDailyAbsenceTrigger(Long dailyAbsenceTrigger) {
        this.dailyAbsenceTrigger = dailyAbsenceTrigger;
    }

    public void triggerNotificationEvaluation() {
        for(Map.Entry<Long, School> schoolEntry: schools.entrySet()) {
            try {
                edPanel.triggerNotificationEvaluation(schoolEntry.getValue().getId());
            } catch(HttpClientException e) {
                LOGGER.warn("failed to execute notification evaluation for school with ID: " +
                        schoolEntry.getValue().getId() + e.getMessage());
            }
        }
    }

    @Override
    public SyncResult syncDistrict(EtlSettings settings) {
        this.syncCutoff = LocalDate.now().minusYears(1l);
        this.powerSchool.setSyncCutoff(this.syncCutoff);

        long startTime = System.currentTimeMillis();
        createSchools();
        long endTime = System.currentTimeMillis();
        long schoolCreationTime = (endTime - startTime)/1000;
        LOGGER.info("School sync complete. " + schools.size() + " school(s) synchronized.");

        migrateSchoolYearsAndTerms();
        long yearsAndTermsComplete = (System.currentTimeMillis() - endTime)/1000;
        endTime = System.currentTimeMillis();
        LOGGER.info("Term and year sync complete");

        //Not used in EdPanel, but needed to resolve section_fk on attendance
        createCycles();
        long cycleCreationTime = (System.currentTimeMillis() - endTime)/1000;
        endTime = System.currentTimeMillis();
        LOGGER.info("Cycle sync complete. " + cycles.size() + " school(s) synchronized.");

        //Not used in EdPanel, but needed to resolve section_fk on attendance
        createPeriods();
        long periodCreationTime = (System.currentTimeMillis() - endTime)/1000;
        endTime = System.currentTimeMillis();
        LOGGER.info("Period sync complete. " + periods.size() + " school(s) synchronized.");

        createStaff();
        long staffCreationComplete = (System.currentTimeMillis() - endTime)/1000;
        endTime = System.currentTimeMillis();
        LOGGER.info("Staff sync complete");

        createStudents(settings);
        long studentCreationComplete = (System.currentTimeMillis() - endTime)/1000;
        endTime = System.currentTimeMillis();
        LOGGER.info("Student sync complete");

        List<File> gpaFiles = settings.getGpaImportFiles();
        syncGpa(gpaFiles);
        long gpaFileComplete = (System.currentTimeMillis() - endTime)/1000;
        endTime = System.currentTimeMillis();
        LOGGER.info("GPA sync complete");


        createCourses();
        long courseCreationComplete = (System.currentTimeMillis() - endTime)/1000;
        endTime = System.currentTimeMillis();
        LOGGER.info("Course sync complete");

        migrateSections();
        long sectionCreationComplete = (System.currentTimeMillis() - endTime)/1000;
        endTime = System.currentTimeMillis();
        LOGGER.info("Section sync complete");

        syncStudentAdvisors();
        long advisorSyncCompletion = (System.currentTimeMillis() - endTime)/1000;
        endTime = System.currentTimeMillis();
        LOGGER.info("Section sync complete");

        syncSchoolDaysAndAttendance();
        long schoolDayCreationComplete = (System.currentTimeMillis() - endTime)/1000;
        endTime = System.currentTimeMillis();
        LOGGER.info("School day & Attendance sync complete");

        LOGGER.info("Total runtime: " + (endTime - startTime) / 1000 +
                " seconds, \nschools: " + schoolCreationTime +
                " seconds, \nYears + Terms: " + yearsAndTermsComplete +
                " seconds, \nstaff: " + staffCreationComplete +
                " seconds, \nstudents: " + studentCreationComplete +
                " seconds, \nadvisors: " + advisorSyncCompletion +
                " seconds, \ndays & attendance: " + schoolDayCreationComplete +
                " seconds, \ncourses: " + courseCreationComplete +
                " seconds, \nsections: " + sectionCreationComplete +
                " seconds");
        return results;
    }

    @Override
    public SyncResult syncDistrict() {
        return syncDistrict(new EtlSettings());
    }

    private void syncGpa(List<File> gpaFiles) {
        if (null != gpaFiles) {
            // parse the gpa file from disk assuming the file type is CSV and of a specific format
            GpaSync gpaSync = new GpaSync(gpaFiles, edPanel, powerSchool, studentAssociator, syncCutoff);
            gpaSync.syncCreateUpdateDelete(results);
        }
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
                    syncCutoff,
                    dailyAbsenceTrigger,
                    this.cycles.get(Long.valueOf(school.getValue().getSourceSystemId())),
                    studentClasses,
                    this.periods.get(Long.valueOf(school.getValue().getSourceSystemId())));
            a.syncCreateUpdateDelete(results);
        }
    }

    private void syncStudentAdvisors() {
        Iterator<Map.Entry<Long,School>> it = schools.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long,School> pair = it.next();
            try {
                edPanel.updateAdvisors(pair.getValue().getId());
            } catch (IOException ex) {
                try {
                    edPanel.updateAdvisors(pair.getValue().getId());
                } catch (IOException ex2) {
                    LOGGER.warn("Failed to match advisors for school with EdPanelID : " + pair.getValue().getId());
                }
            }
        }
    }

    /**
     * For each school in this.schools, resolve all the sections and create an EdPanel Section instance for each.
     * For each EdPanel Section instance, resolve and set the appropriate enrolled student IDs, course ID, teacher(s) ID,
     * PsTerm ID, Assignments, and AssignmentGradeFormula.  After these dependencies are resolve, call the EdPanel API to create the Section
     * and the assignments.  Returns void but populates this.sections with all sections created and includes the collection of
     * assignments on each section.
     */
    private void migrateSections() {
        this.sections = new ConcurrentHashMap<>();
        //Resolve the lookup between PowerTeacher sectionID and PowerSchool sectionID:
        BiMap<Long, Long> ptSectionIdToPsSectionId = resolveSectionIdMap();
        Map<Long, Long> ptStudentIdToPStudentId = resolveStudentIdMap();

        //Resolve PowerSchool section ID to PowerTeacher termID to grade setup mappings
        Map<Long, Map<Long, PsFinalGradeSetup>> sectionIdToGradeFormula = new HashMap<>();
        try {
            PsResponse<PsFinalGradeSetupWrapper> gradeSetups =  powerSchool.getFinalGradeSetups();
            for(PsResponseInner<PsFinalGradeSetupWrapper> wrapper : gradeSetups.record) {
                PsFinalGradeSetup gradeSetup = wrapper.tables.psm_finalgradesetup;
                Long powerSchoolSectionId = ptSectionIdToPsSectionId.get(gradeSetup.sectionid);
                if(!sectionIdToGradeFormula.containsKey(powerSchoolSectionId)) {
                    sectionIdToGradeFormula.put(powerSchoolSectionId, new HashMap<>());
                }
                sectionIdToGradeFormula.get(powerSchoolSectionId).put(gradeSetup.reportingtermid, gradeSetup);
            }
        } catch (HttpClientException | NullPointerException e) {
            LOGGER.error("Failed to resolve PowerSchool grade setups prior to migrating sections: " +
                    e.getLocalizedMessage());
        }
        //Associate powerTeacher AssignmentCategoryID to a String value usable in EdPanel
        Map<Long, String> powerTeacherCategoryToEdPanelType = new HashMap<>();
        try {
            PsResponse<PtAssignmentCategoryWrapper> powerTypes =
                    powerSchool.getPowerTeacherAssignmentCategory();
            if (null != powerTypes && null != powerTypes.record) {
                for (PsResponseInner<PtAssignmentCategoryWrapper> pat : powerTypes.record) {
                    if (null != pat.tables && null != pat.tables.psm_assignmentcategory) {
                        PtAssignmentCategory category = pat.tables.psm_assignmentcategory;
                        powerTeacherCategoryToEdPanelType.put(
                                Long.valueOf(category.id),
                                category.name);
                    }
                }
            }
        } catch(HttpClientException e) {
            LOGGER.error("Failed to resolve assignment categories from PowerSchool prior to migrating sections: " +
                    e.getLocalizedMessage());
        }
        LOGGER.info("Section migration antecendents resolved (grade setups & assignment category mappings)");
        //Now we have the section resolution antecedent
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
                    sectionIdToGradeFormula,
                    powerTeacherCategoryToEdPanelType,
                    ptSectionIdToPsSectionId,
                    ptStudentIdToPStudentId,
                    results,
                    studentClasses);
            executor.execute(sectionRunnable);
        }
        executor.shutdown();
        //Spin while we wait for all the threads to complete
        try {
            if (!executor.awaitTermination(TOTAL_TTL_MINUTES, TimeUnit.MINUTES)) { //optional *
                executor.shutdownNow(); //optional **/optional **
            }
        } catch(InterruptedException e) {
            LOGGER.error("Executor thread pool interrupted " + e.getMessage());
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

    private void createStudents(EtlSettings settings) {
        SpedEllParser parser = new SpedEllParser();
        Map<Long, MutablePair<String, String>> spedEll = new HashMap<>();
        try {
            for(File gpaFile : settings.getEllSpedImportFiles()){
                if(gpaFile.canRead() && gpaFile.isFile()) {
                    spedEll = parser.parse(new FileInputStream(gpaFile));
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to resolve SPED/ELL from file", e);
        }

        for (Map.Entry<Long, School> school : this.schools.entrySet()) {
            StudentSync sync = new StudentSync(edPanel, powerSchool, school.getValue(), studentAssociator, spedEll);
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

    public void createCycles() {
        for (Map.Entry<Long, School> school : this.schools.entrySet()) {
            CycleSync sync = new CycleSync(edPanel, powerSchool, school.getValue());
            this.cycles.put(Long.valueOf(school.getValue().getSourceSystemId()),
                    sync.syncCreateUpdateDelete(results));

        }
    }

    public void createPeriods() {
        for (Map.Entry<Long, School> school : this.schools.entrySet()) {
            PeriodSync sync = new PeriodSync(edPanel, powerSchool, school.getValue());
            this.periods.put(Long.valueOf(school.getValue().getSourceSystemId()),
                    sync.syncCreateUpdateDelete(results));

        }
    }

    /*
        PRIVATE HELPER METHODS
     */

    private BiMap<Long, Long> resolveSectionIdMap() {
        BiMap<Long, Long> ptSectionIdToPsSectionId = HashBiMap.create(1000);
        try {
            PsResponse<PtSectionMapWrapper> powerTeacherSection = powerSchool.getPowerTeacherSectionMappings();
            for (PsResponseInner<PtSectionMapWrapper> ptSectWrap : powerTeacherSection.record) {
                PtSectionMap mapping = ptSectWrap.tables.sync_sectionmap;
                ptSectionIdToPsSectionId.put(mapping.sectionid, mapping.sectionsdcid);
            }
        } catch (HttpClientException e) {
            LOGGER.warn("Unable to resolve the powerTeacher->powerSchool section ID mapping. " + e.getMessage());
        }
        return ptSectionIdToPsSectionId;
    }

    private TermAssociator resolveTermAssociator() {
        BiMap<Long, Long> ptTermIdToPsTermId = resolveTermIdMap();
        BiMap<Long, Long> reportingTermIdToTermBinIn = resolveReportingTermIdMap();
        HashMap<Long, Long> psTermIdToTermBinId = resolveTermBinIdToTermId();
        return new TermAssociator.TermAssociatorBuilder().
                withPsTermBinIdToPsTermId(psTermIdToTermBinId).
                withPtReportingTermIdToPsTermBinId(reportingTermIdToTermBinIn).
                withPtTermIdToPsTermId(ptTermIdToPsTermId).
                build();
    }

    private HashMap<Long, Long> resolveTermBinIdToTermId() {
        HashMap<Long, Long> ptTermIdToPsTermId = new HashMap<>();
        try {
            PsResponse<PsTermBinWrapper> termBinResponse = powerSchool.getTermBins();
            for (PsResponseInner<PsTermBinWrapper> ptTerBinWrap : termBinResponse.record) {
                PsTermBin mapping = ptTerBinWrap.tables.termbins;
                ptTermIdToPsTermId.put(mapping.dcid, mapping.termid);
            }
        } catch(HttpClientException e) {
            LOGGER.warn("Unable to resolve the termId->termBinId mapping. " + e.getMessage());
        }
        return ptTermIdToPsTermId;
    }

    private BiMap<Long, Long> resolveTermIdMap() {
        BiMap<Long, Long> ptTermIdToPsTermId = HashBiMap.create();
        try {
            PsResponse<PtPsTermMapWrapper> powerTeacherSection = powerSchool.getPowerTeacherTermMappings();
            for (PsResponseInner<PtPsTermMapWrapper> ptTermWrap : powerTeacherSection.record) {
                PtPsTermMap mapping = ptTermWrap.tables.sync_termmap;
                ptTermIdToPsTermId.put(mapping.termid, mapping.termsdcid);
            }
        } catch(HttpClientException e) {
            LOGGER.warn("Unable to resolve the powerTeacher->powerSchool term ID mapping. " + e.getMessage());
        }
        return ptTermIdToPsTermId;
    }

    private BiMap<Long, Long> resolveReportingTermIdMap() {
        BiMap<Long, Long> ptTermIdToPsTermId = HashBiMap.create();
        try {
            PsResponse<PtPsTermBinReportingTermWrapper> powerTeacherSection = powerSchool.getPowerTeacherTermBinMappings();
            for (PsResponseInner<PtPsTermBinReportingTermWrapper> ptTermWrap : powerTeacherSection.record) {
                PtPsTermBinReportingTerm mapping = ptTermWrap.tables.sync_reportingtermmap;
                ptTermIdToPsTermId.put(mapping.reportingtermid, mapping.termbinsdcid);
            }
        } catch(HttpClientException e) {
            LOGGER.warn("Unable to resolve the powerTeacher->powerSchool reporting term ID to term bin ID mapping. "
                    + e.getMessage());
        }
        return ptTermIdToPsTermId;
    }

    private Map<Long, Long> resolveStudentIdMap() {
        Map<Long, Long> ptStudentIdToTermId = new HashMap<>();
        try {
            PsResponse<PtPsStudentMapWrapper> studentMapResponse = powerSchool.getPowerTeacherStudentMappings();
            for (PsResponseInner<PtPsStudentMapWrapper> ptStudentMapWrap : studentMapResponse.record) {
                PtPsStudentMap mapping = ptStudentMapWrap.tables.sync_studentmap;
                ptStudentIdToTermId.put(mapping.studentid, mapping.studentsdcid);
            }
        } catch(HttpClientException e) {
            LOGGER.warn("Unable to resolve the powerTeacher->powerSchool student ID mapping. " + e.getMessage());
        }
        return ptStudentIdToTermId;
    }
    
}
