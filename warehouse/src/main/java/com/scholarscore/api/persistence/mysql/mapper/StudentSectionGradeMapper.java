package com.scholarscore.api.persistence.mysql.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.models.StudentSectionGrade;

public class StudentSectionGradeMapper implements RowMapper<StudentSectionGrade> {

    @Override
    public StudentSectionGrade mapRow(ResultSet rs, int rowNum)
            throws SQLException {
        StudentSectionGrade ssg = new StudentSectionGrade();
        Boolean complete = rs.getBoolean(DbConst.STUD_SECTION_GRADE_COMPLETE);
        if(!rs.wasNull()) {
            ssg.setComplete(complete);
        }
        Double grade = rs.getDouble(DbConst.STUD_SECTION_GRADE_GRADE);
        if(!rs.wasNull()) {
            ssg.setGrade(grade);
        }
        ssg.setId(rs.getLong(DbConst.STUD_SECTION_GRADE_ID_COL));
        return ssg;
    }

}
