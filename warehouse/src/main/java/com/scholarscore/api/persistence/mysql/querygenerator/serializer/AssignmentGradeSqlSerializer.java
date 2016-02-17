package com.scholarscore.api.persistence.mysql.querygenerator.serializer;

import com.scholarscore.api.persistence.DbMappings;
import com.scholarscore.api.persistence.mysql.querygenerator.QuerySqlGenerator;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.Measure;

import java.util.Set;

public class AssignmentGradeSqlSerializer implements MeasureSqlSerializer {
    public String toSelectInner() {
        return
            HibernateConsts.STUDENT_ASSIGNMENT_TABLE + DOT + HibernateConsts.STUDENT_ASSIGNMENT_AWARDED_POINTS +
            " / " +
            HibernateConsts.ASSIGNMENT_TABLE + DOT + HibernateConsts.ASSIGNMENT_AVAILABLE_POINTS;
    }

    @Override
    public String toJoinClause(Dimension dimToJoinUpon) {
        String dimTableName = DbMappings.DIMENSION_TO_TABLE_NAME.get(dimToJoinUpon);
        return LEFT_OUTER_JOIN + HibernateConsts.STUDENT_ASSIGNMENT_TABLE + ON +
                dimTableName + DOT + QuerySqlGenerator.resolvePrimaryKeyField(dimTableName) + 
                EQUALS + HibernateConsts.STUDENT_ASSIGNMENT_TABLE + DOT + dimTableName + FK_COL_SUFFIX +
                " " + joinStudentAssignmentFragment();
    }

    private String joinStudentAssignmentFragment() {
        return LEFT_OUTER_JOIN + HibernateConsts.ASSIGNMENT_TABLE + ON +
                HibernateConsts.STUDENT_ASSIGNMENT_TABLE + DOT + HibernateConsts.ASSIGNMENT_TABLE + FK_COL_SUFFIX +
                EQUALS + HibernateConsts.ASSIGNMENT_TABLE + DOT + HibernateConsts.ASSIGNMENT_TABLE + ID_COL_SUFFIX + " ";
    }

    @Override
    public String toFromClause() {
        return HibernateConsts.STUDENT_ASSIGNMENT_TABLE + " " + joinStudentAssignmentFragment();
    }

    @Override
    public String toTableName() {
        return HibernateConsts.STUDENT_ASSIGNMENT_TABLE;
    }

    @Override
    public Set<Dimension> allJoinedTables() {
        Set<Dimension> set = MeasureSqlSerializer.super.allJoinedTables();
        set.add(Dimension.ASSIGNMENT);
        return set;
    }
}
