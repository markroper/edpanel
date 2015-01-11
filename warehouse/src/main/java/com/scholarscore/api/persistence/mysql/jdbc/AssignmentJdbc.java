package com.scholarscore.api.persistence.mysql.jdbc;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.api.persistence.mysql.EntityPersistence;
import com.scholarscore.api.persistence.mysql.mapper.AssignmentMapper;
import com.scholarscore.models.Assignment;
import com.scholarscore.models.GradedAssignment;

public class AssignmentJdbc extends BaseJdbc implements EntityPersistence<Assignment> {
    private static String INSERT_ASSIGNMENT_SQL = "INSERT INTO `"+ 
            DbConst.DATABASE +"`.`" + DbConst.ASSIGNMENT_TABLE + "` " +
            "(`" + DbConst.ASSIGNMENT_NAME_COL + 
            "`, `" + DbConst.SECTION_FK_COL + 
            "`, `" + DbConst.TYPE_FK_COL + 
            "`, `" + DbConst.DUE_DATE_COL + 
            "`, `" + DbConst.ASSIGNED_DATE_COL + 
            "`, `" + DbConst.AVAILABLE_POINTS_COL + "`)" +
            " VALUES (:" + DbConst.ASSIGNMENT_NAME_COL + 
            ", :" + DbConst.SECTION_FK_COL + 
            ", :" +  DbConst.TYPE_FK_COL + 
            ", :" + DbConst.DUE_DATE_COL + 
            ", :" + DbConst.ASSIGNED_DATE_COL + 
            ", :" + DbConst.AVAILABLE_POINTS_COL + ")";
    
    private static String UPDATE_ASSIGNMENT_SQL = 
            "UPDATE `" + DbConst.DATABASE + "`.`" + DbConst.ASSIGNMENT_TABLE + "` " + 
            "SET `" + 
            DbConst.ASSIGNMENT_NAME_COL + "`= :" + DbConst.ASSIGNMENT_NAME_COL + ", `" +
            DbConst.SECTION_FK_COL + "`= :" + DbConst.SECTION_FK_COL + ", `" +
            DbConst.TYPE_FK_COL + "`= :" + DbConst.TYPE_FK_COL + ", `" +
            DbConst.DUE_DATE_COL + "`= :" + DbConst.DUE_DATE_COL + ", `" +
            DbConst.ASSIGNED_DATE_COL + "`= :" + DbConst.ASSIGNED_DATE_COL + ", `" +
            DbConst.AVAILABLE_POINTS_COL + "`= :" + DbConst.AVAILABLE_POINTS_COL + " " +
            "WHERE `" + DbConst.ASSIGNMENT_ID_COL + "`= :" + DbConst.ASSIGNMENT_ID_COL + "";
    
    private static String DELETE_ASSIGNMENT_SQL = "DELETE FROM `"+ 
            DbConst.DATABASE +"`.`" + DbConst.ASSIGNMENT_TABLE + "` " +
            "WHERE `" + DbConst.ASSIGNMENT_ID_COL + "`= :" + DbConst.ASSIGNMENT_ID_COL + "";
    
    private static String SELECT_ALL_ASSIGNMENTS_SQL = "SELECT * FROM `"+ 
            DbConst.DATABASE +"`.`" + DbConst.ASSIGNMENT_TABLE + "` " +
            "WHERE `" + DbConst.SECTION_FK_COL + "` = :" + DbConst.SECTION_FK_COL;
    
    private static String SELECT_ASSIGNMENT_SQL = SELECT_ALL_ASSIGNMENTS_SQL + 
            " AND `" + DbConst.ASSIGNMENT_ID_COL + "`= :" + DbConst.ASSIGNMENT_ID_COL;
    
    @Override
    public Collection<Assignment> selectAll(long id) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.SECTION_FK_COL, new Long(id));
        Collection<Assignment> courses = jdbcTemplate.query(
                SELECT_ALL_ASSIGNMENTS_SQL, 
                params,
                new AssignmentMapper());
        return courses;
    }

    @Override
    public Assignment select(long parentId, long id) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.SECTION_FK_COL, new Long(parentId));
        params.put(DbConst.ASSIGNMENT_ID_COL, new Long(id));
        List<Assignment> assignments = jdbcTemplate.query(
                SELECT_ASSIGNMENT_SQL, 
                params, 
                new AssignmentMapper());
        Assignment assignment = null;
        if(null != assignments && !assignments.isEmpty()) {
            assignment = assignments.get(0);
        }
        return assignment;
    }

    @Override
    public Long insert(long parentId, Assignment entity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.ASSIGNMENT_NAME_COL, entity.getName());
        params.put(DbConst.SECTION_FK_COL, new Long(parentId));
        params.put(DbConst.TYPE_FK_COL, entity.getType().name());
        params.put(DbConst.DUE_DATE_COL, DbConst.resolveTimestamp(entity.getDueDate()));
        Timestamp assigned = null;
        if(entity instanceof GradedAssignment) {
            assigned = DbConst.resolveTimestamp(((GradedAssignment)entity).getAssignedDate());
        }
        params.put(DbConst.ASSIGNED_DATE_COL, DbConst.resolveTimestamp(assigned));
        params.put(DbConst.AVAILABLE_POINTS_COL, entity.getAvailablePoints());
        jdbcTemplate.update(
                INSERT_ASSIGNMENT_SQL, 
                new MapSqlParameterSource(params), 
                keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Long update(long parentId, long id, Assignment entity) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.ASSIGNMENT_NAME_COL, entity.getName());
        params.put(DbConst.SECTION_FK_COL, new Long(parentId));
        params.put(DbConst.TYPE_FK_COL, entity.getType().name());
        params.put(DbConst.DUE_DATE_COL, DbConst.resolveTimestamp(entity.getDueDate()));
        Timestamp assigned = null;
        if(entity instanceof GradedAssignment) {
            assigned = DbConst.resolveTimestamp(((GradedAssignment)entity).getAssignedDate());
        }
        params.put(DbConst.ASSIGNED_DATE_COL, DbConst.resolveTimestamp(assigned));
        params.put(DbConst.AVAILABLE_POINTS_COL, entity.getAvailablePoints());
        params.put(DbConst.ASSIGNMENT_ID_COL, new Long(id));
        jdbcTemplate.update(
                UPDATE_ASSIGNMENT_SQL, 
                new MapSqlParameterSource(params));
        return id;
    }

    @Override
    public Long delete(long id) {
        Map<String, Object> params = new HashMap<>();
        params.put(DbConst.ASSIGNMENT_ID_COL, new Long(id));
        jdbcTemplate.update(DELETE_ASSIGNMENT_SQL, new MapSqlParameterSource(params));
        return id;
    }

}
