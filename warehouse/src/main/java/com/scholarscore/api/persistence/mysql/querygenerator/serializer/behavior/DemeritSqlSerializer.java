package com.scholarscore.api.persistence.mysql.querygenerator.serializer.behavior;

import com.scholarscore.api.persistence.DbMappings;
import com.scholarscore.api.persistence.mysql.querygenerator.QuerySqlGenerator;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.BaseSqlSerializer;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.MeasureSqlSerializer;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.Measure;

public class DemeritSqlSerializer extends BaseSqlSerializer implements MeasureSqlSerializer {

    @Override
    public String toSelectInner() {
        return "if(" + HibernateConsts.BEHAVIOR_TABLE + DOT + HibernateConsts.BEHAVIOR_CATEGORY + " = '" + Measure.DEMERIT.name() +
        "', 1, 0)";
    }

    @Override
    public String toJoinClause(Dimension dimToJoinUpon) {
        return super.toJoinClause(dimToJoinUpon) + " <BARG> ";
    }

    @Override
    // TODO Jordan: handle this break in DB convention - rename userId to staffId?
    // this is preventing the use of the common method in BaseSqlSerializer
    protected String getTableNameFk(String tableName) {
        if (tableName != null && tableName.equals(HibernateConsts.STAFF_TABLE)) {
            return HibernateConsts.USER_FK;
        }
        return super.getTableNameFk(tableName);
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
