package com.scholarscore.api.persistence.mysql.querygenerator.serializer.behavior;

import com.scholarscore.api.persistence.mysql.querygenerator.serializer.behavior.DemeritSqlSerializer;
import com.scholarscore.models.BehaviorCategory;
import com.scholarscore.models.HibernateConsts;

/**
 * @author markroper on 11/28/15.
 */
public class InSchoolSuspensionSqlSerializer extends BehaviorSqlSerializer {
    
    @Override
    BehaviorCategory matchesBehavior() {
        return BehaviorCategory.IN_SCHOOL_SUSPENSION;
    }
    
}
