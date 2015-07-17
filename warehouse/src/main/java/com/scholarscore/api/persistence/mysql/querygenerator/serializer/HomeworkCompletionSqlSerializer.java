package com.scholarscore.api.persistence.mysql.querygenerator.serializer;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.models.query.AggregateFunction;
import com.scholarscore.models.query.Dimension;

public class HomeworkCompletionSqlSerializer implements MeasureSqlSerializer {

    @Override
    public String toSelectClause(AggregateFunction agg) {
        return agg.name() + 
                "( if(" + 
                DbConst.STUDENT_ASSIGNMENT_TABLE + DOT + DbConst.STUD_COMPLETED_COL + 
                " is true, 1, 0))";
    }

    @Override
    public String toJoinClause(Dimension dimToJoinUpon) {
        String dimTableName = DbConst.DIMENSION_TO_TABLE_NAME.get(dimToJoinUpon);
        return LEFT_OUTER_JOIN + DbConst.STUDENT_ASSIGNMENT_TABLE + ON +
                dimTableName + DOT + dimTableName + ID_COL_SUFFIX + 
                EQUALS + DbConst.STUDENT_ASSIGNMENT_TABLE + DOT + dimTableName + FK_COL_SUFFIX +
                " ";
    }

    @Override
    public String toTableName() {
        return DbConst.STUDENT_ASSIGNMENT_TABLE;
    }

}
