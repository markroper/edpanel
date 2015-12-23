package com.scholarscore.etl.powerschool;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.ISync;
import com.scholarscore.etl.PowerSchoolSyncResult;
import com.scholarscore.etl.powerschool.api.model.PsCourses;
import com.scholarscore.etl.powerschool.api.model.PsCycles;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.models.Course;
import com.scholarscore.models.Cycle;
import com.scholarscore.models.School;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by cwallace on 12/23/15.
 */
public class CycleSync implements ISync<Cycle> {

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
    public ConcurrentHashMap<Long, Cycle> syncCreateUpdateDelete(PowerSchoolSyncResult results) {
        ConcurrentHashMap<Long, Cycle> source = null;
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
        return null;
    }

    protected ConcurrentHashMap<Long, Cycle> resolveAllFromSourceSystem() throws HttpClientException {
        ConcurrentHashMap<Long, Cycle> result = new ConcurrentHashMap<>();
        PsCycles response = powerSchool.getCyclesBySchool(Long.valueOf(school.getSourceSystemId()));
        Collection<Cycle> apiListOfCourses = response.toInternalModel();
        for(Cycle c: apiListOfCourses) {
            //result.put(Long.valueOf(c.getSourceSystemId()), c);
        }
        return result;
    }
}
