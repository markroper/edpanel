package com.scholarscore.api.persistence.mysql.querygenerator.serializer;

import com.scholarscore.api.persistence.DbMappings;
import com.scholarscore.api.persistence.mysql.querygenerator.QuerySqlGenerator;
import com.scholarscore.models.query.Dimension;

/**
 * User: jordan
 * Date: 2/17/16
 * Time: 11:30 AM
 */
public abstract class BaseSqlSerializer implements MeasureSqlSerializer {

    @Override
    public String toJoinClause(Dimension dimToJoinUpon) {
        String dimTableName = DbMappings.DIMENSION_TO_TABLE_NAME.get(dimToJoinUpon);
        return LEFT_OUTER_JOIN + toTableName() + ON +
                dimTableName + DOT + QuerySqlGenerator.resolvePrimaryKeyField(dimTableName) + EQUALS +
                toTableName() + DOT + dimTableName + FK_COL_SUFFIX + " "; // + sectionGradeJoin();
        // TODO Jordan refactoring in progress...
    }
}
