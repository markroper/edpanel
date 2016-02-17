package com.scholarscore.api.persistence.mysql.querygenerator.serializer.attendance;

import com.scholarscore.api.persistence.DbMappings;
import com.scholarscore.api.persistence.mysql.querygenerator.QuerySqlGenerator;
import com.scholarscore.api.persistence.mysql.querygenerator.SqlGenerationException;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.BaseSqlSerializer;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.MeasureSqlSerializer;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.attendance.AttendanceStatus;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.MeasureField;
import com.scholarscore.models.query.measure.AttendanceMeasure;

public class AttendanceSqlSerializer extends BaseAttendanceSqlSerializer implements MeasureSqlSerializer {
    
    @Override
    AttendanceStatus attendanceStatusMatches() {
        return AttendanceStatus.ABSENT;
    }

    @Override
    public String toJoinClause(Dimension dimToJoinUpon) {
        if(dimToJoinUpon.equals(Dimension.STUDENT)) {
            return super.toJoinClause(dimToJoinUpon)  + joinTable(HibernateConsts.SCHOOL_DAY_TABLE);
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
        // TODO Jordan: throw new SqlGenerationException("AttendanceSqlSerializer does not support Dimension " + dimToJoinUpon + "!");
        return null;
    }

    @Override
    public String toFromClause() {
        return toTableName();
    }

    @Override
    public String toTableName() {
        return HibernateConsts.ATTENDANCE_TABLE;
    }
    
    @Override
    public String generateMeasureFieldSql(MeasureField f, String tableAlias) throws SqlGenerationException {
        String tableName = DbMappings.MEASURE_TO_TABLE_NAME.get(f.getMeasure());
        if(null != tableAlias) {
            tableName = tableAlias;
        }
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
