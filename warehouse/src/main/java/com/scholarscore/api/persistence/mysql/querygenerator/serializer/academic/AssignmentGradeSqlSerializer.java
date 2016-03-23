package com.scholarscore.api.persistence.mysql.querygenerator.serializer.academic;

import com.scholarscore.api.persistence.mysql.querygenerator.serializer.BaseSqlSerializer;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.MeasureSqlSerializer;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.query.Dimension;

public class AssignmentGradeSqlSerializer extends BaseSqlSerializer implements MeasureSqlSerializer {

    @Override
    // Note: non-standard relationships (not using conventional _id / _fk) will BREAK this method. override if needed
    public String toJoinClause(Dimension dimToJoinUpon) {
        if(!Dimension.SECTION.equals(dimToJoinUpon)) {

            return super.toJoinClause(dimToJoinUpon);
        } else {
            String join = LEFT_OUTER_JOIN + toSecondTableDimension() + ON +
                    toSecondTableDimension() + DOT + dimToJoinUpon + "_fk " +
                    EQUALS + dimToJoinUpon + DOT + dimToJoinUpon + "_id ";
            join += LEFT_OUTER_JOIN + toTableDimension() + ON +
                    toTableDimension() + DOT + toSecondTableDimension() + "_fk " + EQUALS +
                    toSecondTableDimension() + DOT + toSecondTableDimension() + "_id ";
            return join;
        }
    }

    @Override
    public String toSelectInner() {
        return
            toTableName() + DOT + HibernateConsts.STUDENT_ASSIGNMENT_AWARDED_POINTS +
            " / " +
            optionalJoinedTable() + DOT + HibernateConsts.ASSIGNMENT_AVAILABLE_POINTS;
    }
    
    @Override
    public Dimension toTableDimension() {
        return Dimension.STUDENT_ASSIGNMENT;
    }

    @Override
    public Dimension toSecondTableDimension() {
        return Dimension.ASSIGNMENT;
    }
}
