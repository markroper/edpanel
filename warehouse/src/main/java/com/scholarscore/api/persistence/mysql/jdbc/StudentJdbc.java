package com.scholarscore.api.persistence.mysql.jdbc;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.api.persistence.mysql.StudentPersistence;
import com.scholarscore.api.persistence.mysql.mapper.StudentMapper;
import com.scholarscore.models.Student;

public class StudentJdbc extends EnhancedBaseJdbc implements StudentPersistence {
    private static String INSERT_STUDENT_SQL = "INSERT INTO `"+ 
            DbConst.DATABASE +"`.`" + DbConst.STUDENT_TABLE + "` " +
            "(" + DbConst.STUDENT_NAME_COL + ")" +
            " VALUES (:" + DbConst.STUDENT_NAME_COL + ")";   
    private static String UPDATE_STUDENT_SQL = 
            "UPDATE `" + DbConst.DATABASE + "`.`" + DbConst.STUDENT_TABLE + "` " + 
            "SET `" + DbConst.STUDENT_NAME_COL + "`= :" + DbConst.STUDENT_NAME_COL + " " + 
            "WHERE `" + DbConst.STUDENT_ID_COL + "`= :" + DbConst.STUDENT_ID_COL + "";
    private static String SELECT_ALL_STUDENTS_SQL = "SELECT * FROM `"+
            DbConst.DATABASE +"`.`" + DbConst.STUDENT_TABLE + "`";
    private static String SELECT_STUDENT_SQL = SELECT_ALL_STUDENTS_SQL + 
            "WHERE `" + DbConst.STUDENT_ID_COL + "`= :" + DbConst.STUDENT_ID_COL + "";
    
    private static String SELECT_STUDENTS_IN_SECTION_SQL = SELECT_ALL_STUDENTS_SQL + 
            " INNER JOIN `" + DbConst.DATABASE +"`.`" + DbConst.STUDENT_SECTION_GRADE_TABLE + "`" + 
            " ON `" + DbConst.STUDENT_SECTION_GRADE_TABLE + "`.`" + DbConst.STUD_FK_COL + "` = `" +
            DbConst.STUDENT_TABLE + "`.`" + DbConst.STUDENT_ID_COL + "` " +
            "WHERE `" + DbConst.SECTION_FK_COL + "`= :" + DbConst.SECTION_FK_COL;

    @Override
    public Collection<Student> selectAllStudents() {
        Collection<Student> students = jdbcTemplate.query(SELECT_ALL_STUDENTS_SQL, 
                new StudentMapper());
        return students;
    }
    @Override
    public Collection<Student> selectAllStudentsInSection(long sectionId) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.SECTION_FK_COL, new Long(sectionId));
        List<Student> students = jdbcTemplate.query(
                SELECT_STUDENTS_IN_SECTION_SQL, 
                params,
                new StudentMapper());
        return students;
    }
    
    @Override
    public Student selectStudent(long studentId) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.STUDENT_ID_COL, new Long(studentId));
        List<Student> students = jdbcTemplate.query(
                SELECT_STUDENT_SQL, 
                params,
                new StudentMapper());
        Student student = null;
        if(null != students && !students.isEmpty()) {
            student = students.get(0);
        }
        return student;
    }
    @Override
    public Long createStudent(Student student) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.STUDENT_NAME_COL, student.getName());
        jdbcTemplate.update(INSERT_STUDENT_SQL, new MapSqlParameterSource(params), keyHolder);
        return keyHolder.getKey().longValue();
    }
    @Override
    public Long replaceStudent(long studentId, Student student) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.STUDENT_NAME_COL, student.getName());
        params.put(DbConst.STUDENT_ID_COL, new Long(studentId));
        jdbcTemplate.update(UPDATE_STUDENT_SQL, new MapSqlParameterSource(params));
        return studentId;
    }
    @Override
    public Long deleteStudent(long studentId) {
        return super.delete(studentId);
    }

    @Override
    public String getTableName() {
        return DbConst.STUDENT_TABLE;
    }
}
