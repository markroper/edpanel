package com.scholarscore.etl.powerschool.sync.attendance;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.SyncResult;
import com.scholarscore.etl.powerschool.api.model.attendance.PsCalendarDay;
import com.scholarscore.etl.powerschool.api.model.attendance.PsCalendarDayWrapper;
import com.scholarscore.etl.powerschool.api.response.PsResponse;
import com.scholarscore.etl.powerschool.api.response.PsResponseInner;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.models.School;
import com.scholarscore.models.attendance.SchoolDay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by markroper on 10/30/15.
 */
public class SchoolDaySync {
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

    public ConcurrentHashMap<Date, SchoolDay> syncCreateUpdateDelete(SyncResult results) {
        ConcurrentHashMap<Date, SchoolDay> source = null;
        ConcurrentHashMap<Date, SchoolDay> ed = null;
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
        Iterator<Map.Entry<Date, SchoolDay>> sourceIterator = source.entrySet().iterator();
        List<SchoolDay> daysToCreate = new ArrayList<>();
        //Find & perform the inserts and updates, if any
        while(sourceIterator.hasNext()) {
            Map.Entry<Date, SchoolDay> entry = sourceIterator.next();
            SchoolDay schoolDay = entry.getValue();
            SchoolDay edPanelSchoolDay = ed.get(entry.getKey());
            if(null == edPanelSchoolDay){
                daysToCreate.add(schoolDay);
            } else {
                schoolDay.setId(edPanelSchoolDay.getId());
                schoolDay.setSchool(edPanelSchoolDay.getSchool());
                if(!edPanelSchoolDay.equals(schoolDay)) {
                    try {
                        edPanel.updateSchoolDay(school.getId(), schoolDay);
                    } catch (IOException e) {
                        results.schoolDayUpdateFailed(
                                Long.valueOf(schoolDay.getSourceSystemId()),
                                schoolDay.getId());
                        continue;
                    }
                    results.schoolDayUpdated(
                            Long.valueOf(schoolDay.getSourceSystemId()),
                            schoolDay.getId());
                }
            }
        }
        //Perform the bulk creates!
        try {
            List<Long> ids = edPanel.createSchoolDays(school.getId(), daysToCreate);
            //Update the IDS on the source instances for future reference
            if(ids.size() == source.size()) {
                sourceIterator = source.entrySet().iterator();
                int i = 0;
                while(sourceIterator.hasNext()) {
                    SchoolDay curr = sourceIterator.next().getValue();
                    curr.setId(ids.get(i));
                    results.schoolDayCreated(Long.valueOf(curr.getSourceSystemId()), curr.getId());
                    i++;
                }
            }
        } catch (HttpClientException e) {
            for(SchoolDay s: daysToCreate) {
                results.schoolDayCreateFailed(Long.valueOf(s.getSourceSystemId()));
            }
        }
        //Delete anything IN EdPanel that is NOT in source system
        Iterator<Map.Entry<Date, SchoolDay>> edpanelIterator = ed.entrySet().iterator();
        while(edpanelIterator.hasNext()) {
            Map.Entry<Date, SchoolDay> entry = edpanelIterator.next();
            if(!source.containsKey(entry.getKey())) {
                try {
                    edPanel.deleteSchoolDay(school.getId(), entry.getValue());
                } catch (HttpClientException e) {
                    results.schoolDayDeleteFailed(
                            Long.valueOf(entry.getValue().getSourceSystemId()),
                            entry.getValue().getId());
                    continue;
                }
                results.schoolDayDeleted(
                        Long.valueOf(entry.getValue().getSourceSystemId()),
                        entry.getValue().getId());
            }
        }
        return source;
    }

    protected ConcurrentHashMap<Date, SchoolDay> resolveAllFromSourceSystem(Long sourceSchoolId) throws HttpClientException {
        ConcurrentHashMap<Date, SchoolDay> result = new ConcurrentHashMap<>();
        PsResponse<PsCalendarDayWrapper> response = powerSchool.getSchoolCalendarDays(school.getNumber());
        for(PsResponseInner<PsCalendarDayWrapper> wrap : response.record) {
            PsCalendarDay psCalDay = wrap.tables.calendar_day;
            SchoolDay day = psCalDay.toApiModel();
            day.setSchool(school);
            //NOTE: we use id (not the pk, not garunteed unique in PS) and not DCID, because thats the column that
            //gets joined to. :(.  Also, only add the school_day if its 'in session':
            if(psCalDay.insession.equals("1")) {
                result.put(psCalDay.date_value, day);
            }
        }
        return result;
    }

    protected ConcurrentHashMap<Date, SchoolDay> resolveFromEdPanel(Long schoolId) throws HttpClientException {
        SchoolDay[] days = edPanel.getSchoolDays(school.getId());
        ConcurrentHashMap<Date, SchoolDay> dayMap = new ConcurrentHashMap<>();
        for(SchoolDay c: days) {
            Long id = null;
            Long ssid = c.getSourceSystemOtherId();
            if(null != ssid) {
                dayMap.put(c.getDate(), c);
            }
        }
        return dayMap;
    }
}
