package com.scholarscore.api.persistence.mysql.querygenerator.serializer;

import com.scholarscore.api.persistence.mysql.DbMappings;
import com.scholarscore.api.persistence.mysql.querygenerator.SqlGenerationException;
import com.scholarscore.models.query.AggregateFunction;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.MeasureField;

public interface MeasureSqlSerializer {
    public static final String LEFT_OUTER_JOIN = "LEFT OUTER JOIN ";
    public static final String ID_COL_SUFFIX = "_id";
    public static final String FK_COL_SUFFIX = "_fk";
    public static final String ON = " ON ";
    public static final String DOT = ".";
    public static final String EQUALS = " = ";
    
    public String toSelectClause(AggregateFunction agg);
    
    public String toJoinClause(Dimension dimToJoinUpon);
    
    public String toTableName();
    
    public default String generateMeasureFieldSql(MeasureField f) throws SqlGenerationException {
        String tableName = DbMappings.MEASURE_TO_TABLE_NAME.get(f.getMeasure());
        String columnName = DbMappings.MEASURE_FIELD_TO_COL_NAME.get(f);
        if(null == tableName || null == columnName) {
            throw new SqlGenerationException("Invalid dimension, tableName (" + 
                    tableName + ") and columnName (" + 
                    columnName + ") must both be non-null");
        }
        return tableName + "." + columnName;
    }
}
