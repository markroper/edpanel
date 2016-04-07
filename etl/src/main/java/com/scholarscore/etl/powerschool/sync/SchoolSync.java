package com.scholarscore.etl.powerschool.sync;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.ISync;
import com.scholarscore.etl.PowerSchoolSyncResult;
import com.scholarscore.etl.powerschool.api.response.SchoolsResponse;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.models.School;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by markroper on 10/26/15.
 */
public class SchoolSync implements ISync<School> {
    protected IAPIClient edPanel;
    protected IPowerSchoolClient powerSchool;

    public SchoolSync(IAPIClient edPanel, IPowerSchoolClient powerSchool) {
        this.edPanel = edPanel;
        this.powerSchool = powerSchool;
    }

    @Override
    public ConcurrentHashMap<Long, School> syncCreateUpdateDelete(PowerSchoolSyncResult results) {
        ConcurrentHashMap<Long, School> source = null;
        try {
            source = resolveAllFromSourceSystem();
        } catch (HttpClientException e) {
            results.schoolSourceGetFailed(-1L, -1L);
            return new ConcurrentHashMap<>();
        }
        ConcurrentHashMap<Long, School> edpanel = null;
        try {
            edpanel = resolveFromEdPanel();
        } catch (HttpClientException e) {
            results.schoolEdPanelGetFailed(-1L, -1L);
            return new ConcurrentHashMap<>();
        }

        //Find & perform the inserts and updates, if any
        for (Map.Entry<Long, School> entry : source.entrySet()) {
            School sourceSchool = entry.getValue();
            School edPanelSchool = edpanel.get(entry.getKey());
            if (null == edPanelSchool) {
                School created = null;
                try {
                    created = edPanel.createSchool(sourceSchool);
                    sourceSchool.setId(created.getId());
                    results.schoolCreated(entry.getKey(), created.getId());
                } catch (HttpClientException e) {
                    results.schoolCreateFailed(entry.getKey());
                }
            } else {
                sourceSchool.setId(edPanelSchool.getId());
                //Never update the edpanel GPA|Behavior disabled flags from the source system
                sourceSchool.setDisableGpa(edPanelSchool.getDisableGpa());
                sourceSchool.setDisableBehavior(edPanelSchool.getDisableBehavior());
                edPanelSchool.setYears(sourceSchool.getYears());
                if (!edPanelSchool.equals(sourceSchool)) {
                    try {
                        edPanel.updateSchool(sourceSchool);
                        results.schoolUpdated(entry.getKey(), sourceSchool.getId());
                    } catch (HttpClientException e) {
                        results.schoolUpdateFailed(entry.getKey(), sourceSchool.getId());
                    }
                }
            }
        }
        //Delete anything IN EdPanel that is NOT in source system
        for (Map.Entry<Long, School> entry : edpanel.entrySet()) {
            if (!source.containsKey(entry.getKey())) {
                try {
                    edPanel.deleteSchool(entry.getValue());
                    results.schoolDeleted(entry.getKey(), entry.getValue().getId());
                } catch (HttpClientException e) {
                    results.schoolDeleteFailed(entry.getKey(), entry.getValue().getId());
                }
            }
        }
        return source;
    }

    protected ConcurrentHashMap<Long, School> resolveAllFromSourceSystem() throws HttpClientException {
        SchoolsResponse powerSchools = powerSchool.getSchools();
        ConcurrentHashMap<Long, School> schoolMap = new ConcurrentHashMap<>();
        for(School s: powerSchools.toInternalModel()) {
            schoolMap.put(Long.valueOf(s.getSourceSystemId()), s);
        }
        return schoolMap;
    }

    protected ConcurrentHashMap<Long, School> resolveFromEdPanel() throws HttpClientException {
        School[] schools = edPanel.getSchools();
        ConcurrentHashMap<Long, School> schoolMap = new ConcurrentHashMap<>();
        for(School s: schools) {
            String ssid = s.getSourceSystemId();
            if(null != ssid) {
                schoolMap.put(Long.valueOf(ssid), s);
            }
        }
        return schoolMap;
    }
}
