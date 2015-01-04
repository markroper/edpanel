package com.scholarscore.api.persistence.mysql.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.models.Student;

public class StudentMapper implements RowMapper<Student>{

    @Override
    public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
        Student student = new Student();
        student.setId(rs.getLong(DbConst.STUDENT_ID_COL));
        student.setName(rs.getString(DbConst.NAME_COL));
        return student;
    }

}
