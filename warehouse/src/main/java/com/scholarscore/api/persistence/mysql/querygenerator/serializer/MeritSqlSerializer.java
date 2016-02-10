package com.scholarscore.api.persistence.mysql.querygenerator.serializer;

import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.query.Measure;

/**
 * @author markroper on 11/28/15.
 */
public class MeritSqlSerializer extends DemeritSqlSerializer {
    @Override
    public String toSelectInner() {
        return "if(" + HibernateConsts.BEHAVIOR_TABLE + DOT + HibernateConsts.BEHAVIOR_CATEGORY + " = '" + Measure.MERIT.name() +
        "', 1, 0)";
    }
}
