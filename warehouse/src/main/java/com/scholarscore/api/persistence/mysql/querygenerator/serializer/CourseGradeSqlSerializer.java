package com.scholarscore.api.persistence.mysql.querygenerator.serializer;

import com.scholarscore.api.persistence.mysql.DbMappings;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.query.AggregateFunction;
import com.scholarscore.models.query.Dimension;

public class CourseGradeSqlSerializer implements MeasureSqlSerializer {
    @Override
    public String toSelectClause(AggregateFunction agg) {
        return agg.name() + "(" + HibernateConsts.STUDENT_SECTION_GRADE_TABLE + 
                "." + HibernateConsts.STUDENT_SECTION_GRADE_GRADE + ")";
    }

    @Override
    public String toJoinClause(Dimension dimToJoinUpon) {
        String dimTableName = DbMappings.DIMENSION_TO_TABLE_NAME.get(dimToJoinUpon);
        return LEFT_OUTER_JOIN + HibernateConsts.STUDENT_SECTION_GRADE_TABLE + ON +
                dimTableName + DOT + dimTableName + ID_COL_SUFFIX + EQUALS +
                HibernateConsts.STUDENT_SECTION_GRADE_TABLE + DOT + dimTableName + FK_COL_SUFFIX + " ";
    }

    @Override
    public String toTableName() {
        return HibernateConsts.STUDENT_SECTION_GRADE_TABLE;
    }

}
