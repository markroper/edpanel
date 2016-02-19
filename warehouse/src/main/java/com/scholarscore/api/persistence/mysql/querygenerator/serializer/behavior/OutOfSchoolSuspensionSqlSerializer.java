package com.scholarscore.api.persistence.mysql.querygenerator.serializer.behavior;

import com.scholarscore.models.BehaviorCategory;
import com.scholarscore.models.HibernateConsts;

/**
 * @author markroper on 11/28/15.
 */
public class OutOfSchoolSuspensionSqlSerializer extends BehaviorSqlSerializer {

    @Override
    BehaviorCategory matchesBehavior() {
        return BehaviorCategory.OUT_OF_SCHOOL_SUSPENSION;
    }

}
