package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.mysql.DbConst;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: jordan
 * Date: 4/4/15
 * Time: 1:43 PM
 * 
 * This class is an exploration of refactoring common operations one step closer to BaseJdbc
 */
public abstract class EnhancedBaseJdbc<T> extends BaseJdbc {

    protected final String SELECT_ALL_SQL = "SELECT * FROM `" +
            DbConst.DATABASE +"`.`" + getTableName() + "`";

    private final String SELECT_SQL = SELECT_ALL_SQL + " " + 
            "WHERE `" + getIdColName() + "`= :" + getIdColName();

    protected final String DELETE_SQL = "DELETE FROM `"+
            DbConst.DATABASE +"`.`" + getTableName() + "` " +
            "WHERE `" + getIdColName() + "`= :" + getIdColName();
    
    // -- SELECT ALL --
    public Collection<T> selectAll() {
        return this.selectAll(null, SELECT_ALL_SQL);
    }

    protected Collection<T> selectAll(Map<String, Object> params, String selectAllSql) {
        return jdbcTemplate.query(selectAllSql,
                params,
                getMapper());
    }
    // -- END SELECT ALL --

    // -- SELECT --
    public T select(long id) { 
        Map<String, Object> params = new HashMap<>();
        params.put(getIdColName(), new Long(id));
        return this.select(params, SELECT_SQL);
    }
    
    protected T select(Map<String, Object> params, String selectSql) {
        System.out.println("SQL: " + selectSql + ", Map: " + params);
        T result = jdbcTemplate.queryForObject(
                selectSql,
                params,
                getMapper());
        return result;
    }
    // -- END SELECT --
    
    // -- DELETE --
    public Long delete(long id) {
        Map<String, Object> params = new HashMap<>();
        params.put(getIdColName(), new Long(id));
        return this.delete(params, DELETE_SQL);
    }

    // override delete if need to pass in different params (this will always require custom SQL)
    protected Long delete(Map<String, Object> params, String deleteSql) {
        jdbcTemplate.update(deleteSql, new MapSqlParameterSource(params));
        return null;
    }
    // -- END DELETE --

    // -- GENERAL --
    // subclasses must override if id column does not match pattern (tablename_id)
    protected String getIdColName() {
        return getTableName() + "_id";
    }

    // These are needed for all subclasses
    public abstract RowMapper<T> getMapper();

    public abstract String getTableName();
    // -- END GENERAL --
}
