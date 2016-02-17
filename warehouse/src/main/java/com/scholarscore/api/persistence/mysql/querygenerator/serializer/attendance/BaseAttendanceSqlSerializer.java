package com.scholarscore.api.persistence.mysql.querygenerator.serializer.attendance;

import com.scholarscore.api.persistence.mysql.querygenerator.serializer.BaseSqlSerializer;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.attendance.AttendanceStatus;
import com.scholarscore.models.attendance.AttendanceTypes;

/**
 * User: jordan
 * Date: 2/17/16
 * Time: 3:49 PM
 */
public abstract class BaseAttendanceSqlSerializer extends BaseSqlSerializer {

    @Override
    public String toSelectInner() {
        AttendanceTypes attendanceType = attendanceTypeMatches();
        String attendanceTypeString = "";
        if (attendanceType != null) {
            attendanceTypeString = " AND " + toTableName() + DOT + HibernateConsts.ATTENDANCE_TYPE + " = '" + attendanceType + "'";
        }
        return "if(" + toTableName() + DOT + HibernateConsts.ATTENDANCE_STATUS + " in ('"
                + attendanceStatusMatches() + "')" + attendanceTypeString + ", 1, 0)";
    }

    // subclasses MUST, as a minimum, specify the AttendanceStatus they are interested in
    abstract AttendanceStatus attendanceStatusMatches();
    
    // subclasses MAY specify an AttendanceType that should be matched
    AttendanceTypes attendanceTypeMatches() { 
        return null;
    }
}
