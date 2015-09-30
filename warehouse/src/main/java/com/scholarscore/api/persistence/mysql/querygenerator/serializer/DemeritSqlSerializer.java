package com.scholarscore.api.persistence.mysql.querygenerator.serializer;

import com.scholarscore.api.persistence.DbMappings;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.query.AggregateFunction;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.Measure;

public class DemeritSqlSerializer implements MeasureSqlSerializer {

    @Override
    public String toSelectClause(AggregateFunction agg) {
        return agg.name() + 
                "(if(" + HibernateConsts.BEHAVIOR_TABLE + DOT + HibernateConsts.BEHAVIOR_CATEGORY + " = '" + Measure.DEMERIT.name() + 
                "', 1, 0))";
    }

    @Override
    public String toJoinClause(Dimension dimToJoinUpon) {
        String dimTableName = DbMappings.DIMENSION_TO_TABLE_NAME.get(dimToJoinUpon);
        return LEFT_OUTER_JOIN + HibernateConsts.BEHAVIOR_TABLE + ON +
                dimTableName + DOT + dimTableName + ID_COL_SUFFIX +
                EQUALS + HibernateConsts.BEHAVIOR_TABLE + DOT + dimTableName + FK_COL_SUFFIX + " ";
    }

    @Override
    public String toTableName() {
        return HibernateConsts.BEHAVIOR_TABLE;
    }

}
