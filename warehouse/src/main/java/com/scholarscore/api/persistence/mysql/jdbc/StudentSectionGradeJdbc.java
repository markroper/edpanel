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
import com.scholarscore.api.persistence.mysql.StudentSectionGradePersistence;
import com.scholarscore.api.persistence.mysql.mapper.StudentSectionGradeMapper;
import com.scholarscore.models.StudentSectionGrade;

public class StudentSectionGradeJdbc extends EnhancedBaseJdbc<StudentSectionGrade> implements StudentSectionGradePersistence {
    private final String INSERT_STUD_SECTION_GRADE_SQL = "INSERT INTO `"+
            DbConst.DATABASE +"`.`" + getTableName() + "` " +
            "(`" + DbConst.STUD_SECTION_GRADE_COMPLETE + 
            "`, `" + DbConst.STUD_SECTION_GRADE_GRADE + 
            "`, `" + DbConst.STUD_FK_COL + 
            "`, `" + DbConst.SECTION_FK_COL + "`)" +
            " VALUES (:" + DbConst.STUD_SECTION_GRADE_COMPLETE + 
            ", :" + DbConst.STUD_SECTION_GRADE_GRADE + 
            ", :" +  DbConst.STUD_FK_COL + 
            ", :" + DbConst.SECTION_FK_COL + ")";
    
    private final String UPDATE_STUD_SECTION_GRADE_SQL =
            "UPDATE `" + DbConst.DATABASE + "`.`" + getTableName() + "` " +
            "SET `" + 
            DbConst.STUD_SECTION_GRADE_COMPLETE + "`= :" + DbConst.STUD_SECTION_GRADE_COMPLETE + ", `" +
            DbConst.STUD_SECTION_GRADE_GRADE + "`= :" + DbConst.STUD_SECTION_GRADE_GRADE + ", `" +
            DbConst.STUD_FK_COL + "`= :" + DbConst.STUD_FK_COL + ", `" +
            DbConst.SECTION_FK_COL + "`= :" + DbConst.SECTION_FK_COL + 
            " WHERE `" + DbConst.STUD_FK_COL + "`= :" + DbConst.STUD_FK_COL + " " +
            "AND `" + DbConst.SECTION_FK_COL + "`= :" + DbConst.SECTION_FK_COL;
    
    private final String DELETE_STUD_SECTION_GRADE_SQL = "DELETE FROM `"+
            DbConst.DATABASE +"`.`" + getTableName() + "` " +
            "WHERE `" + DbConst.STUD_FK_COL + "`= :" + DbConst.STUD_FK_COL + " " +
            "AND `" + DbConst.SECTION_FK_COL + "`= :" + DbConst.SECTION_FK_COL;
    
    private final String SELECT_ALL_STUD_SECTION_GRADES_SQL = "SELECT * FROM `"+
            DbConst.DATABASE +"`.`" + getTableName() + "` " +
            "WHERE `" + DbConst.SECTION_FK_COL + "` = :" + DbConst.SECTION_FK_COL;
    
    private final String SELECT_STUD_SECTION_GRADE_SQL = SELECT_ALL_STUD_SECTION_GRADES_SQL +
            " AND `" + DbConst.STUD_FK_COL + "`= :" + DbConst.STUD_FK_COL;
    
    @Override
    public Collection<StudentSectionGrade> selectAll(long sectionId) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.SECTION_FK_COL, new Long(sectionId));
        return super.selectAll(params, SELECT_ALL_STUD_SECTION_GRADES_SQL);
    }

    @Override
    public StudentSectionGrade select(long sectionId, long studentId) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.SECTION_FK_COL, new Long(sectionId));
        params.put(DbConst.STUD_FK_COL, new Long(studentId));
        return super.select(params, SELECT_STUD_SECTION_GRADE_SQL);
    }

    @Override
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
        return null;
    }

    @Override
    public Long update(long sectionId, long studentId, StudentSectionGrade entity) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.SECTION_FK_COL, new Long(sectionId));
        params.put(DbConst.STUD_FK_COL, new Long(studentId));
        params.put(DbConst.STUD_SECTION_GRADE_COMPLETE, entity.getComplete());
        params.put(DbConst.STUD_SECTION_GRADE_GRADE, entity.getGrade());
        jdbcTemplate.update(
                UPDATE_STUD_SECTION_GRADE_SQL, 
                new MapSqlParameterSource(params));
        return null;
    }

    @Override
    public Long delete(long sectionId, long studentId) {
        Map<String, Object> params = new HashMap<>();
        params.put(DbConst.SECTION_FK_COL, new Long(sectionId));
        params.put(DbConst.STUD_FK_COL, new Long(studentId));
        return super.delete(params, DELETE_STUD_SECTION_GRADE_SQL);
    }
    
    @Override
    public RowMapper<StudentSectionGrade> getMapper() {
        return new StudentSectionGradeMapper();
    }

    @Override
    public String getTableName() {
        return DbConst.STUDENT_SECTION_GRADE_TABLE;
    }
}
