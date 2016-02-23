package com.scholarscore.api.persistence.mysql.querygenerator.serializer.academic;

import com.scholarscore.api.persistence.DbMappings;
import com.scholarscore.api.persistence.mysql.querygenerator.QuerySqlGenerator;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.BaseSqlSerializer;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.MeasureSqlSerializer;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.Query;

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
        String dimTableName = DbMappings.DIMENSION_TO_TABLE_NAME.get(dimToJoinUpon);
        return LEFT_OUTER_JOIN + optionalJoinedTable() + ON +
                tableNameDotPrimaryKey(dimTableName) + EQUALS +
                optionalJoinedTable() + DOT + dimTableName + FK_COL_SUFFIX + " " + gpaCurrGpaJoin();
    }

    private String gpaCurrGpaJoin() {
        return INNER_JOIN + HibernateConsts.CURRENT_GPA_TABLE + ON +
                tableNameDotPrimaryKey(optionalJoinedTable()) + EQUALS +
                toTableName() + DOT + HibernateConsts.GPA_FK;
    }
    
    @Override
    public String toFromClause() {
        return HibernateConsts.CURRENT_GPA_TABLE + " " + gpaCurrGpaJoin() + " ";
    }

    @Override
    public String toTableName() {
        return HibernateConsts.CURRENT_GPA_TABLE;
    }
    
    @Override
    public String optionalJoinedTable() {
        return HibernateConsts.GPA_TABLE;
    }
}
