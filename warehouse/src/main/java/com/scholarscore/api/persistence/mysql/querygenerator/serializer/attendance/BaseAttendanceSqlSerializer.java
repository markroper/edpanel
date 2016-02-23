package com.scholarscore.api.persistence.mysql.querygenerator.serializer.attendance;

import com.scholarscore.api.persistence.mysql.querygenerator.QuerySqlGenerator;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.BaseSqlSerializer;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.attendance.AttendanceStatus;
import com.scholarscore.models.attendance.AttendanceTypes;
import com.scholarscore.models.query.Dimension;

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
                + attendanceStatusMatches() + "')" + attendanceTypeString + ", 1, " + valueForFalse() + ")";
    }

    @Override
    public String toJoinClause(Dimension dimToJoinUpon) {
        if(dimToJoinUpon.equals(Dimension.STUDENT)) {
            return super.toJoinClause(dimToJoinUpon) + joinTable(HibernateConsts.SCHOOL_DAY_TABLE);
        } else if(dimToJoinUpon.equals(Dimension.SCHOOL)){
            // this may be confusing because SCHOOL_DAY_TABLE is the argument for both, but this is actually expected, here's why -
            // * toJoinClause is joining this sqlizer's dimension (ATTENDANCE) to the supplied dimension (SCHOOL_DAY)
            // * joinTable is joining SchoolDay to the supplied table
            return toJoinClause(HibernateConsts.SCHOOL_DAY_TABLE) +
                    joinTable(HibernateConsts.SCHOOL_DAY_TABLE,     // table TO
                            HibernateConsts.SCHOOL_TABLE,           // table FROM
                            HibernateConsts.SCHOOL_FK,     // table TO col
                            QuerySqlGenerator.resolvePrimaryKeyField(HibernateConsts.SCHOOL_TABLE) // table FROM col
                    );
        }
        // TODO: throw new SqlGenerationException("AttendanceSqlSerializer does not support Dimension " + dimToJoinUpon + "!");
        return null;
    }


    // subclasses MUST, as a minimum, specify the AttendanceStatus they are interested in
    abstract AttendanceStatus attendanceStatusMatches();
    
    // subclasses MAY specify an AttendanceType that should be matched
    AttendanceTypes attendanceTypeMatches() { 
        return null;
    }

    @Override
    public String toTableName() {
        return HibernateConsts.ATTENDANCE_TABLE;
    }
}
