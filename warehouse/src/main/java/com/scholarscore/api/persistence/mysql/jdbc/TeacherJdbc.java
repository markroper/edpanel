package com.scholarscore.api.persistence.mysql.jdbc;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.api.persistence.mysql.TeacherPersistence;
import com.scholarscore.api.persistence.mysql.mapper.TeacherMapper;
import com.scholarscore.models.Teacher;

public class TeacherJdbc extends BaseJdbc implements TeacherPersistence {
    private static String INSERT_TEACHER_SQL = "INSERT INTO `"+ 
            DbConst.DATABASE +"`.`" + DbConst.TEACHER_TABLE + "` " +
            "(" + DbConst.TEACHER_NAME_COL + ")" +
            " VALUES (:" + DbConst.TEACHER_NAME_COL + ")"; 
    private static String UPDATE_TEACHER_SQL = 
            "UPDATE `" + DbConst.DATABASE + "`.`" + DbConst.TEACHER_TABLE + "` " + 
            "SET `" + DbConst.TEACHER_NAME_COL + "`= :" + DbConst.TEACHER_NAME_COL + " " + 
            "WHERE `" + DbConst.TEACHER_ID_COL + "`= :" + DbConst.TEACHER_ID_COL + "";
    private static String DELETE_TEACHER_SQL = "DELETE FROM `"+ 
            DbConst.DATABASE +"`.`" + DbConst.TEACHER_TABLE + "` " +
            "WHERE `" + DbConst.TEACHER_ID_COL + "`= :" + DbConst.TEACHER_ID_COL + "";
    private static String SELECT_ALL_TEACHERS_SQL = "SELECT * FROM `"+ 
            DbConst.DATABASE +"`.`" + DbConst.TEACHER_TABLE + "`";
    private static String SELECT_TEACHER_SQL = SELECT_ALL_TEACHERS_SQL + 
            "WHERE `" + DbConst.TEACHER_ID_COL + "`= :" + DbConst.TEACHER_ID_COL + "";
    
    @Override
    public Collection<Teacher> selectAllTeachers() {
        Collection<Teacher> teachers = jdbcTemplate.query(SELECT_ALL_TEACHERS_SQL, 
                new TeacherMapper());
        return teachers;
    }
    
    @Override
    public Teacher selectTeacher(long teacherId) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.TEACHER_ID_COL, new Long(teacherId));
        List<Teacher> teachers = jdbcTemplate.query(
                SELECT_TEACHER_SQL, 
                params,
                new TeacherMapper());
        Teacher teacher = null;
        if(null != teachers && !teachers.isEmpty()) {
            teacher = teachers.get(0);
        }
        return teacher;
    }
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
    public Long deleteTeacher(long teacherId) {
        Map<String, Object> params = new HashMap<>();
        params.put(DbConst.TEACHER_ID_COL, new Long(teacherId));
        jdbcTemplate.update(DELETE_TEACHER_SQL, new MapSqlParameterSource(params));
        return teacherId;
    }
}
