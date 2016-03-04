package com.scholarscore.api.persistence.mysql.querygenerator.serializer;

import com.scholarscore.api.persistence.DbMappings;
import com.scholarscore.api.persistence.mysql.querygenerator.SqlGenerationException;
import com.scholarscore.models.query.AggregateFunction;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.MeasureField;
import com.scholarscore.models.query.bucket.AggregationBucket;

import java.util.List;

public interface MeasureSqlSerializer {
    public static final String LEFT_OUTER_JOIN = "LEFT OUTER JOIN ";
    public static final String INNER_JOIN = "INNER JOIN ";
    public static final String ID_COL_SUFFIX = "_id";
    public static final String FK_COL_SUFFIX = "_fk";
    public static final String ON = " ON ";
    public static final String DOT = ".";
    public static final String EQUALS = " = ";

    public String toSelectInner();

    @SuppressWarnings("unchecked")
    default String toSelectBucketPseudoColumn(List<AggregationBucket> buckets) throws SqlGenerationException {
        StringBuilder b = new StringBuilder();
        b.append("CASE \n");
        String fieldInner = toSelectInner();
        for(AggregationBucket bucket: buckets) {
            if(null != bucket.getStart() || null != bucket.getEnd()) {
                if(null != bucket.getStart() && null != bucket.getEnd() &&
                        bucket.getStart().compareTo(bucket.getEnd()) == 1) {
                    throw new SqlGenerationException("Bucket start is greater than end, which is invalid");
                }
                b.append("WHEN ");
                boolean isFirst = true;
                if(null != bucket.getStart()) {
                    b.append(fieldInner);
                    b.append(" >= ");
                    b.append(bucket.getStart());
                    isFirst = false;
                }
                if(null != bucket.getEnd()) {
                    if(!isFirst) {
                        b.append(" AND ");
                    }
                    b.append(fieldInner);
                    b.append(" < ");
                    b.append(bucket.getEnd());
                }
                b.append(" THEN '");
                b.append(bucket.getLabel());
                b.append("'");
                b.append("\n");
            }
        }
        b.append("ELSE NULL \nEND");
        return b.toString();
    }

    default String toSelectClause(AggregateFunction agg) {
        return agg.name() + "(" + toSelectInner() + ")";
    }
    
    String toJoinClause(Dimension dimToJoinUpon);

    String toFromClause();
    
    Dimension toTableDimension();

    Dimension toSecondTableDimension();

//    default String optionalJoinedTable() { return null; }
    
    default String generateMeasureFieldSql(MeasureField f, String tableAlias) throws SqlGenerationException {
        String tableName = DbMappings.MEASURE_TO_TABLE_NAME.get(f.getMeasure());
        if(null != tableAlias) {
            tableName = tableAlias;
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
