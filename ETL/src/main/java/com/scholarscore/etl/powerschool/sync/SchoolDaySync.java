package com.scholarscore.etl.powerschool.sync;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.ISync;
import com.scholarscore.etl.SyncResult;
import com.scholarscore.etl.powerschool.api.model.attendance.PsCalendarDay;
import com.scholarscore.etl.powerschool.api.model.attendance.PsCalendarDayWrapper;
import com.scholarscore.etl.powerschool.api.response.PsResponse;
import com.scholarscore.etl.powerschool.api.response.PsResponseInner;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.models.School;
import com.scholarscore.models.attendance.SchoolDay;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by markroper on 10/30/15.
 */
public class SchoolDaySync implements ISync<SchoolDay> {
    protected IAPIClient edPanel;
    protected IPowerSchoolClient powerSchool;
    protected School school;

    public SchoolDaySync(IAPIClient edPanel,
                      IPowerSchoolClient powerSchool,
                      School s) {
        this.edPanel = edPanel;
        this.powerSchool = powerSchool;
        this.school = s;
    }

    @Override
    public ConcurrentHashMap<Long, SchoolDay> syncCreateUpdateDelete(SyncResult results) {
        ConcurrentHashMap<Long, SchoolDay> source = null;
        ConcurrentHashMap<Long, SchoolDay> edPanel = null;
        try {
            source = resolveAllFromSourceSystem(Long.valueOf(school.getSourceSystemId()));
        } catch (HttpClientException e) {
            results.schoolDaySourceGetFailed(Long.valueOf(school.getSourceSystemId()), school.getId());
            return new ConcurrentHashMap<>();
        }
        try {
            edPanel = resolveFromEdPanel(school.getId());
        } catch (HttpClientException e) {
            results.schoolDayEdPaneleGetFailed(Long.valueOf(school.getSourceSystemId()), school.getId());
            return new ConcurrentHashMap<>();
        }
        return null;
    }

    protected ConcurrentHashMap<Long, SchoolDay> resolveAllFromSourceSystem(Long sourceSchoolId) throws HttpClientException {
        ConcurrentHashMap<Long, SchoolDay> result = new ConcurrentHashMap<>();
        PsResponse<PsCalendarDayWrapper> response = powerSchool.getSchoolCalendarDays(sourceSchoolId);
        for(PsResponseInner<PsCalendarDayWrapper> wrap : response.record) {
            PsCalendarDay psCalDay = wrap.tables.calendar_day;
            SchoolDay day = psCalDay.toApiModel();
            result.put(psCalDay.dcid, day);
        }
        return result;
    }

    protected ConcurrentHashMap<Long, SchoolDay> resolveFromEdPanel(Long schoolId) throws HttpClientException {
        SchoolDay[] days = edPanel.getSchoolDays(school.getId());
        ConcurrentHashMap<Long, SchoolDay> dayMap = new ConcurrentHashMap<>();
        for(SchoolDay c: days) {
            Long id = null;
            String ssid = c.getSourceSystemId();
            if(null != ssid) {
                id = Long.valueOf(ssid);
                dayMap.put(id, c);
            }
        }
        return dayMap;
    }
}
