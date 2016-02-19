package com.scholarscore.api.persistence.mysql.querygenerator.serializer.academic;

import com.scholarscore.api.persistence.DbMappings;
import com.scholarscore.api.persistence.mysql.querygenerator.QuerySqlGenerator;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.BaseSqlSerializer;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.MeasureSqlSerializer;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.query.Dimension;

public class AssignmentGradeSqlSerializer extends BaseSqlSerializer implements MeasureSqlSerializer {
    
    @Override
    public String toSelectInner() {
        return
            toTableName() + DOT + HibernateConsts.STUDENT_ASSIGNMENT_AWARDED_POINTS +
            " / " +
            optionalJoinedTable() + DOT + HibernateConsts.ASSIGNMENT_AVAILABLE_POINTS;
    }
    
    @Override
    public String toTableName() {
        return HibernateConsts.STUDENT_ASSIGNMENT_TABLE;
    }

    @Override
    public String optionalJoinedTable() {
        return HibernateConsts.ASSIGNMENT_TABLE;
    }
}
