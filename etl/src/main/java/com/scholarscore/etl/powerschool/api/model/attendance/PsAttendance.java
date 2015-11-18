package com.scholarscore.etl.powerschool.api.model.attendance;

import com.scholarscore.etl.IToApiModel;
import com.scholarscore.models.attendance.Attendance;
import com.scholarscore.models.attendance.SchoolDay;
import com.scholarscore.models.user.Student;

import java.util.Date;

/**
 * Created by markroper on 10/30/15.
 */
public class PsAttendance implements IToApiModel<Attendance> {
    public Long periodid;
    public Long lock_teacher_yn;
    public Date att_date;
    public Long ccid;
    public String att_mode_code;
    public Long ada_value_code;
    public Long id;
    public Long attendance_codeid;
    public String att_flags;
    public Long programid;
    public Long total_minutes;
    public String att_comment;
    public Long lock_reporting_yn;
    public String ip_address;
    public String transaction_type;
    public Long ada_value_time;
    public Long parent_attendanceid;
    public Long yearid;
    public Long adm_value;
    public String whomodifiedtype;
    public Long studentid;
    public Long calendar_dayid;
    public String prog_crse_type;
    public Long dcid;
    public Long whomodifiedid;
    public Long schoolid;
    public String att_interval;

    @Override
    public Attendance toApiModel() {
        Attendance a = new Attendance();
        SchoolDay day = new SchoolDay();
        Student stud = new Student();
        day.setSourceSystemId(String.valueOf(calendar_dayid));
        a.setSourceSystemId(String.valueOf(dcid));
        a.setDescription(att_comment);
        a.setSchoolDay(day);
        a.setAttendanceCode(att_mode_code);
        //TODO: figure out this translation
        //a.setStatus();
        stud.setSourceSystemId(String.valueOf(studentid));
        a.setStudent(stud);
        return a;
    }
}
