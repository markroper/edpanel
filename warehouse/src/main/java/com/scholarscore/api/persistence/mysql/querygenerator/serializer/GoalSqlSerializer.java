package com.scholarscore.api.persistence.mysql.querygenerator.serializer;

import com.scholarscore.models.HibernateConsts;

/**
 * Created by markroper on 3/4/16.
 */
public class GoalSqlSerializer extends BaseSqlSerializer {
    @Override
    public String toSelectInner() {
        return "*";
    }

    @Override
    public String toTableName() {
        return HibernateConsts.GOAL_TABLE;
    }
}
