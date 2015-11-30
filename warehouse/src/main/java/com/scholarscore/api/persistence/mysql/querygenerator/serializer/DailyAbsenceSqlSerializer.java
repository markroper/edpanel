package com.scholarscore.api.persistence.mysql.querygenerator.serializer;

import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.attendance.AttendanceStatus;
import com.scholarscore.models.attendance.AttendanceTypes;
import com.scholarscore.models.query.AggregateFunction;

/**
 * Created by markroper on 11/29/15.
 */
public class DailyAbsenceSqlSerializer extends AttendanceSqlSerializer {
    @Override
    public String toSelectClause(AggregateFunction agg) {
        return agg.name() +
                "( if(" + HibernateConsts.ATTENDANCE_TABLE + DOT + HibernateConsts.ATTENDANCE_STATUS + " in ('"
                + AttendanceStatus.ABSENT + "') AND "
                + HibernateConsts.ATTENDANCE_TABLE + DOT + HibernateConsts.ATTENDANCE_TYPE + " = '" + AttendanceTypes.DAILY + "', 1, 0))";
    }
}
