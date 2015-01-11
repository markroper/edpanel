package com.scholarscore.api.persistence.mysql.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.models.Course;

public class CourseMapper implements RowMapper<Course> {

    @Override
    public Course mapRow(ResultSet rs, int rowNum) throws SQLException {
        Course course = new Course();
        course.setId(rs.getLong(DbConst.COURSE_ID_COL));
        course.setName(rs.getString(DbConst.COURSE_NAME_COL));
        return course;
    }
}
