package com.scholarscore.api.persistence.mysql.querygenerator.serializer.behavior;

import com.scholarscore.api.persistence.mysql.querygenerator.serializer.MeasureSqlSerializer;
import com.scholarscore.models.BehaviorCategory;
import com.scholarscore.models.HibernateConsts;

public class DemeritSqlSerializer extends BehaviorSqlSerializer implements MeasureSqlSerializer {

    @Override
    BehaviorCategory matchesBehavior() {
        return BehaviorCategory.DEMERIT;
    }
    
}
