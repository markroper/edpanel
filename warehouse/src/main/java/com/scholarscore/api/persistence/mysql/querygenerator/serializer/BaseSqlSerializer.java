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
    
    // TODO Jordan: it may be more error-proof if all of these methods only accept dimensions

    @Override
    // Note: non-standard relationships (not using conventional _id / _fk) will BREAK this method. override if needed
    public String toJoinClause(Dimension dimToJoinUpon) {
        String dimTableName = DbMappings.DIMENSION_TO_TABLE_NAME.get(dimToJoinUpon);
        return toJoinClause(dimTableName);
    }
    
    protected String toJoinClause(String dimTableName) {
        String optClause = optionalJoinOrEmptyString();
        return buildJoinClause(dimTableName) + optClause; 
    }
    
    private String buildJoinClause(String dimTableName) {
        return LEFT_OUTER_JOIN + toTableName() + ON +
                dimTableName + DOT + QuerySqlGenerator.resolvePrimaryKeyField(dimTableName) + EQUALS +
                toTableName() + DOT + toTableName() + FK_COL_SUFFIX + " ";
    }
    
    @Override
    public String toFromClause() {
        String optClause = optionalJoinOrEmptyString();
        return toTableName() + " " + optClause;
    }

    // if serializers return something other than null, it shall be joined upon
    public String optionalJoinedTable() { return null; } 
    
    /* 
    * Generic method to join to a table
    * */
    private String joinTable(String tableToJoin, boolean newTableStoresFk) {

        String inferredJoinFromColumnName;
        String inferredJoinToColumnName;
        if (newTableStoresFk) {
            inferredJoinFromColumnName = toTableName() + ID_COL_SUFFIX;
            inferredJoinToColumnName = toTableName() + FK_COL_SUFFIX;
        } else {
            inferredJoinFromColumnName = tableToJoin + FK_COL_SUFFIX;
            inferredJoinToColumnName = tableToJoin + ID_COL_SUFFIX;
        }
        
        return joinTable(tableToJoin, toTableName(), inferredJoinToColumnName, inferredJoinFromColumnName);
    }
    
    protected String joinTable(String tableToJoin) { 
        return this.joinTable(tableToJoin, optionalJoinTableStoresFk());
    }
    
    /* 
    * Overloaded method to join tables allowing the join tables and foreign key columns to be specified.
    * This method should be avoided if possible and more straightforward serializers should not need to use it.
    * Some of the more complicated existing serializers require this method (for the time being)
    * */
    protected String joinTable(String tableToJoinTo, String tableToJoinFrom, String joinToColName, String joinFromColName) {
        return LEFT_OUTER_JOIN + tableToJoinTo + ON +
                tableToJoinFrom + DOT + joinFromColName +
                EQUALS + tableToJoinTo + DOT + joinToColName + " ";
    }

    private String optionalJoinOrEmptyString() {
        return (null == optionalJoinedTable() ? "" : joinTable(optionalJoinedTable()));
    }

    // override this and return true if optional join table contains the key (_FK) that points to the other table's ID 
    protected boolean optionalJoinTableStoresFk() { return false; }
    
    // children can override this to return something other than 0 when the (possibly) contained if statement
    // evaluated returns false.
    // (some statements use 1,0 and some use 1,null -- 1,null seems better/more recent
    // but haven't gotten to the bottom of it yet)
    protected String valueForFalse() { return "0"; }

    protected static String tableNameDotPrimaryKey(String tableName) {
        return tableName + DOT + QuerySqlGenerator.resolvePrimaryKeyField(tableName);
    }
}
