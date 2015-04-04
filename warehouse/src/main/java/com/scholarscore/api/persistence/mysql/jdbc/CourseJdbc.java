package com.scholarscore.api.persistence.mysql.jdbc;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.api.persistence.mysql.EntityPersistence;
import com.scholarscore.api.persistence.mysql.mapper.CourseMapper;
import com.scholarscore.models.Course;

public class CourseJdbc extends EnhancedBaseJdbc<Course> implements EntityPersistence<Course> {
    private static String INSERT_COURSE_SQL = "INSERT INTO `"+ 
            DbConst.DATABASE +"`.`" + DbConst.COURSE_TABLE + "` " +
            "(" + DbConst.COURSE_NAME_COL + ", " + DbConst.SCHOOL_FK_COL + ")" +
            " VALUES (:" + DbConst.COURSE_NAME_COL + ", :" + DbConst.SCHOOL_FK_COL + ")";
    
    private static String UPDATE_COURSE_SQL = 
            "UPDATE `" + DbConst.DATABASE + "`.`" + DbConst.COURSE_TABLE + "` " + 
            "SET `" + DbConst.COURSE_NAME_COL + "`= :" + DbConst.COURSE_NAME_COL + ", `" +
            DbConst.SCHOOL_FK_COL + "`= :" + DbConst.SCHOOL_FK_COL + " " +
            "WHERE `" + DbConst.COURSE_ID_COL + "`= :" + DbConst.COURSE_ID_COL + "";
    
    private static String DELETE_COURSE_SQL = "DELETE FROM `"+ 
            DbConst.DATABASE +"`.`" + DbConst.COURSE_TABLE + "` " +
            "WHERE `" + DbConst.COURSE_ID_COL + "`= :" + DbConst.COURSE_ID_COL + "";
    
    private static String SELECT_ALL_COURSES_SQL = "SELECT * FROM `"+ 
            DbConst.DATABASE +"`.`" + DbConst.COURSE_TABLE + "` " +
            "WHERE `" + DbConst.SCHOOL_FK_COL + "` = :" + DbConst.SCHOOL_FK_COL;
    
    private static String SELECT_COURSE_SQL = SELECT_ALL_COURSES_SQL + 
            " AND `" + DbConst.COURSE_ID_COL + "`= :" + DbConst.COURSE_ID_COL;
    
    @Override
    public Collection<Course> selectAll(long schoolId) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.SCHOOL_FK_COL, new Long(schoolId));
        Collection<Course> courses = jdbcTemplate.query(
                SELECT_ALL_COURSES_SQL, 
                params,
                new CourseMapper());
        return courses;
    }

    @Override
    public Course select(long schoolId, long courseId) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.SCHOOL_FK_COL, new Long(schoolId));
        params.put(DbConst.COURSE_ID_COL, new Long(courseId));
        List<Course> courses = jdbcTemplate.query(
                SELECT_COURSE_SQL, 
                params, 
                new CourseMapper());
        Course course = null;
        if(null != courses && !courses.isEmpty()) {
            course = courses.get(0);
        }
        return course;
    }

    @Override
    public Long insert(long schoolId, Course course) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.COURSE_NAME_COL, course.getName());
        params.put(DbConst.SCHOOL_FK_COL, new Long(schoolId));
        jdbcTemplate.update(
                INSERT_COURSE_SQL, 
                new MapSqlParameterSource(params), 
                keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Long update(long schoolId, long courseId, Course course) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.COURSE_NAME_COL, course.getName());
        params.put(DbConst.SCHOOL_FK_COL, new Long(schoolId));
        params.put(DbConst.COURSE_ID_COL, courseId);
        jdbcTemplate.update(
                UPDATE_COURSE_SQL, 
                new MapSqlParameterSource(params));
        return courseId;
    }

    @Override
    public Long delete(long courseId) {
        Map<String, Object> params = new HashMap<>();
        params.put(DbConst.COURSE_ID_COL, new Long(courseId));
        jdbcTemplate.update(DELETE_COURSE_SQL, new MapSqlParameterSource(params));
        return courseId;
    }

    @Override
    public RowMapper<Course> getMapper() {
        return new CourseMapper();
    }

    @Override
    public String getTableName() {
        return DbConst.COURSE_TABLE;
    }

}
