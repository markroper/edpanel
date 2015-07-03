package com.scholarscore.api.persistence.mysql.jdbc;

import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.api.persistence.mysql.TeacherPersistence;
import com.scholarscore.api.persistence.mysql.mapper.TeacherMapper;
import com.scholarscore.models.Teacher;

public class TeacherJdbc extends EnhancedBaseJdbc<Teacher> implements TeacherPersistence {
    
    private static String INSERT_TEACHER_SQL = "INSERT INTO `"+ 
            DbConst.DATABASE +"`.`" + DbConst.TEACHER_TABLE + "` " +
            "(" + DbConst.TEACHER_NAME_COL + ")" +
            " VALUES (:" + DbConst.TEACHER_NAME_COL + ")"; 
    
    private static String UPDATE_TEACHER_SQL = 
            "UPDATE `" + DbConst.DATABASE + "`.`" + DbConst.TEACHER_TABLE + "` " + 
            "SET `" + DbConst.TEACHER_NAME_COL + "`= :" + DbConst.TEACHER_NAME_COL + " " + 
            "WHERE `" + DbConst.TEACHER_ID_COL + "`= :" + DbConst.TEACHER_ID_COL + "";
    
    @Override
    public Long createTeacher(Teacher teacher) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.TEACHER_NAME_COL, teacher.getName());
        jdbcTemplate.update(INSERT_TEACHER_SQL, new MapSqlParameterSource(params), keyHolder);
        return keyHolder.getKey().longValue();
    }
    
    @Override
    public Long replaceTeacher(long teacherId, Teacher teacher) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.TEACHER_NAME_COL, teacher.getName());
        params.put(DbConst.TEACHER_ID_COL, new Long(teacherId));
        jdbcTemplate.update(UPDATE_TEACHER_SQL, new MapSqlParameterSource(params));
        return teacherId;
    }
    
    @Override
    public RowMapper<Teacher> getMapper() {
        return new TeacherMapper();
    }

    @Override
    public String getTableName() {
        return DbConst.TEACHER_TABLE;
    }
}
