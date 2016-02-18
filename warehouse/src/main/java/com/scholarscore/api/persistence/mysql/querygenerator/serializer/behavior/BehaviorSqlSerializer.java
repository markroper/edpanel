package com.scholarscore.api.persistence.mysql.querygenerator.serializer.behavior;

import com.scholarscore.api.persistence.mysql.querygenerator.serializer.BaseSqlSerializer;
import com.scholarscore.models.BehaviorCategory;
import com.scholarscore.models.HibernateConsts;

/**
 * User: jordan
 * Date: 2/17/16
 * Time: 4:14 PM
 */
public abstract class BehaviorSqlSerializer extends BaseSqlSerializer {

    @Override
    public String toSelectInner() {
        return "if(" + toTableName() + DOT + HibernateConsts.BEHAVIOR_CATEGORY + " = '" + matchesBehavior() +
                "', 1, " + valueForFalse() +")";
    }
    
    String valueForFalse() { return "0"; }
    
    abstract BehaviorCategory matchesBehavior();
}
