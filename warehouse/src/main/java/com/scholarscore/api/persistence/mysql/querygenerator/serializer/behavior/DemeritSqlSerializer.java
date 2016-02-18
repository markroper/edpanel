package com.scholarscore.api.persistence.mysql.querygenerator.serializer.behavior;

import com.scholarscore.api.persistence.mysql.querygenerator.serializer.MeasureSqlSerializer;
import com.scholarscore.models.BehaviorCategory;
import com.scholarscore.models.HibernateConsts;

public class DemeritSqlSerializer extends BehaviorSqlSerializer implements MeasureSqlSerializer {

    @Override
    public String toTableName() {
        return HibernateConsts.BEHAVIOR_TABLE;
    }

    @Override
    BehaviorCategory matchesBehavior() {
        return BehaviorCategory.DEMERIT;
    }
    
    @Override
    protected String getTableNameFk(String tableName) {
        if (tableName != null && tableName.equals(HibernateConsts.STAFF_TABLE)) {
            return HibernateConsts.USER_FK;
        }
        return super.getTableNameFk(tableName);
    }
    
}
