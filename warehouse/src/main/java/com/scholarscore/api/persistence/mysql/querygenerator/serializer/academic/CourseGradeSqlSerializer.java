package com.scholarscore.api.persistence.mysql.querygenerator.serializer.academic;

import com.scholarscore.api.persistence.DbMappings;
import com.scholarscore.api.persistence.mysql.querygenerator.QuerySqlGenerator;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.BaseSqlSerializer;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.MeasureSqlSerializer;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.query.Dimension;

public class CourseGradeSqlSerializer extends BaseSqlSerializer implements MeasureSqlSerializer {
    @Override
    public String toSelectInner() {
        return HibernateConsts.SECTION_GRADE_TABLE + "." + HibernateConsts.STUDENT_SECTION_GRADE_GRADE;
    }

    @Override
    public String toJoinClause(Dimension dimToJoinUpon) {
        return super.toJoinClause(dimToJoinUpon) + sectionGradeJoin();
    }

    private String sectionGradeJoin() {
        return joinTable(HibernateConsts.SECTION_GRADE_TABLE);
    }
    
    @Override
    public String toFromClause() {
        return toTableName() + " " + sectionGradeJoin();
    }

    @Override
    public String toTableName() {
        return HibernateConsts.STUDENT_SECTION_GRADE_TABLE;
    }

}
