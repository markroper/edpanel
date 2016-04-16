package com.scholarscore.etl.schoolbrains;

import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.IEtlEngine;
import com.scholarscore.etl.PowerSchoolSyncResult;
import com.scholarscore.etl.SyncResult;
import com.scholarscore.etl.runner.EtlSettings;
import com.scholarscore.etl.schoolbrains.client.ISchoolBrainsClient;
import com.scholarscore.etl.schoolbrains.sync.SbSchoolSync;
import com.scholarscore.etl.schoolbrains.sync.SbSectionSync;
import com.scholarscore.etl.schoolbrains.sync.SbStaffSync;
import com.scholarscore.etl.schoolbrains.sync.SbStudentSync;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.user.Staff;
import com.scholarscore.models.user.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

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

    // TODO: not the right result
    private PowerSchoolSyncResult result = new PowerSchoolSyncResult();
    
    @Override
    public SyncResult syncDistrict(EtlSettings settings) {

        syncSchools();
        
//        syncSchoolEnrollment();
//        syncSchoolYears();

        syncStaff();
        syncStudents(); 

//        syncGpa();
//        syncTerms();
//        syncCourses();
        
        syncSections();
        
//        syncSectionEnrollment();
        
        /* 
         * (steps pasted from original ETL)
         *
         *  1) Synchronize all schools in the district (CRUD)
         *  2) Synchronize all students and staff for all schools (CRUD)
         *  3) Synchronize all courses for the district (CRUD)
         *  4) Synchronize all school years for all schools in the district (CRUD)
         *  5) Synchronize all terms for all schools (CRUD)
         *  6) Synchronize all sections for all schools (Recommend adding multi-threading here)
         *      i.   Synchronize the section definition
         *      ii.  Synchronize the Student section grades
         *      iii. Synchronize the assignments
         *      iv.  Synchronize the student scores on the assignments
        * */
        
        // sync section assignments
        
        // sync 
        
        // sync attendance
        return null;
    }

    private void syncStaff() {
        SbStaffSync staffSync = new SbStaffSync(schoolBrains, edPanel);
    }

    private void syncStudents() {
//        SbStudentSync studentSync = new SbStudentSync(schoolBrains, edPanel);
    }

    private void syncSchools() {
        SbSchoolSync schoolSync = new SbSchoolSync(schoolBrains, edPanel);
        ssidToSchool = schoolSync.syncCreateUpdateDelete(result);
    }

    private void syncSections() {
        SbSectionSync sectionSync = new SbSectionSync(schoolBrains, edPanel);
        ssidToSection = sectionSync.syncCreateUpdateDelete(result);
    }

    @Override
    public SyncResult syncDistrict() {
        return syncDistrict(new EtlSettings());
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
    
    // TODO: rename this results stuff after merging the other branch
    private class SchoolBrainsSyncResult extends PowerSchoolSyncResult { 
    
    }
}
