package com.scholarscore.api.persistence.mysql.jdbc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.scholarscore.api.persistence.mysql.AuthorityPersistence;
import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.api.persistence.mysql.mapper.AuthorityMapper;
import com.scholarscore.models.Authority;

/**
 * Maintains authority or role information for identities that can authenticate for Spring Security
 * 
 * @author mattg
 */
public class AuthorityJdbc extends BaseJdbc implements AuthorityPersistence {

	private static String INSERT_AUTHORITY_SQL = "INSERT INTO `"+ 
            DbConst.DATABASE +"`.`" + DbConst.AUTHORITY_TABLE + "` " +
            "(" + DbConst.AUTHORITY_USERNAME_COL + "," + DbConst.AUTHORITY_AUTHORITY_COL + ")" +
            " VALUES (:" + DbConst.AUTHORITY_USERNAME_COL + ", :" + DbConst.AUTHORITY_AUTHORITY_COL + ")"; 
    private static String DELETE_AUTHORITY_SQL = "DELETE FROM `"+ 
            DbConst.DATABASE +"`.`" + DbConst.AUTHORITY_TABLE + "` " +
            "WHERE `" + DbConst.AUTHORITY_USERNAME_COL + "`= :" + DbConst.AUTHORITY_USERNAME_COL + "";
    private static String SELECT_ALL_AUTHORITYS_SQL = "SELECT * FROM `"+ 
            DbConst.DATABASE +"`.`" + DbConst.AUTHORITY_TABLE + "`" +
            "WHERE `" + DbConst.AUTHORITY_USERNAME_COL + "`= :" + DbConst.AUTHORITY_USERNAME_COL + "";

	@Override
	public List<Authority> selectAuthorities(String username) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.USER_USERNAME_COL, username);
        List<Authority> authorities = jdbcTemplate.query(
                SELECT_ALL_AUTHORITYS_SQL, 
                params,
                new AuthorityMapper());
		return authorities;
	}

	@Override
	public void createAuthority(Authority authority) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.AUTHORITY_USERNAME_COL, authority.getUsername());
        params.put(DbConst.AUTHORITY_AUTHORITY_COL, authority.getAuthority());
        jdbcTemplate.update(INSERT_AUTHORITY_SQL, new MapSqlParameterSource(params), keyHolder);
	}

	@Override
	public void deleteAuthority(String username) {
        Map<String, Object> params = new HashMap<>();
        params.put(DbConst.AUTHORITY_USERNAME_COL, username);
        jdbcTemplate.update(DELETE_AUTHORITY_SQL, new MapSqlParameterSource(params));
	}
}
