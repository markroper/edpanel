package com.scholarscore.api.persistence.mysql.jdbc;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.api.persistence.mysql.StudentSectionGradePersistence;
import com.scholarscore.api.persistence.mysql.mapper.StudentSectionGradeMapper;
import com.scholarscore.models.StudentSectionGrade;

public class StudentSectionGradeJdbc extends BaseJdbc implements StudentSectionGradePersistence {
    private static String INSERT_STUD_SECTION_GRADE_SQL = "INSERT INTO `"+ 
            DbConst.DATABASE +"`.`" + DbConst.STUDENT_SECTION_GRADE_TABLE + "` " +
            "(`" + DbConst.STUD_SECTION_GRADE_COMPLETE + 
            "`, `" + DbConst.STUD_SECTION_GRADE_GRADE + 
            "`, `" + DbConst.STUD_FK_COL + 
            "`, `" + DbConst.SECTION_FK_COL + "`)" +
            " VALUES (:" + DbConst.STUD_SECTION_GRADE_COMPLETE + 
            ", :" + DbConst.STUD_SECTION_GRADE_GRADE + 
            ", :" +  DbConst.STUD_FK_COL + 
            ", :" + DbConst.SECTION_FK_COL + ")";
    
    private static String UPDATE_STUD_SECTION_GRADE_SQL = 
            "UPDATE `" + DbConst.DATABASE + "`.`" + DbConst.STUDENT_SECTION_GRADE_TABLE + "` " + 
            "SET `" + 
            DbConst.STUD_SECTION_GRADE_COMPLETE + "`= :" + DbConst.STUD_SECTION_GRADE_COMPLETE + ", `" +
            DbConst.STUD_SECTION_GRADE_GRADE + "`= :" + DbConst.STUD_SECTION_GRADE_GRADE + ", `" +
            DbConst.STUD_FK_COL + "`= :" + DbConst.STUD_FK_COL + ", `" +
            DbConst.SECTION_FK_COL + "`= :" + DbConst.SECTION_FK_COL +
            " WHERE `" + DbConst.STUD_SECTION_GRADE_ID_COL + "`= :" + DbConst.STUD_SECTION_GRADE_ID_COL + "";
    
    private static String DELETE_STUD_SECTION_GRADE_SQL = "DELETE FROM `"+ 
            DbConst.DATABASE +"`.`" + DbConst.STUDENT_SECTION_GRADE_TABLE + "` " +
            "WHERE `" + DbConst.STUD_SECTION_GRADE_ID_COL + "`= :" + DbConst.STUD_SECTION_GRADE_ID_COL + "";
    
    private static String SELECT_ALL_STUD_SECTION_GRADES_SQL = "SELECT * FROM `"+ 
            DbConst.DATABASE +"`.`" + DbConst.STUDENT_SECTION_GRADE_TABLE + "` " +
            "WHERE `" + DbConst.SECTION_FK_COL + "` = :" + DbConst.SECTION_FK_COL;
    
    private static String SELECT_STUD_SECTION_GRADE_SQL = SELECT_ALL_STUD_SECTION_GRADES_SQL + 
            " AND `" + DbConst.STUD_FK_COL + "`= :" + DbConst.STUD_FK_COL;
    
    @Override
    public Collection<StudentSectionGrade> selectAll(long id) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.SECTION_FK_COL, new Long(id));
        Collection<StudentSectionGrade> grades = jdbcTemplate.query(
                SELECT_ALL_STUD_SECTION_GRADES_SQL, 
                params,
                new StudentSectionGradeMapper());
        return grades;
    }

    @Override
    public StudentSectionGrade select(long sectionId, long studentId) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.SECTION_FK_COL, new Long(sectionId));
        params.put(DbConst.STUD_FK_COL, new Long(studentId));
        List<StudentSectionGrade> grades = jdbcTemplate.query(
                SELECT_STUD_SECTION_GRADE_SQL, 
                params, 
                new StudentSectionGradeMapper());
        StudentSectionGrade grade = null;
        if(null != grades && !grades.isEmpty()) {
            grade = grades.get(0);
        }
        return grade;
    }

    public Long insert(long sectionId, long studentId, StudentSectionGrade entity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.SECTION_FK_COL, new Long(sectionId));
        params.put(DbConst.STUD_FK_COL, new Long(studentId));
        params.put(DbConst.STUD_SECTION_GRADE_COMPLETE, entity.getComplete());
        params.put(DbConst.STUD_SECTION_GRADE_GRADE, entity.getGrade());
        jdbcTemplate.update(
                INSERT_STUD_SECTION_GRADE_SQL, 
                new MapSqlParameterSource(params), 
                keyHolder);
        return keyHolder.getKey().longValue();
    }

    public Long update(long sectionId, long studentId, long id, StudentSectionGrade entity) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.SECTION_FK_COL, new Long(sectionId));
        params.put(DbConst.STUD_FK_COL, new Long(studentId));
        params.put(DbConst.STUD_SECTION_GRADE_COMPLETE, entity.getComplete());
        params.put(DbConst.STUD_SECTION_GRADE_GRADE, entity.getGrade());
        params.put(DbConst.STUD_SECTION_GRADE_ID_COL, new Long(id));
        jdbcTemplate.update(
                UPDATE_STUD_SECTION_GRADE_SQL, 
                new MapSqlParameterSource(params));
        return id;
    }

    @Override
    public Long delete(long id) {
        Map<String, Object> params = new HashMap<>();
        params.put(DbConst.STUD_SECTION_GRADE_ID_COL, new Long(id));
        jdbcTemplate.update(DELETE_STUD_SECTION_GRADE_SQL, new MapSqlParameterSource(params));
        return id;
    }
}
