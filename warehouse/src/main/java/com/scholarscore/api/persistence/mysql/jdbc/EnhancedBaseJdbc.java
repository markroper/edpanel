package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.mysql.DbConst;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.HashMap;
import java.util.Map;

/**
 * User: jordan
 * Date: 4/4/15
 * Time: 1:43 PM
 * 
 * This class is an exploration of refactoring common operations one step closer to BaseJdbc
 */
public abstract class EnhancedBaseJdbc extends BaseJdbc {

    private String DELETE_SQL = "DELETE FROM `"+
            DbConst.DATABASE +"`.`" + getTableName() + "` " +
            "WHERE `" + getIdColName() + "`= :" + getIdColName() + "";
    
    public Long delete(long id) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(getIdColName(), new Long(id));
        jdbcTemplate.update(getDeleteSQL(), new MapSqlParameterSource(params));
        return id;
    }

    // subclasses can override if using bespoke delete SQL
    protected String getDeleteSQL() {
        return DELETE_SQL;
    }
    
    // subclasses must override if id column does not match pattern (tablename_id)
    protected String getIdColName() {
        return getTableName() + "_id";
    }
    
    public abstract String getTableName();

}
