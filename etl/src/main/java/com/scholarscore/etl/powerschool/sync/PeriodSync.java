package com.scholarscore.etl.powerschool.sync;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.ISync;
import com.scholarscore.etl.PowerSchoolSyncResult;
import com.scholarscore.etl.powerschool.api.model.PsPeriod;
import com.scholarscore.etl.powerschool.api.model.PsPeriodWrapper;
import com.scholarscore.etl.powerschool.api.response.PsResponse;
import com.scholarscore.etl.powerschool.api.response.PsResponseInner;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.models.School;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by cwallace on 12/29/15.
 */
public class PeriodSync implements ISync<PsPeriod> {

    private final static Logger LOGGER = LoggerFactory.getLogger(PeriodSync.class);
    protected IAPIClient edPanel;
    protected IPowerSchoolClient powerSchool;
    protected School school;

    public PeriodSync(IAPIClient edPanel,
                     IPowerSchoolClient powerSchool,
                     School s) {
        this.edPanel = edPanel;
        this.powerSchool = powerSchool;
        this.school = s;
    }
    @Override
    public ConcurrentHashMap<Long, PsPeriod> syncCreateUpdateDelete(PowerSchoolSyncResult results) {
        ConcurrentHashMap<Long, PsPeriod> source = null;
        try {
            source = resolveAllFromSourceSystem();
        } catch (HttpClientException e) {
            try {
                source = resolveAllFromSourceSystem();
            } catch (HttpClientException ex) {
                LOGGER.error("Unable to extract periods from PowerSchool for school: " + school.getName() +
                        " with EdPanel ID: " + school.getId());
                results.periodSourceGetFailed(Long.valueOf(school.getSourceSystemId()), school.getId());
                return new ConcurrentHashMap<>();
            }
        }
        return source;
    }


    protected ConcurrentHashMap<Long, PsPeriod> resolveAllFromSourceSystem() throws HttpClientException {
        ConcurrentHashMap<Long, PsPeriod> result = new ConcurrentHashMap<>();
        PsResponse<PsPeriodWrapper> response = powerSchool.getPeriodsBySchool(school.getNumber());
        for (PsResponseInner<PsPeriodWrapper> period : response.record) {
            result.put(period.tables.period.id , period.tables.period);
        }
        return result;
    }
}
