package com.scholarscore.etl.powerschool.api.model.attendance;

import com.scholarscore.etl.IToApiModel;
import com.scholarscore.models.attendance.Attendance;
import com.scholarscore.models.attendance.AttendanceTypes;
import com.scholarscore.models.attendance.SchoolDay;
import com.scholarscore.models.user.Student;

import java.time.LocalDate;

/**
 * Created by markroper on 10/30/15.
 */
public class PsAttendance implements IToApiModel<Attendance> {
    public Long periodid;
    public Long lock_teacher_yn;
    public LocalDate att_date;
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
        a.setSourceSystemPeriodId(periodid);
        a.setDescription(att_comment);
        a.setSchoolDay(day);
        a.setAttendanceCode(att_mode_code);
        if(null != att_mode_code && att_mode_code.equals("ATT_ModeDaily")) {
            a.setType(AttendanceTypes.DAILY);
        } else if(null != att_mode_code && att_mode_code.equals("ATT_ModeMeeting")){
            a.setType(AttendanceTypes.SECTION);
        } else if(null == periodid || periodid.equals(0L)) {
            a.setType(AttendanceTypes.DAILY);
        } else {
            a.setType(AttendanceTypes.SECTION);
        }
        stud.setSourceSystemId(String.valueOf(studentid));
        a.setStudent(stud);
        return a;
    }
}
