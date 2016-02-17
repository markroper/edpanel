package com.scholarscore.api.persistence.mysql.querygenerator.serializer.academic;

import com.scholarscore.api.persistence.DbMappings;
import com.scholarscore.api.persistence.mysql.querygenerator.QuerySqlGenerator;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.BaseSqlSerializer;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.MeasureSqlSerializer;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.query.Dimension;

/**
 * Created by markroper on 2/10/16.
 */
public class CurrentGpaSqlSerializer extends BaseSqlSerializer implements MeasureSqlSerializer {
    @Override
    public String toSelectInner() {
        return HibernateConsts.GPA_TABLE +
                "." + HibernateConsts.GPA_SCORE;
    }

    @Override
    public String toJoinClause(Dimension dimToJoinUpon) {
        return super.toJoinClause(dimToJoinUpon) + gpaCurrGpaJoin();
    }

    private String gpaCurrGpaJoin() {
        return joinTable(HibernateConsts.GPA_TABLE);
    }
    @Override
    public String toFromClause() {
        return toTableName() + " " + gpaCurrGpaJoin();
    }

    @Override
    public String toTableName() {
        return HibernateConsts.CURRENT_GPA_TABLE;
    }
}
