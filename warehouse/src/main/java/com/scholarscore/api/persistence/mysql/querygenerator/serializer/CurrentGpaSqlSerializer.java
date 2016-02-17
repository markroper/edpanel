package com.scholarscore.api.persistence.mysql.querygenerator.serializer;

import com.scholarscore.api.persistence.DbMappings;
import com.scholarscore.api.persistence.mysql.querygenerator.QuerySqlGenerator;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.query.Dimension;

/**
 * Created by markroper on 2/10/16.
 */
public class CurrentGpaSqlSerializer implements MeasureSqlSerializer {
    @Override
    public String toSelectInner() {
        return HibernateConsts.GPA_TABLE +
                "." + HibernateConsts.GPA_SCORE;
    }

    @Override
    public String toJoinClause(Dimension dimToJoinUpon) {
        String dimTableName = DbMappings.DIMENSION_TO_TABLE_NAME.get(dimToJoinUpon);
        return LEFT_OUTER_JOIN + HibernateConsts.CURRENT_GPA_TABLE + ON +
                dimTableName + DOT + QuerySqlGenerator.resolvePrimaryKeyField(dimTableName) + EQUALS +
                HibernateConsts.CURRENT_GPA_TABLE + DOT + dimTableName + FK_COL_SUFFIX + " " +
                gpaCurrGpaJoin();
    }

    private static String gpaCurrGpaJoin() {
        return LEFT_OUTER_JOIN + HibernateConsts.GPA_TABLE + ON +
                HibernateConsts.GPA_TABLE + DOT + HibernateConsts.GPA_ID + EQUALS +
                HibernateConsts.CURRENT_GPA_TABLE + DOT + HibernateConsts.GPA_FK;
    }
    @Override
    public String toFromClause() {
        return HibernateConsts.CURRENT_GPA_TABLE + " " + gpaCurrGpaJoin();
    }

    @Override
    public String toTableName() {
        return HibernateConsts.CURRENT_GPA_TABLE;
    }
}