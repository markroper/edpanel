package com.scholarscore.etl.schoolbrains;

import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.IEtlEngine;
import com.scholarscore.etl.PowerSchoolSyncResult;
import com.scholarscore.etl.SyncResult;
import com.scholarscore.etl.runner.EtlSettings;
import com.scholarscore.etl.schoolbrains.client.ISchoolBrainsClient;
import com.scholarscore.etl.schoolbrains.sync.SbSchoolSync;
import com.scholarscore.etl.schoolbrains.sync.SbSchoolYearSync;
import com.scholarscore.etl.schoolbrains.sync.SbSectionSync;
import com.scholarscore.etl.schoolbrains.sync.SbStudentSync;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.user.Staff;
import com.scholarscore.models.user.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by markroper on 4/15/16.
 */
public class SchoolBrainsEngine implements IEtlEngine {
    private final static Logger LOGGER = LoggerFactory.getLogger(SchoolBrainsEngine.class);
    private ISchoolBrainsClient schoolBrains;
    private IAPIClient edPanel;

    private Map<String, School> ssidToSchool = new HashMap<>();
    private Map<String, SchoolYear> ssidToSchoolYear = new HashMap<>();
    private Map<String, Course> ssidToCourse = new HashMap<>();
    private Map<String, Student> ssidToStudent = new HashMap<>();
    private Map<String, Staff> ssidToStaff = new HashMap<>();
    private Map<String, Section> ssidToSection = new HashMap<>();

    private SchoolBrainsSyncResult result = new SchoolBrainsSyncResult();
    
    @Override
    public SyncResult syncDistrict(EtlSettings settings) {

        syncSchools();
        
        syncSchoolYears();
        
        syncTerms();
        
        syncStaff();

        syncStudents(); 

        syncCourses();
        
        syncSections();

        syncAttendance();
        
        syncBehavior();
        
        // TODO SchoolBrains: proper result measurement
        return null;
    }

    private void syncSchoolYears() {
        for (School school : ssidToSchool.values()) {
            SbSchoolYearSync schoolYearSync = new SbSchoolYearSync(schoolBrains, edPanel, school);
            ConcurrentHashMap<String, SchoolYear> schoolYears = schoolYearSync.syncCreateUpdateDelete(result);
            for (Map.Entry<String, SchoolYear> schoolYear : schoolYears.entrySet()) {
                if (ssidToSchoolYear.containsKey(schoolYear.getKey())) {
                    LOGGER.warn("Warning! Duplicate school year detected with key " + schoolYear.getKey());
                }
                ssidToSchoolYear.put(schoolYear.getKey(), schoolYear.getValue());
            }
        }
    }

    private void syncCourses() {
        throw new RuntimeException("TODO Schoolbrains");
    }

    private void syncStaff() {
        throw new RuntimeException("TODO Schoolbrains");
    }

    private void syncTerms() {
        // TODO SchoolBrains - sync terms
//        throw new RuntimeException("TODO Schoolbrains");
    }

    @Override
    public SyncResult syncDistrict() {
        return syncDistrict(new EtlSettings());
    }

    private void syncStudents() {
        // For now, one call syncs all students. This differs from powerschool ETL which syncs students by school
        SbStudentSync studentSync = new SbStudentSync(schoolBrains, edPanel);
        Map<String,Student> results = studentSync.syncCreateUpdateDelete(result);
        ssidToStudent.putAll(results);
    }
    

    private void syncSchools() {
        SbSchoolSync schoolSync = new SbSchoolSync(schoolBrains, edPanel);
        ssidToSchool = schoolSync.syncCreateUpdateDelete(result);
    }

    private void syncSections() {
        for (School school : ssidToSchool.values()) {
            // TODO SchoolBrains: if mirroring powerschool, we need terms and courses populated before we do this
            Long schoolYearId = -1L;
            Long termId = -1L;
            SbSectionSync sectionSync = new SbSectionSync(schoolBrains, edPanel, school.getId(), schoolYearId, termId);
            ssidToSection = sectionSync.syncCreateUpdateDelete(result);
        }

    }

    private void syncAttendance() {
        throw new RuntimeException("TODO Schoolbrains - attendance sync not implemented");
    }

    private void syncBehavior() {
//        throw new RuntimeException("TODO Schoolbrains - behavior sync not implemented");
    }

    public ISchoolBrainsClient getSchoolBrains() {
        return schoolBrains;
    }

    public void setSchoolBrains(ISchoolBrainsClient schoolBrains) {
        this.schoolBrains = schoolBrains;
    }

    public IAPIClient getEdPanel() {
        return edPanel;
    }

    public void setEdPanel(IAPIClient edPanel) {
        this.edPanel = edPanel;
    }
    
    // TODO SchoolBrains: rename this results stuff after merging the other branch
    private class SchoolBrainsSyncResult extends PowerSchoolSyncResult { 
    
    }
}
