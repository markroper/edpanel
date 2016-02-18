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
                toTableName() + DOT + getTableNameFk(dimTableName) + " "; // + sectionGradeJoin();
        // TODO Jordan refactoring in progress - many sqlizers include another join here. currently they must override toJoinClause and add it.
    }

    @Override
    public String toFromClause() {
        return toTableName();
    }

    // TODO Jordan: this method is only required today because the Behavior table has a FK to staff (should be staff_fk)
    // but instead this field is named user_fk. Change it and this can be removed!
    protected String getTableNameFk(String tableName) {
        return tableName + FK_COL_SUFFIX;
    }

    // TODO Jordan: this will go away once with new dimension types. Just a hack so I can sanity check this without adding all that.
    // I think it's just school_day right now that needs this
    protected String toJoinClause(String dimTableName) {
        return LEFT_OUTER_JOIN + toTableName() + ON +
                dimTableName + DOT + QuerySqlGenerator.resolvePrimaryKeyField(dimTableName) + EQUALS +
                toTableName() + DOT + getTableNameFk(dimTableName) + " ";
    }
     
    /* 
    * Generic method to join to a table
    * */
    protected String joinTable(String tableToJoin) {
        String inferredJoinFromColumnName = tableToJoin + FK_COL_SUFFIX;
        String inferredJoinToColumnName = tableToJoin + ID_COL_SUFFIX;
        return joinTable(tableToJoin, toTableName(), inferredJoinToColumnName, inferredJoinFromColumnName);
    }
    
//    protected String joinTable()

    /* 
    * Overloaded method to join tables allowing the join tables and foreign key columns to be specified.
    * This method should be avoided if possible and more straightforward serializers should not need to use it.
    * Some of the more complicated existing serializers require this method (for the time being)
    * */
    protected String joinTable(String tableToJoinTo, String tableToJoinFrom, String joinedToTableFkName, String joinedFromTableFkName) {
        return LEFT_OUTER_JOIN + tableToJoinTo + ON +
                tableToJoinFrom + DOT + joinedFromTableFkName +
                EQUALS + tableToJoinTo + DOT + joinedToTableFkName + " ";
    }
}
