package com.scholarscore.api.persistence.mysql.querygenerator.serializer;

import com.scholarscore.api.persistence.DbMappings;
import com.scholarscore.api.persistence.mysql.querygenerator.QuerySqlGenerator;
import com.scholarscore.api.persistence.mysql.querygenerator.SqlGenerationException;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.attendance.AttendanceStatus;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.MeasureField;
import com.scholarscore.models.query.measure.AttendanceMeasure;

public class AttendanceSqlSerializer implements MeasureSqlSerializer {

    @Override
    public String toSelectInner() {
        return "( if(" + HibernateConsts.ATTENDANCE_TABLE + DOT + HibernateConsts.ATTENDANCE_STATUS + " in ('"
                + AttendanceStatus.ABSENT + "'), 1, 0)";
    }

    @Override
    public String toJoinClause(Dimension dimToJoinUpon) {
        String dimTableName = DbMappings.DIMENSION_TO_TABLE_NAME.get(dimToJoinUpon);
        if(dimToJoinUpon.equals(Dimension.STUDENT)) {
            return LEFT_OUTER_JOIN + HibernateConsts.ATTENDANCE_TABLE + ON + dimTableName + DOT + QuerySqlGenerator.resolvePrimaryKeyField(dimTableName) 
                    + EQUALS + HibernateConsts.ATTENDANCE_TABLE + DOT + dimTableName + FK_COL_SUFFIX + " "
                    + LEFT_OUTER_JOIN + HibernateConsts.SCHOOL_DAY_TABLE + ON + HibernateConsts.SCHOOL_DAY_TABLE + DOT + HibernateConsts.SCHOOL_DAY_ID
                    + EQUALS + HibernateConsts.ATTENDANCE_TABLE + DOT + HibernateConsts.SCHOOL_DAY_FK + " ";
        } else if(dimToJoinUpon.equals(Dimension.SCHOOL)){
            return LEFT_OUTER_JOIN + HibernateConsts.SCHOOL_DAY_TABLE + ON + dimTableName + DOT + QuerySqlGenerator.resolvePrimaryKeyField(dimTableName) 
                    + EQUALS + HibernateConsts.SCHOOL_DAY_TABLE + DOT + dimTableName + FK_COL_SUFFIX + " "
                    + LEFT_OUTER_JOIN + HibernateConsts.ATTENDANCE_TABLE + ON + HibernateConsts.SCHOOL_DAY_TABLE + DOT + HibernateConsts.SCHOOL_DAY_ID
                    + EQUALS + HibernateConsts.ATTENDANCE_TABLE + DOT + HibernateConsts.SCHOOL_DAY_FK + " ";
        }
        return null;
    }

    @Override
    public String toTableName() {
        return HibernateConsts.ATTENDANCE_TABLE;
    }
    
    @Override
    public String generateMeasureFieldSql(MeasureField f) throws SqlGenerationException {
        String tableName = DbMappings.MEASURE_TO_TABLE_NAME.get(f.getMeasure());
        //We are actually interrogating the school_day table which is joined in above, in the case of school_fk or date related values
        if(f.getField().equals(AttendanceMeasure.DATE) || f.getField().equals(AttendanceMeasure.SCHOOL_FK)) {
            tableName = HibernateConsts.SCHOOL_DAY_TABLE;
        }
        String columnName = DbMappings.MEASURE_FIELD_TO_COL_NAME.get(f);
        if(null == tableName || null == columnName) {
            throw new SqlGenerationException("Invalid dimension, tableName (" + 
                    tableName + ") and columnName (" + 
                    columnName + ") must both be non-null");
        }
        return tableName + "." + columnName;
    }

}
