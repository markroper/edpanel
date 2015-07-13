package com.scholarscore.api.persistence.mysql.querygenerator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.MeasureSqlSerializer;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.MeasureSqlSerializerFactory;
import com.scholarscore.models.query.AggregateMeasure;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.DimensionField;
import com.scholarscore.models.query.Query;
import com.scholarscore.models.query.expressions.Expression;
import com.scholarscore.models.query.expressions.operands.DateOperand;
import com.scholarscore.models.query.expressions.operands.DimensionOperand;
import com.scholarscore.models.query.expressions.operands.IOperand;
import com.scholarscore.models.query.expressions.operands.NumericOperand;
import com.scholarscore.models.query.expressions.operands.StringOperand;

public class QuerySqlGenerator {
    private static final String SELECT = "SELECT ";
    private static final String FROM = "FROM ";
    private static final String LEFT_OUTER_JOIN = "LEFT OUTER JOIN ";
    private static final String GROUP_BY = "GROUP BY ";
    private static final String WHERE = "WHERE ";
    
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
            for(int i = 1; i < orderedTables.size(); i++) {
                Dimension joinDim = orderedTables.get(i);
                String currentTableName = DbConst.DIMENSION_TO_TABLE_NAME.get(currTable);
                String joinTableName = DbConst.DIMENSION_TO_TABLE_NAME.get(joinDim);
                if(null == currentTableName || null == joinTableName) {
                    throw new SqlGenerationException("Unable to generate JOIN clause due to null table name");
                }
                sqlBuilder.append(LEFT_OUTER_JOIN + joinTableName + " ON ");
                sqlBuilder.append(currentTableName + "." + joinTableName + "_fk = ");
                sqlBuilder.append(joinTableName + "." + joinTableName + "_id ");
                currTable = joinDim;
            }
        }
    }
    
    protected static void populateWhereClause(StringBuilder sqlBuilder, Map<String, Object> params, Query q) 
            throws SqlGenerationException {
        sqlBuilder.append(WHERE);
        expressionToSql(q.getFilter(), params, sqlBuilder);
    }
    
    protected static void expressionToSql(Expression exp, Map<String, Object> params, StringBuilder sqlBuilder) 
            throws SqlGenerationException{
        sqlBuilder.append(" (");
        operandToSql(exp.getLeftHandSide(), params, sqlBuilder);
        sqlBuilder.append(" " + exp.getOperator().name() + " ");
        operandToSql(exp.getRightHandSide(), params, sqlBuilder);
        sqlBuilder.append(") ");
        
    }
    
    protected static void operandToSql(IOperand operand, Map<String, Object> params, StringBuilder sqlBuilder) 
            throws SqlGenerationException {
        switch(operand.getType()) {
            case DATE:
                sqlBuilder.append(" '" + DbConst.resolveTimestamp(((DateOperand)operand).getValue()) + "' ");
                break;
            case DIMENSION:
                sqlBuilder.append(" " + generateDimensionFieldSql( ((DimensionOperand)operand).getValue()) + " ");
                break;
            case NUMERIC:
                sqlBuilder.append(" " + ((NumericOperand)operand).getValue() + " ");
                break;
            case EXPRESSION:
                expressionToSql((Expression) operand, params, sqlBuilder);
                break;
            case STRING:
                String rand = RandomStringUtils.randomAlphabetic(32);
                sqlBuilder.append(" :" + rand + " ");
                params.put(rand, ((StringOperand)operand).getValue());
                break;
            default:
                throw new SqlGenerationException("Operand type not supported: " + operand);
        }
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
