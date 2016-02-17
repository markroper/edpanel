package com.scholarscore.api.persistence.mysql.querygenerator.serializer.behavior;

import com.scholarscore.api.persistence.mysql.querygenerator.serializer.BaseSqlSerializer;
import com.scholarscore.models.BehaviorCategory;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.query.Measure;

/**
 * User: jordan
 * Date: 2/17/16
 * Time: 4:14 PM
 */
public abstract class BehaviorSqlSerializer extends BaseSqlSerializer {

    @Override
    public String toSelectInner() {
        return "if(" + HibernateConsts.BEHAVIOR_TABLE + DOT + HibernateConsts.BEHAVIOR_CATEGORY + " = GARBAGE'" + matchesBehavior() +
                "', 1, 0)";
    }
    
//    abstract BehaviorCategory matchesBehavior();
    BehaviorCategory matchesBehavior() { return BehaviorCategory.DEMERIT; }
}
