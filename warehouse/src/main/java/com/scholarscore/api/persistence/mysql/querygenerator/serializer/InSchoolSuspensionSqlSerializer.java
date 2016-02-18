package com.scholarscore.api.persistence.mysql.querygenerator.serializer;

import com.scholarscore.models.BehaviorCategory;
import com.scholarscore.models.HibernateConsts;

/**
 * @author markroper on 11/28/15.
 */
public class InSchoolSuspensionSqlSerializer extends DemeritSqlSerializer {
    @Override
    public String toSelectInner() {
        return "if(" + HibernateConsts.BEHAVIOR_TABLE + DOT + HibernateConsts.BEHAVIOR_CATEGORY + " = '" + BehaviorCategory.IN_SCHOOL_SUSPENSION +
        "', 1, 0)";
    }
}