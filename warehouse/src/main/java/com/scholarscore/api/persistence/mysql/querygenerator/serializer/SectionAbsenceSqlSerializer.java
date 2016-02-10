package com.scholarscore.api.persistence.mysql.querygenerator.serializer;

import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.attendance.AttendanceStatus;
import com.scholarscore.models.attendance.AttendanceTypes;

/**
 * Created by cwallace on 1/4/16.
 */
public class SectionAbsenceSqlSerializer extends AttendanceSqlSerializer {
    @Override
    public String toSelectInner() {
        return "if(" + HibernateConsts.ATTENDANCE_TABLE + DOT + HibernateConsts.ATTENDANCE_STATUS + " in ('"
                + AttendanceStatus.ABSENT + "') AND "
                + HibernateConsts.ATTENDANCE_TABLE + DOT + HibernateConsts.ATTENDANCE_TYPE + " = '" + AttendanceTypes.SECTION + "', 1, 0)";
    }
}
