package com.scholarscore.api.persistence.mysql.querygenerator.serializer;

import com.scholarscore.models.query.Dimension;

/**
 * Created by markroper on 3/4/16.
 */
public class GoalSqlSerializer extends BaseSqlSerializer {
    @Override
    public String toSelectInner() {
        return "*";
    }

    @Override
    public Dimension toTableDimension() {
        return Dimension.GOAL;
    }
}
