package com.scholarscore.api.persistence.mysql.querygenerator.serializer;

import com.scholarscore.models.query.AggregateFunction;
import com.scholarscore.models.query.Dimension;

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
}
