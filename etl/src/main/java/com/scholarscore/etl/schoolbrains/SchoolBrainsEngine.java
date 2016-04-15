package com.scholarscore.etl.schoolbrains;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.IEtlEngine;
import com.scholarscore.etl.SyncResult;
import com.scholarscore.etl.runner.EtlSettings;
import com.scholarscore.etl.schoolbrains.client.ISchoolBrainsClient;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.user.Staff;
import com.scholarscore.models.user.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
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

    @Override
    public SyncResult syncDistrict(EtlSettings settings) {
        syncSchools();
        return null;
    }

    private void syncSchools() {
        try {
            List<School> schools = schoolBrains.getSchools();
            School[] edPanelSchools = edPanel.getSchools();
            if(null != schools) {
                for(School s: schools) {
                    ssidToSchool.put(s.getSourceSystemId(), s);
                }
            }
        } catch (HttpClientException e) {
            LOGGER.error("Failed to resolve schools from schoolbrains: " + e.getMessage());
        }
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
}
