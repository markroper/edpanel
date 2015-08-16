package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.mysql.BehaviorPersistence;
import com.scholarscore.api.persistence.mysql.DbConst;
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

    private static final String INSERT_BEHAVIOR_SQL = "";

    @Override
    public Long createBehavior(long studentId, Behavior behavior) {

        KeyHolder keyHolder = new GeneratedKeyHolder();
        Map<String, Object> params = new HashMap<>();
        params.put(DbConst.BEHAVIOR_STUDENT_FK_COL, studentId);
        params.put(DbConst.BEHAVIOR_NAME_COL, behavior.getName());
        params.put(DbConst.BEHAVIOR_CATEGORY_COL, behavior.getBehaviorCategory());
        params.put(DbConst.BEHAVIOR_POINT_VALUE_COL, behavior.getPointValue());
        params.put(DbConst.BEHAVIOR_ROSTER_COL, behavior.getRoster());
        params.put(DbConst.BEHAVIOR_STAFF_NAME_COL, behavior.getStaffName());
        params.put(DbConst.BEHAVIOR_DATE_COL, behavior.getBehaviorDate());
        params.put(DbConst.BEHAVIOR_REMOTE_STUDENT_ID_COL, behavior.getRemoteSystemEventId());
        jdbcTemplate.update(INSERT_BEHAVIOR_SQL, new MapSqlParameterSource(params), keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Long delete(long studentId, long behaviorId) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public Collection<Behavior> selectAll(long studentId) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public Behavior select(long studentId, long behaviorId) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public Long replaceBehavior(long studentId, long behaviorId, Behavior behavior) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public RowMapper<Behavior> getMapper() {
        throw new UnsupportedOperationException("mapper not implemented yet");
    }

    @Override
    public String getTableName() {
        return DbConst.BEHAVIOR_TABLE;
    }
}
