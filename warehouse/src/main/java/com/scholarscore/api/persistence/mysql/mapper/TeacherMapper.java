package com.scholarscore.api.persistence.mysql.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.models.Teacher;

public class TeacherMapper implements RowMapper<Teacher> {

    @Override
    public Teacher mapRow(ResultSet rs, int rowNum) throws SQLException {
        Teacher teacher = new Teacher();
        teacher.setId(rs.getLong(DbConst.TEACHER_ID_COL));
        teacher.setName(rs.getString(DbConst.TEACHER_NAME_COL));
        return teacher;
    }

}