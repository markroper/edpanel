package com.scholarscore.api.querygenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.models.query.AggregateMeasure;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.DimensionField;
import com.scholarscore.models.query.Query;

public class QueryGenerator {
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
        sqlBuilder.append("SELECT ");
        boolean isFirst = true;
        for(DimensionField f: q.getFields()) {
            String tableName = DbConst.DIMENSION_TO_TABLE_NAME.get(f.getDimension());
            String columnName = DbConst.DIMENSION_TO_DB_NAME.get(f);
            if(null == tableName || null == columnName) {
                throw new SqlGenerationException("Invalid dimension, tableName (" + 
                        tableName + ") and columnName (" + 
                        columnName + ") must both be non-null");
            }
            String delimeter = ", ";
            if(isFirst) {
                delimeter = "";
                isFirst = false;
            }
            sqlBuilder.append(delimeter + tableName + "." + columnName); 
        }
        for(AggregateMeasure am: q.getAggregateMeasures()) {
//            String tableName = DbConst.DIMENSION_TO_TABLE_NAME.get(f.getDimension());
//            String columnName = DbConst.DIMENSION_TO_DB_NAME.get(f);
        }
        sqlBuilder.append(" ");
    }
    
    protected static void populateFromClause(StringBuilder sqlBuilder, Query q) throws SqlGenerationException {
        sqlBuilder.append("FROM ");
        List<Dimension> orderedTables = resolveOrderedFromTables(q);
        if(null == orderedTables || orderedTables.isEmpty()) {
            throw new SqlGenerationException("No tables were resolved to query in the FROM clause");
        }
        Dimension currTable = orderedTables.get(0);
        sqlBuilder.append(DbConst.DIMENSION_TO_TABLE_NAME.get(currTable) + " ");
        if(orderedTables.size() > 1) {
            for(Dimension joinDim : orderedTables) {
                String currentTableName = DbConst.DIMENSION_TO_TABLE_NAME.get(currTable);
                String joinTableName = DbConst.DIMENSION_TO_TABLE_NAME.get(joinDim);
                if(null == currentTableName || null == joinTableName) {
                    throw new SqlGenerationException("Unable to generate JOIN clause due to null table name");
                }
                sqlBuilder.append("LEFT OUTER JOIN " + joinTableName + " ON ");
                sqlBuilder.append(currentTableName + "." + currentTableName + "_id = ");
                sqlBuilder.append(joinTableName + "." + currentTableName + "_fk ");
                currTable = joinDim;
            }
        }
    }
    
    //TODO: this hierarchy should really live in the enum itself via compareTo?
    protected static List<Dimension> resolveOrderedFromTables(Query q) throws SqlGenerationException {
        HashSet<Dimension> selectedDims = new HashSet<Dimension>();
        for(DimensionField f: q.getFields()) {
            selectedDims.add(f.getDimension());
        }
        List<Dimension> orderedDimTables = new ArrayList<>();
        
        if(selectedDims.contains(Dimension.STUDENT)) {
            orderedDimTables.add(Dimension.STUDENT);
        }
        if(selectedDims.contains(Dimension.TEACHER)) {
            orderedDimTables.add(Dimension.TEACHER);
        }
        if(selectedDims.contains(Dimension.SECTION)) {
            orderedDimTables.add(Dimension.SECTION);
        }
        if(selectedDims.contains(Dimension.TERM)) {
            orderedDimTables.add(Dimension.TERM);
        }
        if(selectedDims.contains(Dimension.YEAR)) {
            orderedDimTables.add(Dimension.YEAR);
        }
        if(selectedDims.contains(Dimension.COURSE)) {
            orderedDimTables.add(Dimension.COURSE);
        }
        if(selectedDims.contains(Dimension.SUBJECT_AREA)) {
            orderedDimTables.add(Dimension.STUDENT);
        }
        if(selectedDims.contains(Dimension.GRADE_LEVEL)) {
            orderedDimTables.add(Dimension.GRADE_LEVEL);
        }
        if(selectedDims.contains(Dimension.SCHOOL)) {
            orderedDimTables.add(Dimension.SCHOOL);
        }
        return orderedDimTables;
    }
    
    protected static void populateWhereClause(StringBuilder sqlBuilder, Map<String, Object> params, Query q) throws SqlGenerationException {
        //TODO: implement me
    }
    
    protected static void populateGroupByClause(StringBuilder sqlBuilder, Query q) throws SqlGenerationException {
        
    }
}
