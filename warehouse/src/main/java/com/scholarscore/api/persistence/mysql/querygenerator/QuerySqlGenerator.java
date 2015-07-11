package com.scholarscore.api.persistence.mysql.querygenerator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.MeasureSqlSerializer;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.MeasureSqlSerializerFactory;
import com.scholarscore.models.query.AggregateMeasure;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.DimensionField;
import com.scholarscore.models.query.Query;

public class QuerySqlGenerator {
    private static final String SELECT = "SELECT ";
    private static final String FROM = "FROM ";
    private static final String LEFT_OUTER_JOIN = "LEFT OUTER JOIN ";
    private static final String GROUP_BY = "GROUP BY ";
    
    public static SqlWithParameters generate(Query q) throws SqlGenerationException {
        Map<String, Object> params = new HashMap<>();
        StringBuilder sqlBuilder = new StringBuilder();
        
        populateSelectClause(sqlBuilder, params, q);
        populateFromClause(sqlBuilder, q);
        populateWhereClause(sqlBuilder, params, q);
        populateGroupByClause(sqlBuilder, q);
        return new SqlWithParameters(sqlBuilder.toString(), params);
    }
    
    protected static void populateSelectClause(StringBuilder sqlBuilder, Map<String, Object> params, Query q) 
            throws SqlGenerationException {
        sqlBuilder.append(SELECT);
        boolean isFirst = true;
        for(DimensionField f: q.getFields()) {
            String delimeter = ", ";
            if(isFirst) {
                delimeter = "";
                isFirst = false;
            }
            sqlBuilder.append(delimeter + generateDimensionFieldSql(f)); 
        }
        for(AggregateMeasure am: q.getAggregateMeasures()) {
            MeasureSqlSerializer mss = MeasureSqlSerializerFactory.get(am.getMeasure());
            sqlBuilder.append(", " + mss.toSelectClause(am.getAggregation()));
        }
        sqlBuilder.append(" ");
    }
    
    protected static void populateFromClause(StringBuilder sqlBuilder, Query q) throws SqlGenerationException {
        sqlBuilder.append(FROM);
        
        //Get the dimensions in the correct order for joining:
        HashSet<Dimension> selectedDims = new HashSet<Dimension>();
        for(DimensionField f: q.getFields()) {
            selectedDims.add(f.getDimension());
        }
        List<Dimension> orderedTables = Dimension.resolveOrderedDimensions(selectedDims);
        if(null == orderedTables || orderedTables.isEmpty()) {
            throw new SqlGenerationException("No tables were resolved to query in the FROM clause");
        }
        
        //Use the first dimension in the sorted columns as the FROM table
        Dimension currTable = orderedTables.get(0);
        sqlBuilder.append(DbConst.DIMENSION_TO_TABLE_NAME.get(currTable) + " ");
        
        //Join in the measures table on the FROM table
        AggregateMeasure am = q.getAggregateMeasures().get(0);
        MeasureSqlSerializer mss = MeasureSqlSerializerFactory.get(am.getMeasure());
        sqlBuilder.append(mss.toJoinClause(currTable));
        
        //Join in the remaining dimensions tables, if any
        if(orderedTables.size() > 1) {
            for(Dimension joinDim : orderedTables) {
                String currentTableName = DbConst.DIMENSION_TO_TABLE_NAME.get(currTable);
                String joinTableName = DbConst.DIMENSION_TO_TABLE_NAME.get(joinDim);
                if(null == currentTableName || null == joinTableName) {
                    throw new SqlGenerationException("Unable to generate JOIN clause due to null table name");
                }
                sqlBuilder.append(LEFT_OUTER_JOIN + joinTableName + " ON ");
                sqlBuilder.append(currentTableName + "." + currentTableName + "_id = ");
                sqlBuilder.append(joinTableName + "." + currentTableName + "_fk ");
                currTable = joinDim;
            }
        }
    }
    
    protected static void populateWhereClause(StringBuilder sqlBuilder, Map<String, Object> params, Query q) 
            throws SqlGenerationException {
        //TODO: implement me
    }
    
    protected static void populateGroupByClause(StringBuilder sqlBuilder, Query q) throws SqlGenerationException {
        sqlBuilder.append(GROUP_BY);
        boolean isFirst = true;
        for(DimensionField f: q.getFields()) {
            String delimeter = ", ";
            if(isFirst) {
                delimeter = "";
                isFirst = false;
            }
            sqlBuilder.append(delimeter + generateDimensionFieldSql(f)); 
        }
    }
    
    protected static String generateDimensionFieldSql(DimensionField f) throws SqlGenerationException {
        String tableName = DbConst.DIMENSION_TO_TABLE_NAME.get(f.getDimension());
        String columnName = DbConst.DIMENSION_TO_COL_NAME.get(f);
        if(null == tableName || null == columnName) {
            throw new SqlGenerationException("Invalid dimension, tableName (" + 
                    tableName + ") and columnName (" + 
                    columnName + ") must both be non-null");
        }
        return tableName + "." + columnName;
    }
}
