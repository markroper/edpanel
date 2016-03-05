package com.scholarscore.api.persistence.mysql.querygenerator.serializer.attendance;

import com.scholarscore.api.persistence.mysql.querygenerator.QuerySqlGenerator;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.BaseSqlSerializer;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.attendance.AttendanceStatus;
import com.scholarscore.models.attendance.AttendanceType;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.QueryException;

/**
 * User: jordan
 * Date: 2/17/16
 * Time: 3:49 PM
 */
public abstract class BaseAttendanceSqlSerializer extends BaseSqlSerializer {

    @Override
    public String toSelectInner() {
        AttendanceType attendanceType = attendanceTypeMatches();
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
            // join from student -> attendance -> school day
            return super.toJoinClause(Dimension.STUDENT);
        } else if(dimToJoinUpon.equals(Dimension.SCHOOL)){
            // join from school -> school day -> attendance
            return
                    joinTable(HibernateConsts.SCHOOL_DAY_TABLE,     // table joined TO
                            HibernateConsts.SCHOOL_TABLE,           // table joined FROM
                            HibernateConsts.SCHOOL_FK,     // table TO col
                            QuerySqlGenerator.resolvePrimaryKeyField(HibernateConsts.SCHOOL_TABLE)) // table FROM col
                    +
                        joinTable(HibernateConsts.ATTENDANCE_TABLE,     // table TO (contains _id)
                                HibernateConsts.SCHOOL_DAY_TABLE,           // table FROM (contains _fk)
                                HibernateConsts.SCHOOL_DAY_FK,     // table TO col
                                QuerySqlGenerator.resolvePrimaryKeyField(HibernateConsts.SCHOOL_DAY_TABLE)) // table FROM col 
                    ;
        }
        throw new QueryException("AttendanceSqlSerializer does not support Dimension " + dimToJoinUpon + "!");
    }


    // subclasses MUST, as a minimum, specify the AttendanceStatus they are interested in
    abstract AttendanceStatus attendanceStatusMatches();
    
    // subclasses MAY specify an AttendanceType that should be matched
    AttendanceType attendanceTypeMatches() { 
        return null;
    }

    @Override
    public String toFromClause() {
        return toTableName() + " ";
    }

    @Override
    public Dimension toSecondTableDimension() {
        return Dimension.SCHOOL_DAY;
    }

    @Override
    public Dimension toTableDimension() {
        return Dimension.ATTENDANCE;
    }
}
