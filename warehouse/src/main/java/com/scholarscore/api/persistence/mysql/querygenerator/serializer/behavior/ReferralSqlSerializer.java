package com.scholarscore.api.persistence.mysql.querygenerator.serializer.behavior;

import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.query.Measure;

/**
 * Created by markroper on 2/11/16.
 */
public class ReferralSqlSerializer extends DemeritSqlSerializer {
    @Override
    public String toSelectInner() {
        return "if(" + HibernateConsts.BEHAVIOR_TABLE + DOT + HibernateConsts.BEHAVIOR_CATEGORY + " = '" + Measure.REFERRAL.name() +
                "', 1, null)";
    }
}
