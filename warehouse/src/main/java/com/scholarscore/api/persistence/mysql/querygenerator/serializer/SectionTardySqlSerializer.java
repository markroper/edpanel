package com.scholarscore.api.persistence.mysql.querygenerator.serializer;

import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.attendance.AttendanceStatus;
import com.scholarscore.models.attendance.AttendanceTypes;

/**
 * Created by markroper on 11/29/15.
 */
public class SectionTardySqlSerializer extends AttendanceSqlSerializer {
    @Override
    public String toSelectInner() {
        return "if(" + HibernateConsts.ATTENDANCE_TABLE + DOT + HibernateConsts.ATTENDANCE_STATUS + " in ('"
                + AttendanceStatus.TARDY + "') AND "
                + HibernateConsts.ATTENDANCE_TABLE + DOT + HibernateConsts.ATTENDANCE_TYPE + " = '" + AttendanceTypes.SECTION + "', 1, 0)";
    }
}
