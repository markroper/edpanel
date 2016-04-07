package com.scholarscore.etl.powerschool.sync.attendance;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.PowerSchoolSyncResult;
import com.scholarscore.etl.powerschool.api.model.attendance.PsCalendarDay;
import com.scholarscore.etl.powerschool.api.model.attendance.PsCalendarDayWrapper;
import com.scholarscore.etl.powerschool.api.response.PsResponse;
import com.scholarscore.etl.powerschool.api.response.PsResponseInner;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.models.School;
import com.scholarscore.models.attendance.SchoolDay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by markroper on 10/30/15.
 */
public class SchoolDaySync {
    private final static Logger LOGGER = LoggerFactory.getLogger(SchoolDaySync.class);
    protected IAPIClient edPanel;
    protected IPowerSchoolClient powerSchool;
    protected School school;
    protected LocalDate syncCutoff;

    public SchoolDaySync(IAPIClient edPanel,
                      IPowerSchoolClient powerSchool,
                      School s,
                      LocalDate syncCutoff) {
        this.edPanel = edPanel;
        this.powerSchool = powerSchool;
        this.school = s;
        this.syncCutoff = syncCutoff;
    }

    public ConcurrentHashMap<LocalDate, SchoolDay> syncCreateUpdateDelete(PowerSchoolSyncResult results) {
        ConcurrentHashMap<LocalDate, SchoolDay> source = null;
        ConcurrentHashMap<LocalDate, SchoolDay> ed = null;
        try {
            source = resolveAllFromSourceSystem();
        } catch (HttpClientException e) {
            try {
                source = resolveAllFromSourceSystem();
            } catch (HttpClientException ex) {
                LOGGER.error("Unable to fetch school days from PowerSchool for school " + school.getName() +
                        " with ID: " + school.getId());
                results.schoolDaySourceGetFailed(Long.valueOf(school.getSourceSystemId()), school.getId());
                return new ConcurrentHashMap<>();
            }
        }
        try {
            ed = resolveFromEdPanel();
        } catch (HttpClientException e) {
            try {
                ed = resolveFromEdPanel();
            } catch (HttpClientException ex) {
                LOGGER.error("Unable to fetch school days from EdPanel for school " + school.getName() +
                        " with ID: " + school.getId());
                results.schoolDayEdPaneleGetFailed(Long.valueOf(school.getSourceSystemId()), school.getId());
                return new ConcurrentHashMap<>();
            }
        }
        Iterator<Map.Entry<LocalDate, SchoolDay>> sourceIterator = source.entrySet().iterator();
        List<SchoolDay> daysToCreate = new ArrayList<>();
        //Find & perform the inserts and updates, if any
        while(sourceIterator.hasNext()) {
            Map.Entry<LocalDate, SchoolDay> entry = sourceIterator.next();
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
                    } catch (HttpClientException e) {
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
        for (Map.Entry<LocalDate, SchoolDay> entry : ed.entrySet()) {
            if (!source.containsKey(entry.getKey())
                    && entry.getValue().getDate().compareTo(syncCutoff) > 0) {
                try {
                    //We only sync the last year's school days so we can't just delete from ed panel those
                    //that are not in the source system because we want to keep history
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

    protected ConcurrentHashMap<LocalDate, SchoolDay> resolveAllFromSourceSystem() throws HttpClientException {
        ConcurrentHashMap<LocalDate, SchoolDay> result = new ConcurrentHashMap<>();
        PsResponse<PsCalendarDayWrapper> response = powerSchool.getSchoolCalendarDays(school.getNumber());
        for(PsResponseInner<PsCalendarDayWrapper> wrap : response.record) {
            PsCalendarDay psCalDay = wrap.tables.calendar_day;
            SchoolDay day = psCalDay.toApiModel();
            day.setSchool(school);
            //NOTE: we use id (not the pk, not guaranteed unique in PS) and not DCID, because that's the column that
            //gets joined to. :(.  Also, only add the school_day if its 'in session':
            if(psCalDay.insession.equals("1")) {
                result.put(psCalDay.date_value, day);
            }
        }
        return result;
    }

    protected ConcurrentHashMap<LocalDate, SchoolDay> resolveFromEdPanel() throws HttpClientException {
        SchoolDay[] days = edPanel.getSchoolDays(school.getId());
        ConcurrentHashMap<LocalDate, SchoolDay> dayMap = new ConcurrentHashMap<>();
        for(SchoolDay c: days) {
            Long ssid = c.getSourceSystemOtherId();
            if(null != ssid) {
                dayMap.put(c.getDate(), c);
            }
        }
        return dayMap;
    }
}
