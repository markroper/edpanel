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
import com.scholarscore.api.persistence.mysql.StudentAssignmentPersistence;
import com.scholarscore.api.persistence.mysql.mapper.StudentAssignmentMapper;
import com.scholarscore.models.StudentAssignment;

public class StudentAssignmentJdbc extends EnhancedBaseJdbc<StudentAssignment> 
        implements StudentAssignmentPersistence {
    private static String INSERT_STUD_ASSIGNMENT_SQL = "INSERT INTO `"+ 
            DbConst.DATABASE +"`.`" + DbConst.STUDENT_ASSIGNMENT_TABLE + "` " +
            "(`" + DbConst.STUD_ASSIGNMENT_NAME_COL + 
            "`, `" + DbConst.ASSIGNMENT_FK_COL + 
            "`, `" + DbConst.STUD_COMPLETED_COL + 
            "`, `" + DbConst.STUD_COMPLETION_DATE_COL + 
            "`, `" + DbConst.STUD_AWARDED_POINTS + 
            "`, `" + DbConst.STUD_FK_COL + "`)" +
            " VALUES (:" + DbConst.STUD_ASSIGNMENT_NAME_COL + 
            ", :" + DbConst.ASSIGNMENT_FK_COL + 
            ", :" +  DbConst.STUD_COMPLETED_COL + 
            ", :" + DbConst.STUD_COMPLETION_DATE_COL + 
            ", :" + DbConst.STUD_AWARDED_POINTS + 
            ", :" + DbConst.STUD_FK_COL + ")";
    
    private static String UPDATE_STUD_ASSIGNMENT_SQL = 
            "UPDATE `" + DbConst.DATABASE + "`.`" + DbConst.STUDENT_ASSIGNMENT_TABLE + "` " + 
            "SET `" + 
            DbConst.STUD_ASSIGNMENT_NAME_COL + "`= :" + DbConst.STUD_ASSIGNMENT_NAME_COL + ", `" +
            DbConst.ASSIGNMENT_FK_COL + "`= :" + DbConst.ASSIGNMENT_FK_COL + ", `" +
            DbConst.STUD_COMPLETED_COL + "`= :" + DbConst.STUD_COMPLETED_COL + ", `" +
            DbConst.STUD_COMPLETION_DATE_COL + "`= :" + DbConst.STUD_COMPLETION_DATE_COL + ", `" +
            DbConst.STUD_AWARDED_POINTS + "`= :" + DbConst.STUD_AWARDED_POINTS + ", `" +
            DbConst.STUD_FK_COL + "`= :" + DbConst.STUD_FK_COL + " " +
            "WHERE `" + DbConst.STUD_ASSIGNMENT_ID_COL + "`= :" + DbConst.STUD_ASSIGNMENT_ID_COL + "";

    private static String SELECT_ALL_STUD_ASSIGNMENTS_SQL = "SELECT * FROM `"+ 
            DbConst.DATABASE +"`.`" + DbConst.STUDENT_ASSIGNMENT_TABLE + "` " +
            "INNER JOIN `" + DbConst.DATABASE +"`.`" + DbConst.ASSIGNMENT_TABLE + "` " +
            "ON `" + DbConst.ASSIGNMENT_TABLE + "`.`" + DbConst.ASSIGNMENT_ID_COL + "` = `" +
            DbConst.STUDENT_ASSIGNMENT_TABLE + "`.`" + DbConst.ASSIGNMENT_FK_COL + "` " + 
            "INNER JOIN `" + DbConst.DATABASE +"`.`" + DbConst.STUDENT_TABLE + "` " +
            "ON `" + DbConst.STUDENT_TABLE + "`.`" + DbConst.STUDENT_ID_COL + "` = `" +
            DbConst.STUDENT_ASSIGNMENT_TABLE + "`.`" + DbConst.STUD_FK_COL + "` " + 
            "WHERE `" + DbConst.ASSIGNMENT_FK_COL + "` = :" + DbConst.ASSIGNMENT_FK_COL;
    
    private static String SELECT_STUD_ASSIGNMENTS_ONE_STUDENT_SQL = "SELECT * FROM `"+ 
            DbConst.DATABASE +"`.`" + DbConst.STUDENT_ASSIGNMENT_TABLE + "` " +
            "INNER JOIN `" + DbConst.DATABASE +"`.`" + DbConst.ASSIGNMENT_TABLE + "` " +
            "ON `" + DbConst.ASSIGNMENT_TABLE + "`.`" + DbConst.ASSIGNMENT_ID_COL + "` = `" +
            DbConst.STUDENT_ASSIGNMENT_TABLE + "`.`" + DbConst.ASSIGNMENT_FK_COL + "` " + 
            "INNER JOIN `" + DbConst.DATABASE +"`.`" + DbConst.STUDENT_TABLE + "` " +
            "ON `" + DbConst.STUDENT_TABLE + "`.`" + DbConst.STUDENT_ID_COL + "` = `" +
            DbConst.STUDENT_ASSIGNMENT_TABLE + "`.`" + DbConst.STUD_FK_COL + "` " + 
            "WHERE `" + DbConst.STUD_FK_COL + "` = :" + DbConst.STUD_FK_COL +
            " and `" + DbConst.SECTION_FK_COL + "` = :" + DbConst.SECTION_FK_COL;
    
    private static String SELECT_STUD_ASSIGNMENT_SQL = SELECT_ALL_STUD_ASSIGNMENTS_SQL + 
            " AND `" + DbConst.STUD_ASSIGNMENT_ID_COL + "`= :" + DbConst.STUD_ASSIGNMENT_ID_COL;

    @Override
    public Collection<StudentAssignment> selectAll(long id) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.ASSIGNMENT_FK_COL, new Long(id));
        return super.selectAll(params, SELECT_ALL_STUD_ASSIGNMENTS_SQL);
    }
    
    @Override
    public Collection<StudentAssignment> selectAllAssignmentsOneSectionOneStudent(
            long sectionId, long studentId) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.SECTION_FK_COL, new Long(sectionId));
        params.put(DbConst.STUD_FK_COL, new Long(studentId));
        return super.selectAll(params, SELECT_STUD_ASSIGNMENTS_ONE_STUDENT_SQL);
    }

    @Override
    public StudentAssignment select(long parentId, long id) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.ASSIGNMENT_FK_COL, new Long(parentId));
        params.put(DbConst.STUD_ASSIGNMENT_ID_COL, new Long(id));
        return super.select(params, SELECT_STUD_ASSIGNMENT_SQL);
    }

    @Override
    public Long insert(long parentId, StudentAssignment entity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.STUD_ASSIGNMENT_NAME_COL, entity.getName());
        params.put(DbConst.ASSIGNMENT_FK_COL, new Long(parentId));
        params.put(DbConst.STUD_COMPLETED_COL, entity.getCompleted());
        params.put(DbConst.STUD_COMPLETION_DATE_COL, DbConst.resolveTimestamp(entity.getCompletionDate()));
        params.put(DbConst.STUD_AWARDED_POINTS, entity.getAwardedPoints());
        params.put(DbConst.STUD_FK_COL, entity.getStudent().getId());
        jdbcTemplate.update(
                INSERT_STUD_ASSIGNMENT_SQL, 
                new MapSqlParameterSource(params), 
                keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Long update(long parentId, long id, StudentAssignment entity) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.STUD_ASSIGNMENT_NAME_COL, entity.getName());
        params.put(DbConst.ASSIGNMENT_FK_COL, new Long(parentId));
        params.put(DbConst.STUD_COMPLETED_COL, entity.getCompleted());
        params.put(DbConst.STUD_COMPLETION_DATE_COL, DbConst.resolveTimestamp(entity.getCompletionDate()));
        params.put(DbConst.STUD_AWARDED_POINTS, entity.getAwardedPoints());
        params.put(DbConst.STUD_FK_COL, entity.getStudent().getId());
        params.put(DbConst.STUD_ASSIGNMENT_ID_COL, new Long(id));
        jdbcTemplate.update(
                UPDATE_STUD_ASSIGNMENT_SQL, 
                new MapSqlParameterSource(params));
        return id;
    }

    @Override
    public RowMapper<StudentAssignment> getMapper() {
        return new StudentAssignmentMapper();
    }

    @Override
    public String getTableName() {
        return DbConst.STUDENT_ASSIGNMENT_TABLE;
    }
}
