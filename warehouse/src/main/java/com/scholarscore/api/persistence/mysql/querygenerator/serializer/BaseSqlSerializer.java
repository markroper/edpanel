package com.scholarscore.api.persistence.mysql.querygenerator.serializer;

import com.scholarscore.api.persistence.DbMappings;
import com.scholarscore.api.persistence.mysql.querygenerator.QuerySqlGenerator;
import com.scholarscore.models.query.Dimension;

/**
 * User: jordan
 * Date: 2/17/16
 * Time: 11:30 AM
 */
public abstract class BaseSqlSerializer implements MeasureSqlSerializer {

    @Override
    // Note: non-standard relationships (not using conventional _id / _fk) will BREAK this method. override if needed
    public String toJoinClause(Dimension dimToJoinUpon) {
        String dimTableName = DbMappings.DIMENSION_TO_TABLE_NAME.get(dimToJoinUpon);
        return LEFT_OUTER_JOIN + toTableName() + ON +
                dimTableName + DOT + QuerySqlGenerator.resolvePrimaryKeyField(dimTableName) + EQUALS +
                toTableName() + DOT + dimTableName + FK_COL_SUFFIX + " "; // + sectionGradeJoin();
        // TODO Jordan refactoring in progress - many sqlizers include another join here. currently they must override toJoinClause and add it.
    }

    // TODO Jordan: this will go away once with new dimension types. Just a hack so I can sanity check this without adding all that.
    protected String toJoinClause(String tableToJoinUpon) {
        String dimTableName = tableToJoinUpon;
        return LEFT_OUTER_JOIN + toTableName() + ON +
                dimTableName + DOT + QuerySqlGenerator.resolvePrimaryKeyField(dimTableName) + EQUALS +
                toTableName() + DOT + dimTableName + FK_COL_SUFFIX + " ";
        // TODO Jordan refactoring in progress - many sqlizers include another join here. currently they must override toJoinClause and add it.
    }
     
    /* 
* Generic method to join to a table
* */
    protected String joinTable(String tableToJoin) {
        String inferredJoinFromColumnName = tableToJoin + FK_COL_SUFFIX;
        String inferredJoinToColumnName = tableToJoin + ID_COL_SUFFIX;
        return joinTable(tableToJoin, inferredJoinFromColumnName, inferredJoinToColumnName);
    }

    /* 
    * Overloaded method to join tables allowing the foreign key columns to be specified.
    * This should generally not be needed if the keys are named following the _id and _fk convention.
    * */
    protected String joinTable(String tableToJoin, String joinedFromTableFkName, String joinedToTableFkName) {
        return LEFT_OUTER_JOIN + tableToJoin + ON +
                toTableName() + DOT + joinedFromTableFkName +
                EQUALS + tableToJoin + DOT + joinedToTableFkName + " ";
    }
}
