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
    // I don't know why every other SQL if statement uses 1,0 and this one uses 1,null -- but sticking with existing behavior.
    String valueForFalse() { return "null"; }
}
