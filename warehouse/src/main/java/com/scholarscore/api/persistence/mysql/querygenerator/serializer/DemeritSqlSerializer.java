package com.scholarscore.api.persistence.mysql.querygenerator.serializer;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.models.query.AggregateFunction;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.Measure;

public class DemeritSqlSerializer implements MeasureSqlSerializer {

    @Override
    public String toSelectClause(AggregateFunction agg) {
        return agg.name() + 
                "(if(" + DbConst.BEHAVIOR_TABLE + DOT + DbConst.BEHAVIOR_CATEGORY_COL + " = '" + Measure.DEMERIT.name() + 
                "', 1, 0))";
    }

    @Override
    public String toJoinClause(Dimension dimToJoinUpon) {
        String dimTableName = DbConst.DIMENSION_TO_TABLE_NAME.get(dimToJoinUpon);
        return LEFT_OUTER_JOIN + DbConst.BEHAVIOR_TABLE + ON +
                dimTableName + DOT + dimTableName + ID_COL_SUFFIX +
                EQUALS + DbConst.BEHAVIOR_TABLE + DOT + dimTableName + FK_COL_SUFFIX + " ";
    }

    @Override
    public String toTableName() {
        return DbConst.BEHAVIOR_TABLE;
    }

}
