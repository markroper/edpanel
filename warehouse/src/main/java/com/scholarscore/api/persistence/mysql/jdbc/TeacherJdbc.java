package com.scholarscore.api.persistence.mysql.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.api.persistence.mysql.TeacherPersistence;
import com.scholarscore.api.persistence.mysql.mapper.TeacherMapper;
import com.scholarscore.models.Teacher;

public class TeacherJdbc extends SimpleORM<Teacher> implements TeacherPersistence {

    private static final LinkedHashSet<String> fieldNames = new LinkedHashSet<>(Arrays.asList(
            DbConst.TEACHER_NAME_COL,
            DbConst.TEACHER_SOURCE_SYSTEM_ID_COL,
            DbConst.TEACHER_USERNAME_COL,
            DbConst.TEACHER_HOME_STREET,
            DbConst.TEACHER_HOME_CITY,
            DbConst.TEACHER_HOME_STATE,
            DbConst.TEACHER_HOME_POSTAL_CODE,
            DbConst.TEACHER_HOME_PHONE));

    @Override
    public Long createTeacher(Teacher teacher) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.TEACHER_NAME_COL, teacher.getName());
        params.put(DbConst.TEACHER_SOURCE_SYSTEM_ID_COL, teacher.getSourceSystemId());
        if (null != teacher.getLogin()) {
            params.put(DbConst.TEACHER_USERNAME_COL, teacher.getLogin().getUsername());
        }
        if (null != teacher.getHomeAddress()) {
            params.put(DbConst.TEACHER_HOME_STREET, teacher.getHomeAddress().getStreet());
            params.put(DbConst.TEACHER_HOME_CITY, teacher.getHomeAddress().getCity());
            params.put(DbConst.TEACHER_HOME_STATE, teacher.getHomeAddress().getState());
            params.put(DbConst.TEACHER_HOME_POSTAL_CODE, teacher.getHomeAddress().getPostalCode());
        }
        params.put(DbConst.TEACHER_HOME_PHONE, teacher.getHomePhone());
        jdbcTemplate.update(generateInsert(), new MapSqlParameterSource(params), keyHolder);
        return keyHolder.getKey().longValue();
    }
    
    @Override
    public Long replaceTeacher(long teacherId, Teacher teacher) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.TEACHER_NAME_COL, teacher.getName());
        params.put(DbConst.TEACHER_SOURCE_SYSTEM_ID_COL, teacher.getSourceSystemId());
        if (null != teacher.getLogin()) {
            params.put(DbConst.TEACHER_USERNAME_COL, teacher.getLogin().getUsername());
        }
        params.put(DbConst.TEACHER_ID_COL, new Long(teacherId));
        if (null != teacher.getHomeAddress()) {
            params.put(DbConst.TEACHER_HOME_STREET, teacher.getHomeAddress().getStreet());
            params.put(DbConst.TEACHER_HOME_CITY, teacher.getHomeAddress().getCity());
            params.put(DbConst.TEACHER_HOME_STATE, teacher.getHomeAddress().getState());
            params.put(DbConst.TEACHER_HOME_POSTAL_CODE, teacher.getHomeAddress().getPostalCode());
        }
        params.put(DbConst.TEACHER_HOME_PHONE, teacher.getHomePhone());
        jdbcTemplate.update(generateUpdate(), new MapSqlParameterSource(params));
        return teacherId;
    }
    
    @Override
    public RowMapper<Teacher> getMapper() {
        return new RowMapper<Teacher>() {
            @Override
            public Teacher mapRow(ResultSet rs, int rowNum) throws SQLException {
                Teacher teacher = new Teacher();
                teacher.setId(rs.getLong(DbConst.TEACHER_ID_COL));
                teacher.setName(rs.getString(DbConst.TEACHER_NAME_COL));
                return teacher;
            }
        };
    }

    @Override
    public LinkedHashSet<String> getFieldNames() {
        return fieldNames;
    }

    @Override
    public String getTableName() {
        return DbConst.TEACHER_TABLE;
    }
}
