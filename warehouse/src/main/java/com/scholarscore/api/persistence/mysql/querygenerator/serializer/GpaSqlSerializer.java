package com.scholarscore.api.persistence.mysql.querygenerator.serializer;

import com.scholarscore.api.persistence.DbMappings;
import com.scholarscore.api.persistence.mysql.querygenerator.QuerySqlGenerator;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.query.AggregateFunction;
import com.scholarscore.models.query.Dimension;

/**
 * Created by markroper on 12/1/15.
 */
public class GpaSqlSerializer implements MeasureSqlSerializer {
    @Override
    public String toSelectClause(AggregateFunction agg) {
        return agg.name() + "(" + HibernateConsts.GPA_TABLE +
                "." + HibernateConsts.GPA_SCORE + ")";
    }

    @Override
    public String toJoinClause(Dimension dimToJoinUpon) {
        String dimTableName = DbMappings.DIMENSION_TO_TABLE_NAME.get(dimToJoinUpon);
        return LEFT_OUTER_JOIN + HibernateConsts.GPA_TABLE + ON +
                dimTableName + DOT + QuerySqlGenerator.resolvePrimaryKeyField(dimTableName) + EQUALS +
                HibernateConsts.GPA_TABLE + DOT + dimTableName + FK_COL_SUFFIX + " ";
    }

    @Override
    public String toTableName() {
        return HibernateConsts.GPA_TABLE;
    }
}
