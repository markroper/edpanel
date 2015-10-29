package com.scholarscore.etl.powerschool.sync;

import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.powerschool.api.response.SchoolsResponse;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.models.School;

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
    public ConcurrentHashMap<Long, School> syncCreateUpdateDelete() {
        ConcurrentHashMap<Long, School> source = resolveAllFromSourceSystem();
        ConcurrentHashMap<Long, School> edpanel = resolveFromEdPanel();

        Iterator<Map.Entry<Long, School>> sourceIterator = source.entrySet().iterator();
        //Find & perform the inserts and updates, if any
        while(sourceIterator.hasNext()) {
            Map.Entry<Long, School> entry = sourceIterator.next();
            School sourceSchool = entry.getValue();
            School edPanelSchool = edpanel.get(entry.getKey());
            if(null == edPanelSchool){
                School created = edPanel.createSchool(sourceSchool);
                sourceSchool.setId(created.getId());
            } else {
                sourceSchool.setId(edPanelSchool.getId());
                edPanelSchool.setYears(sourceSchool.getYears());
                if(!edPanelSchool.equals(sourceSchool)) {
                    edPanel.updateSchool(sourceSchool);
                }
            }
        }
        //Delete anything IN EdPanel that is NOT in source system
        Iterator<Map.Entry<Long, School>> edpanelIterator = edpanel.entrySet().iterator();
        while(edpanelIterator.hasNext()) {
            Map.Entry<Long, School> entry = edpanelIterator.next();
            if(!source.containsKey(entry.getKey())) {
                edPanel.deleteSchool(entry.getValue());
            }
        }
        return source;
    }

    protected ConcurrentHashMap<Long, School> resolveAllFromSourceSystem() {
        SchoolsResponse powerSchools = powerSchool.getSchools();
        ConcurrentHashMap<Long, School> schoolMap = new ConcurrentHashMap<>();
        for(School s: powerSchools.toInternalModel()) {
            schoolMap.put(Long.valueOf(s.getSourceSystemId()), s);
        }
        return schoolMap;
    }

    protected ConcurrentHashMap<Long, School> resolveFromEdPanel() {
        School[] schools = edPanel.getSchools();
        ConcurrentHashMap<Long, School> schoolMap = new ConcurrentHashMap<>();
        for(School s: schools) {
            Long id = null;
            String ssid = s.getSourceSystemId();
            if(null != ssid) {
                id = Long.valueOf(ssid);
                schoolMap.put(id, s);
            }
        }
        return schoolMap;
    }
}
