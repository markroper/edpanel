package com.scholarscore.api.persistence.mysql.querygenerator.serializer.behavior;

import com.scholarscore.api.persistence.mysql.querygenerator.serializer.BaseSqlSerializer;
import com.scholarscore.models.BehaviorCategory;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.query.Dimension;

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
    
    // children must override this to specify which behavior they are interested in 
    abstract BehaviorCategory matchesBehavior();
    
    @Override
    public Dimension toTableDimension() {
        return Dimension.BEHAVIOR;
    }
}
