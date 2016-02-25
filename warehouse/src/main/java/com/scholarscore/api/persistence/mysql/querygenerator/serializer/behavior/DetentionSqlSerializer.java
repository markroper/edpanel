package com.scholarscore.api.persistence.mysql.querygenerator.serializer.behavior;

import com.scholarscore.models.BehaviorCategory;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.query.Measure;

/**
 * @author markroper on 11/28/15.
 */
public class DetentionSqlSerializer extends BehaviorSqlSerializer {

    @Override
    BehaviorCategory matchesBehavior() {
        return BehaviorCategory.DETENTION;
    }

}
