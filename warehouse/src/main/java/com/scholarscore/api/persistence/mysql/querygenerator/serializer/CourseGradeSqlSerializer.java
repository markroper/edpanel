package com.scholarscore.api.persistence.mysql.querygenerator.serializer;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.models.query.AggregateFunction;
import com.scholarscore.models.query.Dimension;

public class CourseGradeSqlSerializer implements MeasureSqlSerializer {
    @Override
    public String toSelectClause(AggregateFunction agg) {
        return agg.name() + "(" + DbConst.STUDENT_SECTION_GRADE_TABLE + 
                "." + DbConst.STUD_SECTION_GRADE_GRADE + ")";
    }

    @Override
    public String toJoinClause(Dimension dimToJoinUpon) {
        String dimTableName = DbConst.DIMENSION_TO_TABLE_NAME.get(dimToJoinUpon);
        return LEFT_OUTER_JOIN + DbConst.STUDENT_SECTION_GRADE_TABLE + ON +
                dimTableName + DOT + dimTableName + ID_COL_SUFFIX + EQUALS +
                DbConst.STUDENT_SECTION_GRADE_TABLE + DOT + dimTableName + FK_COL_SUFFIX;
    }

}
