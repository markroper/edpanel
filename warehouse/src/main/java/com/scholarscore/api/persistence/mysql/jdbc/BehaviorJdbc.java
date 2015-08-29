package com.scholarscore.api.persistence.mysql.jdbc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.scholarscore.api.persistence.mysql.BehaviorPersistence;
import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.api.persistence.mysql.mapper.BehaviorMapper;
import com.scholarscore.models.Behavior;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * User: jordan
 * Date: 8/8/15
 * Time: 6:30 PM
 */
public class BehaviorJdbc extends EnhancedBaseJdbc<Behavior> implements BehaviorPersistence {

    private static final String INSERT_BEHAVIOR_SQL = "INSERT INTO " +
            "`" + DbConst.DATABASE + "`.`" + DbConst.BEHAVIOR_TABLE + "` " +
            "(`" + DbConst.BEHAVIOR_NAME_COL +
            "`, `" + DbConst.BEHAVIOR_CATEGORY_COL +
            "`, `" + DbConst.BEHAVIOR_DATE_COL +
            "`, `" + DbConst.BEHAVIOR_POINT_VALUE_COL +
            "`, `" + DbConst.BEHAVIOR_REMOTE_STUDENT_ID_COL +
            "`, `" + DbConst.BEHAVIOR_ROSTER_COL +
            "`, `" + DbConst.BEHAVIOR_STUDENT_FK_COL +
            "`, `" + DbConst.BEHAVIOR_TEACHER_FK_COL + "`)" +
            " VALUES (" + ":" + DbConst.BEHAVIOR_NAME_COL +
            ", :" + DbConst.BEHAVIOR_CATEGORY_COL +
            ", :" + DbConst.BEHAVIOR_DATE_COL +
            ", :" + DbConst.BEHAVIOR_POINT_VALUE_COL +
            ", :" + DbConst.BEHAVIOR_REMOTE_STUDENT_ID_COL +
            ", :" + DbConst.BEHAVIOR_ROSTER_COL +
            ", :" + DbConst.BEHAVIOR_STUDENT_FK_COL +
            ", :" + DbConst.BEHAVIOR_TEACHER_FK_COL + ")";
    
    // update sql - ignore student and teacher as these fields cannot be updated
    private static final String UPDATE_BEHAVIOR_SQL = "UPDATE " +
            "`" + DbConst.DATABASE + "`.`" + DbConst.BEHAVIOR_TABLE + "` " + 
            "SET `" + DbConst.BEHAVIOR_NAME_COL + "`= :" + DbConst.BEHAVIOR_NAME_COL + ", "
            + backtickEquals(DbConst.BEHAVIOR_CATEGORY_COL) + ", "
            + backtickEquals(DbConst.BEHAVIOR_DATE_COL) + ", "
            + backtickEquals(DbConst.BEHAVIOR_POINT_VALUE_COL) + ", "
            + backtickEquals(DbConst.BEHAVIOR_REMOTE_STUDENT_ID_COL) + ", "
            + backtickEquals(DbConst.BEHAVIOR_ROSTER_COL) + ", "
            + backtickEquals(DbConst.BEHAVIOR_POINT_VALUE_COL) + ", "
            + backtickEquals(DbConst.BEHAVIOR_REMOTE_STUDENT_ID_COL)
            + " WHERE " + backtickEquals(DbConst.BEHAVIOR_ID_COL);
    
    private String SELECT_ALL_BEHAVIORS_SQL = SELECT_ALL_SQL + " "

            // join on student (first draft -- may change)
            + "JOIN `" + DbConst.DATABASE + "`.`" + DbConst.STUDENT_TABLE + "` ON `"
            + getTableName() + "`.`" + DbConst.BEHAVIOR_STUDENT_FK_COL + "`=`"
            + DbConst.STUDENT_TABLE + "`.`" + DbConst.STUDENT_ID_COL + "` "
            // join on teacher (first draft -- may change)
            + "JOIN `" + DbConst.DATABASE + "`.`" + DbConst.TEACHER_TABLE + "` ON `"
            + getTableName() + "`.`" + DbConst.BEHAVIOR_TEACHER_FK_COL + "`=`"
            + DbConst.TEACHER_TABLE + "`.`" + DbConst.TEACHER_ID_COL + "` "

            + "WHERE " + backtickEquals(DbConst.BEHAVIOR_STUDENT_FK_COL);
    
    private String SELECT_BEHAVIOR_SQL = SELECT_ALL_BEHAVIORS_SQL + " " + 
            "AND " + backtickEquals(DbConst.BEHAVIOR_ID_COL);
    
    private final String DELETE_BEHAVIOR_SQL = DELETE_SQL + " " + 
            "AND " + backtickEquals(DbConst.BEHAVIOR_STUDENT_FK_COL);

    @Override
    public Long createBehavior(long studentId, Behavior behavior) /*throws JsonProcessingException*/ {

        KeyHolder keyHolder = new GeneratedKeyHolder();
        Map<String, Object> params = new HashMap<>();
        params.put(DbConst.BEHAVIOR_STUDENT_FK_COL, studentId);
        params.put(DbConst.BEHAVIOR_NAME_COL, behavior.getName());
        params.put(DbConst.BEHAVIOR_CATEGORY_COL, behavior.getBehaviorCategory());
        params.put(DbConst.BEHAVIOR_POINT_VALUE_COL, behavior.getPointValue());
        params.put(DbConst.BEHAVIOR_ROSTER_COL, behavior.getRoster());
        params.put(DbConst.BEHAVIOR_DATE_COL, behavior.getBehaviorDate());
        params.put(DbConst.BEHAVIOR_REMOTE_STUDENT_ID_COL, behavior.getRemoteStudentId());
        if (behavior.getTeacher() != null) {
            params.put(DbConst.BEHAVIOR_TEACHER_FK_COL, behavior.getTeacher().getId());
        }
        jdbcTemplate.update(INSERT_BEHAVIOR_SQL, new MapSqlParameterSource(params), keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Collection<Behavior> selectAll(long studentId) {
        Map<String, Object> params = new HashMap<>();
        params.put(DbConst.BEHAVIOR_STUDENT_FK_COL, studentId);
        return super.selectAll(params, SELECT_ALL_BEHAVIORS_SQL);
    }

    @Override
    public Behavior select(long studentId, long behaviorId) {
        Map<String, Object> params = new HashMap<>();
        params.put(DbConst.BEHAVIOR_STUDENT_FK_COL, studentId);
        params.put(DbConst.BEHAVIOR_ID_COL, behaviorId);
        return super.select(params, SELECT_BEHAVIOR_SQL);
    }

    @Override
    public Long replaceBehavior(long studentId, long behaviorId, Behavior behavior) {
        Map<String, Object> params = new HashMap<>();
        params.put(DbConst.BEHAVIOR_ID_COL, behaviorId);
        params.put(DbConst.BEHAVIOR_NAME_COL, behavior.getName());
        params.put(DbConst.BEHAVIOR_CATEGORY_COL, behavior.getBehaviorCategory());
        params.put(DbConst.BEHAVIOR_POINT_VALUE_COL, behavior.getPointValue());
        params.put(DbConst.BEHAVIOR_ROSTER_COL, behavior.getRoster());
        params.put(DbConst.BEHAVIOR_DATE_COL, behavior.getBehaviorDate());
        params.put(DbConst.BEHAVIOR_REMOTE_STUDENT_ID_COL, behavior.getRemoteStudentId());
        jdbcTemplate.update(UPDATE_BEHAVIOR_SQL, new MapSqlParameterSource(params));
        return behaviorId;
    }

    @Override
    public Long delete(long studentId, long behaviorId) {
        Map<String, Object> params = new HashMap<>();
        params.put(getIdColName(), behaviorId);
        params.put(DbConst.BEHAVIOR_STUDENT_FK_COL, studentId);
        return this.delete(params, DELETE_BEHAVIOR_SQL);
    }

    @Override
    public RowMapper<Behavior> getMapper() {
        return new BehaviorMapper();
    }

    @Override
    public String getTableName() {
        return DbConst.BEHAVIOR_TABLE;
    }

    private static String backtickEquals(String colName) {
        return "`" + colName + "`= :" + colName;
    }
}
