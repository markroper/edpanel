package com.scholarscore.api.persistence.mysql.querygenerator.serializer;

import com.scholarscore.api.persistence.DbMappings;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.query.AggregateFunction;
import com.scholarscore.models.query.Dimension;

public class AssignmentGradeSqlSerializer implements MeasureSqlSerializer {

    @Override
    public String toSelectClause(AggregateFunction agg) {
        return agg.name() + 
                "(" + 
                HibernateConsts.STUDENT_ASSIGNMENT_TABLE + DOT + HibernateConsts.STUDENT_ASSIGNMENT_AWARDED_POINTS + 
                " / " + 
                HibernateConsts.ASSIGNMENT_TABLE + DOT + HibernateConsts.ASSIGNMENT_AVAILABLE_POINTS +
                ")";
    }

    @Override
    public String toJoinClause(Dimension dimToJoinUpon) {
        String dimTableName = DbMappings.DIMENSION_TO_TABLE_NAME.get(dimToJoinUpon);
        return LEFT_OUTER_JOIN + HibernateConsts.STUDENT_ASSIGNMENT_TABLE + ON +
                dimTableName + DOT + dimTableName + ID_COL_SUFFIX + 
                EQUALS + HibernateConsts.STUDENT_ASSIGNMENT_TABLE + DOT + dimTableName + FK_COL_SUFFIX +
                " " +
                LEFT_OUTER_JOIN + HibernateConsts.ASSIGNMENT_TABLE + ON +
                HibernateConsts.STUDENT_ASSIGNMENT_TABLE + DOT + HibernateConsts.ASSIGNMENT_TABLE + FK_COL_SUFFIX +
                EQUALS + HibernateConsts.ASSIGNMENT_TABLE + DOT + HibernateConsts.ASSIGNMENT_TABLE + ID_COL_SUFFIX + " ";
    }

    @Override
    public String toTableName() {
        return HibernateConsts.STUDENT_ASSIGNMENT_TABLE;
    }

}
