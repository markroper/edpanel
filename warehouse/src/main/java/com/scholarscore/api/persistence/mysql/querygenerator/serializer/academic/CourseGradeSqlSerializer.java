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
        return optionalJoinedTable() + "." + HibernateConsts.STUDENT_SECTION_GRADE_GRADE;
    }

    @Override
    public Dimension toTableDimension() {
        return Dimension.STUDENT_SECTION_GRADE;
    }

    @Override
    public Dimension toSecondTableDimension() {
        return Dimension.SECTION_GRADE;
    }
}
