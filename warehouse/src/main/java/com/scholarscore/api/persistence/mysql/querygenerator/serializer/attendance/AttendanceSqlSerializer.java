package com.scholarscore.api.persistence.mysql.querygenerator.serializer.attendance;

import com.scholarscore.api.persistence.DbMappings;
import com.scholarscore.api.persistence.mysql.querygenerator.SqlGenerationException;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.MeasureSqlSerializer;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.attendance.AttendanceStatus;
import com.scholarscore.models.query.MeasureField;
import com.scholarscore.models.query.measure.AttendanceMeasure;

public class AttendanceSqlSerializer extends BaseAttendanceSqlSerializer implements MeasureSqlSerializer {
    
    @Override
    AttendanceStatus attendanceStatusMatches() {
        return AttendanceStatus.ABSENT;
    }
    
    @Override
    protected String valueForFalse() { return "null"; }
    
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
