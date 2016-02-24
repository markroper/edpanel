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
import com.scholarscore.models.query.dimension.IDimension;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
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

        // TODO Jordan: this is a work in progress and 
        // doesn't touch anything else... move to
        List<Dimension> copyOfOrderedTables = new ArrayList<>();
        copyOfOrderedTables.addAll(orderedTables);

        // I was hoping to get the other table (from the measure) here as well...
        // well, let's try hacky to start and maybe later work our way up to elegant
//        String measureTableName = null;
//        Dimension measureDimension = null;
        if (q.getAggregateMeasures() != null && q.getAggregateMeasures().size() > 0) {
            AggregateMeasure aggregateMeasure = q.getAggregateMeasures().get(0);
            MeasureSqlSerializer serializer = MeasureSqlSerializerFactory.get(aggregateMeasure.getMeasure());
            Dimension table = DbMappings.TABLE_NAME_TO_DIMENSION.get(serializer.toTableName());
            Dimension optionalTable = DbMappings.TABLE_NAME_TO_DIMENSION.get(serializer.optionalJoinedTable());
            
            copyOfOrderedTables.add(table);
            if (optionalTable != null) {
                copyOfOrderedTables.add(optionalTable);
            }
            
            /*
            for (Dimension dimensionKey : DbMappings.DIMENSION_TO_TABLE_NAME.keySet()) {
                String thisTableName = DbMappings.DIMENSION_TO_TABLE_NAME.get(dimensionKey);
                if (thisTableName.equals(measureTableName)) {
                    // this is the dimension! 
                    // measureDimension = dimensionKey;
                    copyOfOrderedTables.add(dimensionKey);
                    break;
                }
            }
            */
        }

//        if (measureDimension != null) {
//            copyOfOrderedTables.add(measureDimension);
//        }
        
        // errg. just make it fit.
        boolean hasCompletePath = hasCompleteJoinPath(copyOfOrderedTables);
        if (hasCompletePath) {
            System.out.println("Query has COMPLETE join path!");
        } else {
            System.out.println("Query has -incomplete- join path!");
        }
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
                if (mss != null) {
                    sqlBuilder.append(mss.toJoinClause(currTable));
                }
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
                        if(am != null 
                                && mss != null
                                && am.getMeasure() != null 
                                && Measure.buildMeasure(am.getMeasure()).getCompatibleDimensions().contains(joinDim)){
                            currentTableName = mss.toTableName();
                        } else {
                            Dimension dimDesc = currTable;
                            //Start with the previous dimension since we're already dealing with i and i-1...
                            int descIndex = i - 2;
                            while(descIndex >= 0 && !Dimension.buildDimension(dimDesc).getParentDimensions().contains(joinDim)) {
                                dimDesc = orderedTables.get(descIndex);
                                descIndex--;
                            }
                            if(Dimension.buildDimension(dimDesc).getParentDimensions().contains(joinDim)) {
                                currentTableName = DbMappings.DIMENSION_TO_TABLE_NAME.get(dimDesc);
                            } else {
                                throw new SqlGenerationException(
                                        "Cannot join dimension to either previous dimension or measure: " + joinDim);
                            }
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
    
    private static class Node { 
        Edge[] edges;
        Dimension dimension;
        
        @Override
        public String toString() { 
            return "Node (d:" + dimension +") (edges: [" + edges.length + "])";
        }
    }
    
    private static class Edge {
        Node pointedFrom;
        Node pointedAt;
    }
    
    // should probably take all dimensions and build the full graph once?
    private static HashMap<Dimension, Node> buildGraph(List<Dimension> dimensions) { 
        HashMap<Dimension, Node> nodesSoFar = new HashMap<>();
        // first just build the nodes
        for (Dimension dimension : dimensions) {
            Node node = new Node();
            node.dimension = dimension;
            nodesSoFar.put(dimension, node);
        }
        // then wire up the edges
        for (Dimension dimension : dimensions) {
            Node node = nodesSoFar.get(dimension);
            IDimension dimensionClass = Dimension.buildDimension(dimension);
            Set<Dimension> parentDimensions = dimensionClass.getParentDimensions();
            if (parentDimensions != null && parentDimensions.size() > 0) {
                Edge[] edges = new Edge[parentDimensions.size()];
                int arrayPos = 0;
                for (Dimension parentDimension : parentDimensions) {
                    Edge edge = new Edge();
                    edge.pointedAt = nodesSoFar.get(parentDimension);
                    edge.pointedFrom = nodesSoFar.get(dimension);
                    edges[arrayPos++] = edge;
                }
                node.edges = edges;
            } else {
                node.edges = new Edge[0];
            }
        }
        return nodesSoFar;
    }
    
    private static final HashMap<Dimension, Node> allDimensionsGraph = buildGraph(Arrays.asList(Dimension.values()));
    
    // right now this only checks the neighbors a node points to and all nodes pointing at a node
    private static Set<Node> findImmediateNeighbors(Node dimensionNode) {
        if (dimensionNode == null) { return new HashSet<>(); }
        Set<Node> allNodes = new HashSet<>();
        // all nodes this node points at
        for (Edge edge : dimensionNode.edges) {
            allNodes.add(edge.pointedAt);
        }
        // all nodes that are pointing at this node
        allNodes.addAll(getAllNodesPointingAt(dimensionNode));
        return allNodes;
    }
    
    // doesn't actually tell us a path, but rather if a given list of dimensions can be joined together w/o additional tables
    private static boolean hasCompleteJoinPath(List<Dimension> orderedTables) {
        if (orderedTables.size() <= 1) { return true; }
        
        Set<Node> unmatchedTables = new HashSet<>();
        for (Dimension dimension : orderedTables) {
            unmatchedTables.add(allDimensionsGraph.get(dimension));
        }
        
        // take the first table and scan its neighbors, which will then lead to all connected tables being scanned.
        HashMap<Node, Integer> tableGraph = new HashMap<>();
        Node firstNode = unmatchedTables.iterator().next();
        Set<Node> neighborNodes = findImmediateNeighbors(firstNode);
        unmatchedTables.remove(firstNode);

        // yeah, okay, a table hasn't been matched yet. more readable would be a separate 'isFirst' var maybe
        // we may need to loop through this a number of times depending on the order of the tables.
        // as long as any new table is matched, loop again.
        boolean tableMatchedThisRound = true; 
        
        while (tableMatchedThisRound) {
            tableMatchedThisRound = false;
            // check all unmatched tables against these neighbor nodes. 
            for (Iterator<Node> unmatchedTableIterator = unmatchedTables.iterator(); unmatchedTableIterator.hasNext(); ) {
                Node unmatchedTable = unmatchedTableIterator.next();
//            for (Node unmatchedTable : unmatchedTables) {
                if (neighborNodes.contains(unmatchedTable)) {
                    Set<Node> moreNeighborNodes = findImmediateNeighbors(unmatchedTable);
                    unmatchedTableIterator.remove();
                    neighborNodes.addAll(moreNeighborNodes);
                    tableMatchedThisRound = true;
                }
//            }
            }
        }
        
        // okay, now all the tables are matched or they *will never* be matched.
        // if there's any unmatched tables, the join path is incomplete.
        if (unmatchedTables.size() > 0) {
            System.out.println("Unmatched Table! Could not match on table(s)...");
            System.out.print("ALL TABLES: ( ");
            for (Dimension table : orderedTables) {
                if (table != null) {
                    System.out.print(table.name() + " ");
                }
            }
            System.out.println(")");
            for (Node unmatchedTable : unmatchedTables) {
                System.out.println("NO MATCH FOR TABLE " + unmatchedTable);
            }
            return false;
        }  
        return true;
        
        /*
        for (int i = 1; i < orderedTables.size(); i++) {
            Dimension currentTable = orderedTables.get(i);
            Node tableNode = allDimensionsGraph.get(currentTable);
            
            // is node already scanned? if so, we don't need to do anything else
            if (tableGraph.keySet().contains(tableNode)) { continue; }
            
            // okay, node isn't scanned. This could be because our graph is directional, 
            // so maybe *this* table points to one of the ones we've already scanned. 
            // Additionally, it's possible that this table points to ANOTHER table that's not scanned yet, but 
            // is found LATER in the orderedTables list (and thus, will be scanned) 
            if (tableNode.edges != null && tableNode.edges.length > 0) {
                for (Edge edge : tableNode.edges) {
                    // if the node has edges, check 'em. Maybe this table points to either
                    // (a) a table that we've already scanned 
                    if (tableGraph.keySet().contains(edge.pointedAt.dimension)) {
                        // ayup, already scanned a table this one is pointing at. might as well scan this one 
                    }
                    // or 
                    // (b) a table that we will be scanning soon
                }
            }
        }
        */


        /*
        // ...
        Set<Node> nodesPointingAt = getAllNodesPointingAt(firstDimensionNode);
        for (Node nodePointingAt : nodesPointingAt) {
            // if we have unscanned nodes that are directly pointing to our initial node,
        }
        

        // now, check to see if all tables are in the set (i.e. connected) 
        // any dimension not in the set indicates a query without a complete join path
        for (Dimension table : orderedTables) {
            Node tableNode = allDimensionsGraph.get(table);
            if (!tableGraph.containsKey(tableNode)) {
                return false;
            }
        }
        return true;
        */
    }
   
    private static Set<Node> getAllNodesPointingAt(Node root) { 
        HashSet<Node> allNodesPointingAtRoot = new HashSet<>();
        for (Dimension dimension : allDimensionsGraph.keySet()) {
            Node currentNode = allDimensionsGraph.get(dimension);
            if (currentNode.edges != null) {
                for (Edge edge : currentNode.edges) {
                    if (edge.pointedAt != null && edge.pointedAt.equals(root)) {
                        allNodesPointingAtRoot.add(currentNode);
                    }
                }
            }
        }
        return allNodesPointingAtRoot;
    }
    
    private static List<Dimension> breadthFirstSearch(Dimension rootDimension, Dimension targetDimension) { 
        PriorityQueue<Dimension> dimensionsToSearch = new PriorityQueue<>();
        
        return null;
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
