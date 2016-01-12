package com.scholarscore.etl.powerschool.sync;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.ISync;
import com.scholarscore.etl.PowerSchoolSyncResult;
import com.scholarscore.etl.powerschool.api.model.cycles.PsCycle;
import com.scholarscore.etl.powerschool.api.model.cycles.PsCycleWrapper;
import com.scholarscore.etl.powerschool.api.response.PsResponse;
import com.scholarscore.etl.powerschool.api.response.PsResponseInner;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.models.School;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by cwallace on 12/23/15.
 */
public class CycleSync implements ISync<PsCycle> {

    private final static Logger LOGGER = LoggerFactory.getLogger(CycleSync.class);
    protected IAPIClient edPanel;
    protected IPowerSchoolClient powerSchool;
    protected School school;

    public CycleSync(IAPIClient edPanel,
                      IPowerSchoolClient powerSchool,
                      School s) {
        this.edPanel = edPanel;
        this.powerSchool = powerSchool;
        this.school = s;
    }
    @Override
    public ConcurrentHashMap<Long, PsCycle> syncCreateUpdateDelete(PowerSchoolSyncResult results) {
        ConcurrentHashMap<Long, PsCycle> source = null;
        try {
            source = resolveAllFromSourceSystem();
        } catch (HttpClientException e) {
            try {
                source = resolveAllFromSourceSystem();
            } catch (HttpClientException ex) {
                LOGGER.error("Unable to extract cycles from PowerSchool for school: " + school.getName() +
                        " with EdPanel ID: " + school.getId());
                results.cycleSourceGetFailed(Long.valueOf(school.getSourceSystemId()), school.getId());
                return new ConcurrentHashMap<>();
            }
        }
        return source;
    }

    protected ConcurrentHashMap<Long, PsCycle> resolveAllFromSourceSystem() throws HttpClientException {
        ConcurrentHashMap<Long, PsCycle> result = new ConcurrentHashMap<>();
        PsResponse<PsCycleWrapper> response = powerSchool.getCyclesBySchool(school.getNumber());
        for (PsResponseInner<PsCycleWrapper> cycle : response.record) {
            result.put(cycle.id , cycle.tables.cycle_day);
        }
        return result;
    }
}