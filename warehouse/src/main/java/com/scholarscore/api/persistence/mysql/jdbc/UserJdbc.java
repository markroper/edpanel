package com.scholarscore.api.persistence.mysql.jdbc;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.api.persistence.mysql.UserPersistence;
import com.scholarscore.api.persistence.mysql.mapper.UserMapper;
import com.scholarscore.models.User;

/**
 * Maintain User identities separate from Student / Teacher entities for Spring Security
 * 
 * @author mattg
 */
public class UserJdbc extends BaseJdbc implements UserPersistence {

	private static String INSERT_USER_SQL = "INSERT INTO `"+ 
            DbConst.DATABASE +"`.`" + DbConst.USER_TABLE + "` " +
            "(" + DbConst.USER_USERNAME_COL + "," + DbConst.USER_PASSWORD_COL + "," + DbConst.USER_ENABLED_COL + ")" +
            " VALUES (:" + DbConst.USER_USERNAME_COL + ", :" + DbConst.USER_PASSWORD_COL + ", :" + DbConst.USER_ENABLED_COL + ")"; 
    private static String UPDATE_USER_SQL = 
            "UPDATE `" + DbConst.DATABASE + "`.`" + DbConst.USER_TABLE + "` " + 
            "SET `" + DbConst.USER_USERNAME_COL + "`= :" + DbConst.USER_USERNAME_COL + ", " +
            	 "`" + DbConst.USER_PASSWORD_COL + "`= :" + DbConst.USER_PASSWORD_COL + ", " +
            	 "`" + DbConst.USER_ENABLED_COL + "` = :" + DbConst.USER_ENABLED_COL + ")" +
            "WHERE `" + DbConst.USER_USERNAME_COL + "`= :" + DbConst.USER_USERNAME_COL + "";
    private static String DELETE_USER_SQL = "DELETE FROM `"+ 
            DbConst.DATABASE +"`.`" + DbConst.USER_TABLE + "` " +
            "WHERE `" + DbConst.USER_USERNAME_COL + "`= :" + DbConst.USER_USERNAME_COL + "";
    private static String SELECT_ALL_USERS_SQL = "SELECT * FROM `"+ 
            DbConst.DATABASE +"`.`" + DbConst.USER_TABLE + "`";
    private static String SELECT_USER_SQL = SELECT_ALL_USERS_SQL + 
            "WHERE `" + DbConst.USER_USERNAME_COL + "`= :" + DbConst.USER_USERNAME_COL + "";
    
	
	@Override
	public Collection<User> selectAllUsers() {
        Collection<User> users = jdbcTemplate.query(SELECT_ALL_USERS_SQL, 
                new UserMapper());
        return users;
	}

	@Override
	public User selectUser(String username) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.USER_USERNAME_COL, username);
        List<User> users = jdbcTemplate.query(
                SELECT_USER_SQL, 
                params,
                new UserMapper());
        User user = null;
        if(null != users && !users.isEmpty()) {
            user = users.get(0);
        }
        return user;
	}

	@Override
	public String createUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.USER_USERNAME_COL, user.getUsername());
        params.put(DbConst.USER_PASSWORD_COL, user.getPassword());
        params.put(DbConst.USER_ENABLED_COL, user.getEnabled());
        try {
        	jdbcTemplate.update(INSERT_USER_SQL, new MapSqlParameterSource(params), keyHolder);
        }
        catch (Exception e) {
        	e.printStackTrace();
        	throw e;
        }
    	return keyHolder.getKey().toString();
    }
	
    @Override
    public String replaceUser(String username, User user) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.USER_USERNAME_COL, user.getUsername());
        params.put(DbConst.USER_PASSWORD_COL, user.getPassword());
        params.put(DbConst.USER_ENABLED_COL, user.getEnabled());
        jdbcTemplate.update(UPDATE_USER_SQL, new MapSqlParameterSource(params));
        return username;
    }
    
    @Override
    public String deleteUser(String username) {
        Map<String, Object> params = new HashMap<>();
        params.put(DbConst.USER_USERNAME_COL, username);
        jdbcTemplate.update(DELETE_USER_SQL, new MapSqlParameterSource(params));
        return username;
    }
}
