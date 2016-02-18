package com.scholarscore.api.persistence.mysql.querygenerator.serializer;

import com.scholarscore.models.BehaviorCategory;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.query.Measure;

/**
 * @author markroper on 11/28/15.
 */
public class DetentionSqlSerializer extends DemeritSqlSerializer {
    @Override
    public String toSelectInner() {
        return "if(" + HibernateConsts.BEHAVIOR_TABLE + DOT + HibernateConsts.BEHAVIOR_CATEGORY + " = '" + BehaviorCategory.DETENTION +
        "', 1, 0)";
    }
}
