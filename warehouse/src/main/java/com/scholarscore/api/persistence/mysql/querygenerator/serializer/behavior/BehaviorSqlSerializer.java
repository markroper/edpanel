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
    
    // children can override this to return something other than 0 when the if statement evaluated above returns false
    String valueForFalse() { return "0"; }
    
    // children must override this to specify which behavior they are interested in 
    abstract BehaviorCategory matchesBehavior();

    @Override
    public String toTableName() {
        return HibernateConsts.BEHAVIOR_TABLE;
    }

    @Override
    protected String getTableNameFk(String tableName) {
        if (tableName != null && tableName.equals(HibernateConsts.STAFF_TABLE)) {
            return HibernateConsts.USER_FK;
        }
        return super.getTableNameFk(tableName);
    }
}
