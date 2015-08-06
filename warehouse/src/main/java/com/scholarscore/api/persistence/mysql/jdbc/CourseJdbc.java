package com.scholarscore.api.persistence.mysql.jdbc;

import java.util.Collection;
import java.util.HashMap;
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
    private static final String INSERT_COURSE_SQL = "INSERT INTO `"+
            DbConst.DATABASE +"`.`" + DbConst.COURSE_TABLE + "` " +
            "(" + DbConst.COURSE_NAME_COL + ", " + DbConst.SCHOOL_FK_COL + ", " + DbConst.COURSE_NUMBER_COL + ", " + DbConst.COURSE_SOURCE_SYSTEM_ID_COL + ")" +
            " VALUES (:" + DbConst.COURSE_NAME_COL + ", :" + DbConst.SCHOOL_FK_COL + ", :" + DbConst.COURSE_NUMBER_COL + ", :" + DbConst.COURSE_SOURCE_SYSTEM_ID_COL + ")";
    
    private static final String UPDATE_COURSE_SQL =
            "UPDATE `" + DbConst.DATABASE + "`.`" + DbConst.COURSE_TABLE + "` " + 
            "SET `" + DbConst.COURSE_NAME_COL + "`= :" + DbConst.COURSE_NAME_COL + ", `" +
                    DbConst.COURSE_NUMBER_COL + "`= :" + DbConst.COURSE_NUMBER_COL + ", `" +
                    DbConst.COURSE_SOURCE_SYSTEM_ID_COL + "`= :" + DbConst.COURSE_SOURCE_SYSTEM_ID_COL + ", `" +
                    DbConst.SCHOOL_FK_COL + "`= :" + DbConst.SCHOOL_FK_COL + " " +
            "WHERE `" + DbConst.COURSE_ID_COL + "`= :" + DbConst.COURSE_ID_COL + "";
    
    private static final String DELETE_COURSE_SQL = "DELETE FROM `"+
            DbConst.DATABASE +"`.`" + DbConst.COURSE_TABLE + "` " +
            "WHERE `" + DbConst.COURSE_ID_COL + "`= :" + DbConst.COURSE_ID_COL + "";
    
    private static final String SELECT_ALL_COURSES_SQL = "SELECT * FROM `"+
            DbConst.DATABASE +"`.`" + DbConst.COURSE_TABLE + "` " +
            "WHERE `" + DbConst.SCHOOL_FK_COL + "` = :" + DbConst.SCHOOL_FK_COL;
    
    private static final String SELECT_COURSE_SQL = SELECT_ALL_COURSES_SQL +
            " AND `" + DbConst.COURSE_ID_COL + "`= :" + DbConst.COURSE_ID_COL;
    
    @Override
    public Collection<Course> selectAll(long schoolId) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.SCHOOL_FK_COL, new Long(schoolId));
        return super.selectAll(params, SELECT_ALL_COURSES_SQL);
    }

    @Override
    public Course select(long schoolId, long courseId) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.SCHOOL_FK_COL, new Long(schoolId));
        params.put(DbConst.COURSE_ID_COL, new Long(courseId));
        return super.select(params, SELECT_COURSE_SQL);
    }

    @Override
    public Long insert(long schoolId, Course course) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.COURSE_NAME_COL, course.getName());
        params.put(DbConst.SCHOOL_FK_COL, new Long(schoolId));
        params.put(DbConst.COURSE_NUMBER_COL, course.getNumber());
        params.put(DbConst.COURSE_SOURCE_SYSTEM_ID_COL, course.getSourceSystemId());
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
        params.put(DbConst.COURSE_NUMBER_COL, course.getNumber());
        params.put(DbConst.COURSE_SOURCE_SYSTEM_ID_COL, course.getSourceSystemId());
        jdbcTemplate.update(
                UPDATE_COURSE_SQL, 
                new MapSqlParameterSource(params));
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
