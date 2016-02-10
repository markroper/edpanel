package com.scholarscore.api.persistence.mysql.querygenerator.serializer;

import com.scholarscore.api.persistence.DbMappings;
import com.scholarscore.api.persistence.mysql.querygenerator.QuerySqlGenerator;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.Measure;

public class DemeritSqlSerializer implements MeasureSqlSerializer {

    @Override
    public String toSelectInner() {
        return "if(" + HibernateConsts.BEHAVIOR_TABLE + DOT + HibernateConsts.BEHAVIOR_CATEGORY + " = '" + Measure.DEMERIT.name() +
        "', 1, 0)";
    }

    @Override
    public String toJoinClause(Dimension dimToJoinUpon) {
        String dimTableName = DbMappings.DIMENSION_TO_TABLE_NAME.get(dimToJoinUpon);
        String fkFieldString = dimTableName + FK_COL_SUFFIX;
        if(dimTableName.equals(HibernateConsts.STAFF_TABLE)) {
            fkFieldString = HibernateConsts.USER_FK;
        }
        return LEFT_OUTER_JOIN + HibernateConsts.BEHAVIOR_TABLE + ON +
                dimTableName + DOT + QuerySqlGenerator.resolvePrimaryKeyField(dimTableName) +
                EQUALS + HibernateConsts.BEHAVIOR_TABLE + DOT + fkFieldString + " ";
    }

    @Override
    public String toFromClause() {
        return HibernateConsts.BEHAVIOR_TABLE;
    }

    @Override
    public String toTableName() {
        return HibernateConsts.BEHAVIOR_TABLE;
    }

}
