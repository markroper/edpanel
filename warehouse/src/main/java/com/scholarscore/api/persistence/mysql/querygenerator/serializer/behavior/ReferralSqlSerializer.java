package com.scholarscore.api.persistence.mysql.querygenerator.serializer.behavior;

import com.scholarscore.models.BehaviorCategory;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.query.Measure;

/**
 * Created by markroper on 2/11/16.
 */
public class ReferralSqlSerializer extends DemeritSqlSerializer {
    @Override
    BehaviorCategory matchesBehavior() {
        return BehaviorCategory.REFERRAL;
    }

    @Override
    protected String valueForFalse() { return "null"; }
}
