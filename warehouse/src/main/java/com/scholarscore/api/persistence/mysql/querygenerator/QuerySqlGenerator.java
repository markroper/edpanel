package com.scholarscore.api.persistence.mysql.querygenerator;

import com.scholarscore.api.persistence.DbMappings;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.MeasureSqlSerializer;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.MeasureSqlSerializerFactory;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.query.AggregateFunction;
import com.scholarscore.models.query.AggregateMeasure;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.DimensionField;
import com.scholarscore.models.query.Measure;
import com.scholarscore.models.query.MeasureField;
import com.scholarscore.models.query.Query;
import com.scholarscore.models.query.SubqueryColumnRef;
import com.scholarscore.models.query.SubqueryExpression;
import com.scholarscore.models.query.expressions.Expression;
import com.scholarscore.models.query.expressions.operands.DateOperand;
import com.scholarscore.models.query.expressions.operands.DimensionOperand;
import com.scholarscore.models.query.expressions.operands.IOperand;
import com.scholarscore.models.query.expressions.operands.ListNumericOperand;
import com.scholarscore.models.query.expressions.operands.MeasureOperand;
import com.scholarscore.models.query.expressions.operands.NumericOperand;
import com.scholarscore.models.query.expressions.operands.StringOperand;
import com.scholarscore.models.query.expressions.operators.ComparisonOperator;
import com.scholarscore.models.query.expressions.operators.IOperator;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The query generator provides a single public method generate(Query q), which returns an object 
 * containing a SQL string and a set of parameters to be sent to the database for execution via JDBC.
 * 
 * @author markroper
 *
 */
public abstract class QuerySqlGenerator {
    private static final String SELECT = "SELECT ";
    private static final String FROM = "\nFROM ";
    private static final String LEFT_OUTER_JOIN = "\nLEFT OUTER JOIN ";
    private static final String GROUP_BY = "\nGROUP BY ";
    private static final String WHERE = "\nWHERE ";
    private static final String OR = "OR";
    private static final String AND = "AND";
    private static final String ON = "ON";
    private static final String GREATER_THAN = "GREATER_THAN";
    private static final String GREATER_THAN_OR_EQUAL = "GREATER_THAN_OR_EQUAL";
    private static final String LESS_THAN = "LESS_THAN";
    private static final String LESS_THAN_OR_EQUAL = "LESS_THAN_OR_EQUAL";
    private static final String IN = "IN";
    private static final String LIKE = "LIKE";
    private static final String EQUAL = "EQUAL";
    private static final String NOT_EQUAL = "NOT_EQUAL";
    private static final String ID_SUFFIX = "_id";
    private static final String FK_SUFFIX = "_fk";
    private static final String DELIM = ", ";
    private static final String DOT = ".";
    
    public static SqlWithParameters generate(Query q) throws SqlGenerationException {
        addAnyNecessaryJoinTables(q);
        Map<String, Object> params = new HashMap<>();
        StringBuilder sqlBuilder = new StringBuilder();
        populateSelectClause(sqlBuilder, params, q);
        populateFromClause(sqlBuilder, q);
        populateWhereClause(sqlBuilder, params, q, null);
        populateGroupByClause(sqlBuilder, q, params);
        SqlWithParameters sql = new SqlWithParameters(sqlBuilder.toString(), params);
        if(null != q.getSubqueryColumnsByPosition()) {
            return generateQueryOfSubquery(q, sql);
        }
        return sql;
    }
    
    private static void addAnyNecessaryJoinTables(Query q) {
        boolean queryHasPath = QuerySqlPathHelper.queryHasCompletePath(q);
        if (!queryHasPath) {
            System.out.println("Detected Query w/o path! Attempting to automatically find join path.");
            QuerySqlPathHelper.calculateAndAddAdditionalNeededDimensions(q);
        }
    }

    /**
     * Generates a non-root query. One that references only things within the subquery.
     *
     * @param q
     * @param child
     * @return
     * @throws SqlGenerationException
     */
    private static SqlWithParameters generateQueryOfSubquery(Query q, SqlWithParameters child) throws SqlGenerationException {
        Map<String, Object> params = new HashMap<>();
        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder groupByBuilder = new StringBuilder();
        groupByBuilder.append(" GROUP BY ");
        String tableAlias = "subq_1";
        sqlBuilder.append(SELECT);
        int numChildDimensions = 0;
        if(null != q.getFields()) {
            numChildDimensions = q.getFields().size();
        }
        int numAggregateMeasures = 0;
        if(null != q.getAggregateMeasures()) {
            numAggregateMeasures = q.getAggregateMeasures().size();
            for(AggregateMeasure am: q.getAggregateMeasures()) {
                if(null != am.getBuckets() && !am.getBuckets().isEmpty()) {
                    numAggregateMeasures++;
                }
            }
        }
        //SELECT CLAUSE
        toSqlSelectAgainstSubquery(sqlBuilder, groupByBuilder, q, tableAlias, numChildDimensions, numAggregateMeasures);
        //FROM CLAUSE
        sqlBuilder.append(" \nFROM (\n");
        sqlBuilder.append(child.getSql());
        sqlBuilder.append("\n) as ");
        sqlBuilder.append(tableAlias);
        sqlBuilder.append(" \n");
        //WHERE CLAUSE
        if(null != q.getSubqueryFilter() && !q.getSubqueryFilter().isEmpty()) {
            sqlBuilder.append(WHERE);
            FirstAwareWrapper innerSqlBuilder = new FirstAwareWrapper(sqlBuilder);
            for(SubqueryExpression entry: q.getSubqueryFilter()) {
                Integer pos = entry.getPosition();
                IOperator operator = entry.getOperator();
                IOperand operand = entry.getOperand();
                if(pos > numChildDimensions - 1) {
                    innerSqlBuilder.markNotFirstOrAppend(" AND ");
                    pos = pos - numChildDimensions;
                    int counter = 0;
                    for(AggregateMeasure am: q.getAggregateMeasures()) {
                        if(counter == pos) {
                            sqlBuilder.append(tableAlias + DOT + generateAggColumnName(am));
                            sqlBuilder.append(" ");
                            sqlBuilder.append(resolveOperatorSql(operator));
                            sqlBuilder.append(" ");
                            operandToSql(operand, params, sqlBuilder, tableAlias);
                            break;
                        }
                        counter++;
                        if(null != am.getBuckets() && !am.getBuckets().isEmpty()) {
                            if(counter == pos) {
                                sqlBuilder.append(tableAlias + DOT + generateBucketPseudoColumnName(am));
                                sqlBuilder.append(" ");
                                sqlBuilder.append(resolveOperatorSql(operator));
                                sqlBuilder.append(" ");
                                operandToSql(operand, params, sqlBuilder, tableAlias);
                                break;
                            }
                            counter++;
                        }
                    }
                } else {
                    innerSqlBuilder.markNotFirstOrAppend(DELIM);
                    //look for the dimension
                    sqlBuilder.append(generateDimensionFieldSql(q.getFields().get(pos), tableAlias));
                    sqlBuilder.append(" ");
                    sqlBuilder.append(resolveOperatorSql(operator));
                    sqlBuilder.append(" ");
                    operandToSql(operand, params, sqlBuilder, tableAlias);
                }
            }
        }
        //GROUP BY
        sqlBuilder.append(groupByBuilder.toString());
        params.putAll(child.getParams());
        return new SqlWithParameters(sqlBuilder.toString(), params);
    }

    protected static void populateSelectClause(StringBuilder sqlBuilder, Map<String, Object> params, Query q)
            throws SqlGenerationException {
        sqlBuilder.append(SELECT);
        boolean isFirst = true;
        if(null != q.getFields()) {
            for (DimensionField f : q.getFields()) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    sqlBuilder.append(DELIM);
                }
                sqlBuilder.append(generateDimensionFieldSql(f, null));
            }
        }
        if (q.getAggregateMeasures() != null) {
            for (AggregateMeasure am : q.getAggregateMeasures()) {
                MeasureSqlSerializer mss = MeasureSqlSerializerFactory.get(am.getMeasure());
                if(!isFirst) {
                    sqlBuilder.append(DELIM);
                    isFirst = false;
                }
                sqlBuilder.append(mss.toSelectClause(am.getAggregation()) + " as " + generateAggColumnName(am));
                //If there are buckets involved in the aggregate query, inject the bucket pseudo column
                if(null != am.getBuckets() && !am.getBuckets().isEmpty()) {
                    sqlBuilder.append(DELIM);
                    sqlBuilder.append(mss.toSelectBucketPseudoColumn(am.getBuckets()));
                    sqlBuilder.append(" as ");
                    sqlBuilder.append(generateBucketPseudoColumnName(am));
                }
            }
        }
        sqlBuilder.append(" ");
    }

    protected static void populateFromClause(StringBuilder sqlBuilder, Query q) throws SqlGenerationException {
        sqlBuilder.append(FROM);

        //Get the dimensions in the correct order for joining:
        HashSet<Dimension> selectedDims = new HashSet<>();
        if(null != q.getFields()) {
            for (DimensionField f : q.getFields()) {
                selectedDims.add(f.getDimension());
            }
        }
        // if any hints are included, use them 
        if (null != q.getJoinTables()) {
            for (Dimension d : q.getJoinTables()) {
                selectedDims.add(d);
            }
        }
        //Add any dimensions to join that may be referenced only in the WHERE clause
        Set<Dimension> filterDims = q.resolveFilterDimensions();
        if(null != filterDims) {
            selectedDims.addAll(filterDims);
        }
        List<Dimension> orderedTables = Dimension.resolveOrderedDimensions(selectedDims);
        
        //Use the first dimension in the sorted columns as the FROM table
        if(null != orderedTables && !orderedTables.isEmpty()) {
            //Use the first dimension in the sorted columns as the FROM table
            Dimension currTable = orderedTables.get(0);
            sqlBuilder.append(DbMappings.DIMENSION_TO_TABLE_NAME.get(currTable) + " ");
            //Join in the measures table on the FROM table (if there is a measure)
            AggregateMeasure am = null;
            MeasureSqlSerializer mss = null;
            if (q.getAggregateMeasures() != null && q.getAggregateMeasures().size() > 0) {
                am = q.getAggregateMeasures().get(0);
                mss = MeasureSqlSerializerFactory.get(am.getMeasure());
                sqlBuilder.append(mss.toJoinClause(currTable));
            }
            //Join in the remaining dimensions tables, if any
            if(orderedTables.size() > 1) {
                for(int i = 1; i < orderedTables.size(); i++) {
                    Dimension joinDim = orderedTables.get(i);
                    String currentTableName = DbMappings.DIMENSION_TO_TABLE_NAME.get(currTable);
                    //If the next dimension is not compatible with the previous table for joining, that is, there is no
                    //PK/FK relationship between the two, try to join on the measure table directly. If the measure is
                    //not compatible with the dimension for joining, try joining on the previous dimension in the hierarchy.
                    //If that doesn't match, check the dimension before that.
                    if (Dimension.buildDimension(currTable).getParentDimensions() != null &&
                            !Dimension.buildDimension(currTable).getParentDimensions().contains(joinDim)) {
                        if (measureIsCompatible(am, joinDim)){
                            currentTableName = mss.toTableName();
                        } else {
                            currentTableName = getDimensionJoinOrThrowException(orderedTables.subList(0, i-1), currTable, joinDim);
                        }
                    }
                    String joinTableName = DbMappings.DIMENSION_TO_TABLE_NAME.get(joinDim);
                    if(null == currentTableName || null == joinTableName) {
                        throw new SqlGenerationException("Unable to generate JOIN clause due to null table name");
                    }
                    sqlBuilder.append(LEFT_OUTER_JOIN + joinTableName + " " + ON + " ");
                    sqlBuilder.append(joinTableName + DOT + resolvePrimaryKeyField(joinTableName) + " = ");
                    //Assignment is simplified to the user which means it can be ambiguous which dimension to actually join on
                    //So we join on assignment if we're dealing with anything up a student or an assignment table directly
                    if(currentTableName.equals(HibernateConsts.STUDENT_ASSIGNMENT_TABLE) &&
                            !joinTableName.equals(HibernateConsts.STUDENT_TABLE) &&
                            !joinTableName.equals(HibernateConsts.ASSIGNMENT_TABLE)) {
                        currentTableName = HibernateConsts.ASSIGNMENT_TABLE;
                    }
                    sqlBuilder.append(currentTableName + DOT + joinTableName + FK_SUFFIX + " ");
                    currTable = joinDim;
                }
            }
        } else if (null != q.getAggregateMeasures() && q.getAggregateMeasures().size() > 0) {
            //There are no dimensions, query off the measure table directly.
            if (q.getAggregateMeasures() != null && q.getAggregateMeasures().size() > 0) {
                AggregateMeasure am = q.getAggregateMeasures().get(0);
                MeasureSqlSerializer mss = MeasureSqlSerializerFactory.get(am.getMeasure());
                sqlBuilder.append(mss.toFromClause());
            }
        } else {
            throw new SqlGenerationException("No tables were resolved to query in the FROM clause");
        }


    }

    private static String getDimensionJoinOrThrowException(List<Dimension> orderedTables, Dimension dimDesc, Dimension joinDim) throws SqlGenerationException {
        //Start with the previous dimension since we're already dealing with i and i-1...
        int descIndex = orderedTables.size()-1;
        while(descIndex >= 0 && !Dimension.buildDimension(dimDesc).getParentDimensions().contains(joinDim)) {
            dimDesc = orderedTables.get(descIndex);
            descIndex--;
        }
        if(Dimension.buildDimension(dimDesc).getParentDimensions().contains(joinDim)) {
            return DbMappings.DIMENSION_TO_TABLE_NAME.get(dimDesc);
        } else {
            throw new SqlGenerationException(
                    "Cannot join dimension to either previous dimension or measure: " + joinDim);
        }
    }

    private static boolean measureIsCompatible(AggregateMeasure aggregateMeasure, Dimension dimension) {
        return (aggregateMeasure != null && aggregateMeasure.getMeasure() != null
                && Measure.buildMeasure(aggregateMeasure.getMeasure()).getCompatibleDimensions().contains(dimension));
    }

    /**
     * Breaking with our pattern for all other tables in the system, the Student, Teacher & Administrator
     * tables do not have a primary key field called TABLENAME_id.  This is because they have a unique FK
     * to the users table which serves as the ID.  Therefore, we have to special case the generation of the join
     * SQL for these tables because they don't follow the pattern :(.
     *
     * @param tableName
     * @return
     */
    public static String resolvePrimaryKeyField(String tableName) {
        String primaryKeyFieldReference = tableName + ID_SUFFIX;
        if(tableName.equals(HibernateConsts.STUDENT_TABLE) ||
                tableName.equals(HibernateConsts.STAFF_TABLE)) {
            primaryKeyFieldReference = tableName + "_user_fk";
        }
        return primaryKeyFieldReference;
    }

    protected static void populateWhereClause(StringBuilder sqlBuilder, Map<String, Object> params, Query q, String tableAlias)
            throws SqlGenerationException {
        if(null == q.getFilter()) {
            return;
        }
        sqlBuilder.append(WHERE);
        expressionToSql(q.getFilter(), params, sqlBuilder, tableAlias);
    }

    protected static void expressionToSql(Expression exp, Map<String, Object> params, StringBuilder sqlBuilder, String tableAlias)
            throws SqlGenerationException{
        sqlBuilder.append(" (");
        //Serialize the left hand side
        operandToSql(exp.getLeftHandSide(), params, sqlBuilder, tableAlias);
        //Serialize the operator
        String operator = resolveOperatorSql(exp.getOperator());
        sqlBuilder.append(" " + operator + " ");
        if (exp.getOperator().equals((ComparisonOperator.IN))) {
            sqlBuilder.append(" (");
        }
        //Serialize the right hand side
        operandToSql(exp.getRightHandSide(), params, sqlBuilder, tableAlias);
        if(exp.getOperator().equals(ComparisonOperator.IN)) {
            sqlBuilder.append(") ");
        }
        sqlBuilder.append(") ");
    }

    protected static void operandToSql(IOperand operand, Map<String, Object> params, StringBuilder sqlBuilder, String tableAlias)
            throws SqlGenerationException {
        switch(operand.getType()) {
            case DATE:
                sqlBuilder.append(" '" + DbMappings.resolveTimestamp(((DateOperand)operand).getValue()) + "' ");
                break;
            case DIMENSION:
                sqlBuilder.append(" " + generateDimensionFieldSql( ((DimensionOperand)operand).getValue(), tableAlias) + " ");
                break;
            case MEASURE:
                MeasureOperand mo = (MeasureOperand)operand;
                try {
                    //This will throw an exception if the where clause field is not an agg function.
                    //We catch that exception and look for a field on the measure table with the name in that case.
                    AggregateFunction func = AggregateFunction.valueOf(mo.getValue().getField());
                    MeasureSqlSerializer mss = MeasureSqlSerializerFactory.get(mo.getValue().getMeasure());
                    sqlBuilder.append(mss.toSelectClause(func));
                } catch(RuntimeException e) {
                    MeasureSqlSerializer mss = MeasureSqlSerializerFactory.get(((MeasureOperand)operand).getValue().getMeasure());
                    sqlBuilder.append(" " + generateMeasureFieldSql(((MeasureOperand)operand).getValue(), tableAlias) + " ");
                }
                break;
            case NUMERIC:
                sqlBuilder.append(" " + ((NumericOperand)operand).getValue() + " ");
                break;
            case EXPRESSION:
                expressionToSql((Expression) operand, params, sqlBuilder, tableAlias);
                break;
            case STRING:
                //Protect against SQL injection attack:
                String rand = RandomStringUtils.randomAlphabetic(32);
                sqlBuilder.append(" :" + rand + " ");
                params.put(rand, ((StringOperand)operand).getValue());
                break;
            case LIST_NUMERIC:
                List<Number> nums = ((ListNumericOperand)operand).getValue();
                for(int i = 0; i < nums.size(); i++) {
                    sqlBuilder.append(nums.get(i));
                    if(i != nums.size() -1) {
                        sqlBuilder.append(",");
                    }
                }
                break;
            default:
                throw new SqlGenerationException("Operand type not supported: " + operand);
        }
    }
    
    protected static void populateGroupByClause(StringBuilder parentSqlBuilder, Query q, Map<String, Object> params) throws SqlGenerationException {
        StringBuilder groupBySqlBuilder = new StringBuilder();
        boolean isFirst = true;
        if(null != q.getFields()) {
            for (DimensionField f : q.getFields()) {
                if (isFirst) {
                    groupBySqlBuilder.append(generateDimensionFieldSql(f, null));
                    isFirst = false;
                } else {
                    groupBySqlBuilder.append(DELIM + generateDimensionFieldSql(f, null));
                }
            }
        }
        //If there are buckets involved in the aggregate query, inject the bucket pseudo column
        if(null != q.getAggregateMeasures()) {
            for (AggregateMeasure m : q.getAggregateMeasures()) {
                if(null != m.getBuckets() && !m.getBuckets().isEmpty()) {
                    String bucketFieldName = generateBucketPseudoColumnName(m);
                    if (isFirst) {
                        groupBySqlBuilder.append(bucketFieldName);
                        isFirst = false;
                    } else {
                        groupBySqlBuilder.append(DELIM + bucketFieldName);
                    }
                }
            }
        }
        
        // "GROUP BY" may not be present -- only append "GROUP BY" if the rest of the string exists
        if (groupBySqlBuilder.length() > 0) {
            parentSqlBuilder.append(GROUP_BY);
            parentSqlBuilder.append(groupBySqlBuilder.toString());
			if(null != q.getHaving()) {
            	parentSqlBuilder.append(" \nHAVING ");
            	expressionToSql(q.getHaving(), params, parentSqlBuilder, null);
        	}
        }
    }

    public static String generateBucketPseudoColumnName(AggregateMeasure m) {
        return m.getAggregation().name().toLowerCase() + "_" + m.getMeasure().name().toLowerCase() + "_group";
    }
    public static String generateAggColumnName(AggregateMeasure m) {
        return m.getAggregation().name().toLowerCase() + "_" + m.getMeasure().name().toLowerCase() + "_agg";
    }
    protected static String generateDimensionFieldSql(DimensionField f, String tableAlias) throws SqlGenerationException {
        String tableName = DbMappings.DIMENSION_TO_TABLE_NAME.get(f.getDimension());
        if(null != tableAlias) {
            tableName = tableAlias;
        }
        String columnName = DbMappings.DIMENSION_TO_COL_NAME.get(f);
        if(null == tableName || null == columnName) {
            throw new SqlGenerationException("Invalid dimension, tableName (" + 
                    tableName + ") and columnName (" + 
                    columnName + ") must both be non-null");
        }
        return tableName + DOT + columnName;
    }
    
    protected static String generateMeasureFieldSql(MeasureField f, String tableAlias) throws SqlGenerationException {
        return MeasureSqlSerializerFactory.get(f.getMeasure()).generateMeasureFieldSql(f, tableAlias);
    }
    
    protected static String resolveOperatorSql(IOperator op) throws SqlGenerationException {
        String operator = null;
        switch(op.name()) {
            case OR:
                operator = OR;
                break;
            case AND:
                operator = AND;
                break;
            case EQUAL:
                operator = "=";
                break;
            case NOT_EQUAL:
                operator = "!=";
                break;
            case GREATER_THAN:
                operator = ">";
                break;
            case GREATER_THAN_OR_EQUAL:
                operator = ">=";
                break;
            case LESS_THAN:
                operator = "<";
                break;
            case LESS_THAN_OR_EQUAL:
                operator = "<=";
                break;
            case IN:
                operator = IN;
                break;
            case LIKE:
                operator = LIKE;
                break;
            default:
                throw new SqlGenerationException("Operator not supported: " + op.name());
        }
        return operator;
    }
    private static void toSqlSelectAgainstSubquery(StringBuilder sqlBuilder, StringBuilder groupByBuilder,
                                                   Query q, String tableAlias, Integer numChildDimensions,
                                                   Integer numAggregateMeasures) throws SqlGenerationException {
        FirstAwareWrapper innerSqlBuilder = new FirstAwareWrapper(sqlBuilder);
        FirstAwareWrapper innerGroupByBuilder = new FirstAwareWrapper(groupByBuilder);
        //For every column, pluck the correct dimension or measure from the subquery
        for(SubqueryColumnRef col: q.getSubqueryColumnsByPosition()) {
            Integer pos = col.getPosition();
            AggregateFunction function = col.getFunction();
            if(-1 == pos) {
                innerSqlBuilder.markNotFirstOrAppend(DELIM);
                innerSqlBuilder.append(function.name() + "(*)");
            } else if(pos > numChildDimensions - 1) {
                //Find the right agg measure
                pos -= numChildDimensions;
                if(pos < numAggregateMeasures) {
                    innerSqlBuilder.markNotFirstOrAppend(DELIM);
                    int counter = 0;
                    for(AggregateMeasure am: q.getAggregateMeasures()) {
                        if(counter == pos) {
                            if(null != function) {
                                innerSqlBuilder.append(function.name() + ")");
                            }
                            innerSqlBuilder.append(tableAlias + DOT + generateAggColumnName(am));
                            if(null != function) {
                                innerSqlBuilder.append(function.name() + ")");
                            } else {
                                innerGroupByBuilder.markNotFirstOrAppend(DELIM);
                                innerGroupByBuilder.append(tableAlias + DOT + generateAggColumnName(am));
                            }
                        }
                        counter++;
                        if(null != am.getBuckets() && !am.getBuckets().isEmpty()) {
                            if(counter == pos) {
                                if(null != function) {
                                    innerSqlBuilder.append(function.name() + "(");
                                }
                                innerSqlBuilder.append(tableAlias + DOT + generateBucketPseudoColumnName(am));
                                if(null != function) {
                                    innerSqlBuilder.append(function.name() + ")");
                                } else {
                                    innerGroupByBuilder.markNotFirstOrAppend(DELIM);
                                    innerGroupByBuilder.append(tableAlias + DOT + generateBucketPseudoColumnName(am));
                                }
                            }
                            counter++;
                        }
                    }
                }
            } else {
                //find the right dimension
                innerSqlBuilder.markNotFirstOrAppend(DELIM);
                //look for the dimension
                innerSqlBuilder.append(generateDimensionFieldSql(q.getFields().get(pos), tableAlias));
                innerGroupByBuilder.markNotFirstOrAppend(DELIM);
                innerGroupByBuilder.append(generateDimensionFieldSql(q.getFields().get(pos), tableAlias));
            }
        }
    }
    
    private static class FirstAwareWrapper { 
        StringBuilder sb;
        boolean first = true;
        
        public FirstAwareWrapper(StringBuilder stringBuilder) { 
            if (stringBuilder == null) { throw new IllegalArgumentException("FirstAwareWrapper requires StringBuffer to not be null"); }
            this.sb = stringBuilder;
        }
        
        private void markNotFirstOrAppend(String append) { 
            if (first) { first = false; } 
            else { append(append); }
        }
        
        private void append(Object toAppend) { sb.append(toAppend); }
        
        @Override public String toString() { return sb.toString(); }
    }
}
