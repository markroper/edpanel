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

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
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
        ConcurrentHashMap<Long, SchoolDay> ed = null;
        try {
            source = resolveAllFromSourceSystem(Long.valueOf(school.getSourceSystemId()));
        } catch (HttpClientException e) {
            results.schoolDaySourceGetFailed(Long.valueOf(school.getSourceSystemId()), school.getId());
            return new ConcurrentHashMap<>();
        }
        try {
            ed = resolveFromEdPanel(school.getId());
        } catch (HttpClientException e) {
            results.schoolDayEdPaneleGetFailed(Long.valueOf(school.getSourceSystemId()), school.getId());
            return new ConcurrentHashMap<>();
        }
        Iterator<Map.Entry<Long, SchoolDay>> sourceIterator = source.entrySet().iterator();
        //Find & perform the inserts and updates, if any
        while(sourceIterator.hasNext()) {
            Map.Entry<Long, SchoolDay> entry = sourceIterator.next();
            SchoolDay schoolDay = entry.getValue();
            SchoolDay edPanelSchoolDay = ed.get(entry.getKey());
            if(null == edPanelSchoolDay){
                SchoolDay created = null;
                try {
                    created = edPanel.createSchoolDays(school.getId(), schoolDay);
                } catch (HttpClientException e) {
                    results.schoolDayCreateFailed(entry.getKey());
                    continue;
                }
                schoolDay.setId(created.getId());
                results.schoolDayCreated(entry.getKey(), schoolDay.getId());
            } else {
                schoolDay.setId(edPanelSchoolDay.getId());
                schoolDay.setSchool(edPanelSchoolDay.getSchool());
                if(!edPanelSchoolDay.equals(schoolDay)) {
                    try {
                        edPanel.updateSchoolDays(school.getId(), schoolDay);
                    } catch (IOException e) {
                        results.schoolDayUpdateFailed(entry.getKey(), schoolDay.getId());
                        continue;
                    }
                    results.schoolDayUpdated(entry.getKey(), schoolDay.getId());
                }
            }
        }
        //Delete anything IN EdPanel that is NOT in source system
        Iterator<Map.Entry<Long, SchoolDay>> edpanelIterator = ed.entrySet().iterator();
        while(edpanelIterator.hasNext()) {
            Map.Entry<Long, SchoolDay> entry = edpanelIterator.next();
            if(!source.containsKey(entry.getKey())) {
                try {
                    edPanel.deleteSchoolDays(school.getId(), entry.getValue());
                } catch (HttpClientException e) {
                    results.schoolDayDeleteFailed(entry.getKey(), entry.getValue().getId());
                    continue;
                }
                results.schoolDayDeleted(entry.getKey(), entry.getValue().getId());
            }
        }
        return source;
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
