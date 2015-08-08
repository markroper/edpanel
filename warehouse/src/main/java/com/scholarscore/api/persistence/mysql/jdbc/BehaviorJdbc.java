package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.mysql.BehaviorPersistence;
import com.scholarscore.models.Behavior;
import org.springframework.jdbc.core.RowMapper;

/**
 * User: jordan
 * Date: 8/8/15
 * Time: 6:30 PM
 */
public class BehaviorJdbc extends EnhancedBaseJdbc<Behavior> implements BehaviorPersistence {
    @Override
    public Long createBehavior(Behavior behavior) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public Long replaceBehavior(long behaviorId, Behavior behavior) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public RowMapper<Behavior> getMapper() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public String getTableName() {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
