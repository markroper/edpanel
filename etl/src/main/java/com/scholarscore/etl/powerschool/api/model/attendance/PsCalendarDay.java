package com.scholarscore.etl.powerschool.api.model.attendance;

import com.scholarscore.etl.IToApiModel;
import com.scholarscore.models.School;
import com.scholarscore.models.attendance.SchoolDay;

import java.time.LocalDate;
import java.util.Date;

/**
 * Created by markroper on 10/30/15.
 */
public class PsCalendarDay implements IToApiModel<SchoolDay> {
    public String note;
    public String insession;
    public String membershipvalue;
    public Long dcid;
    public Long id;
    public LocalDate date_value;
    public Long schoolid;
    public String type;
    public String scheduleid;
    public Long cycle_day_id;

    @Override
    public SchoolDay toApiModel() {
        School s = new School();
        SchoolDay day = new SchoolDay();
        day.setSourceSystemId(String.valueOf(dcid));
        day.setSourceSystemOtherId(id);
        s.setNumber(schoolid);
        day.setSchool(s);
        day.setDate(date_value);
        day.setCycleId(cycle_day_id);
        return day;
    }
}
