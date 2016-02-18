package com.scholarscore.api.persistence.mysql.querygenerator.serializer.academic;

import com.scholarscore.api.persistence.DbMappings;
import com.scholarscore.api.persistence.mysql.querygenerator.QuerySqlGenerator;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.BaseSqlSerializer;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.MeasureSqlSerializer;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.query.Dimension;

/**
 * Created by markroper on 12/1/15.
 */
public class GpaSqlSerializer extends BaseSqlSerializer implements MeasureSqlSerializer {
    @Override
    public String toSelectInner() {
        return HibernateConsts.GPA_TABLE +
                "." + HibernateConsts.GPA_SCORE;
    }

    @Override
    public String toFromClause() {
        return toTableName() + " ";
    }

    @Override
    public String toTableName() {
        return HibernateConsts.GPA_TABLE;
    }
}