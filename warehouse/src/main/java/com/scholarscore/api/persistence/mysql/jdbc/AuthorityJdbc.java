package com.scholarscore.api.persistence.mysql.jdbc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.scholarscore.api.persistence.mysql.AuthorityPersistence;
import com.scholarscore.api.persistence.mysql.DbMappings;
import com.scholarscore.api.persistence.mysql.mapper.AuthorityMapper;
import com.scholarscore.models.Authority;
import com.scholarscore.models.HibernateConsts;

/**
 * Maintains authority or role information for identities that can authenticate for Spring Security
 * 
 * @author mattg
 */
public class AuthorityJdbc extends BaseJdbc implements AuthorityPersistence {

	private static String INSERT_AUTHORITY_SQL = "INSERT INTO `"+ 
            DbMappings.DATABASE +"`.`" + DbMappings.AUTHORITY_TABLE + "` " +
            "(" + DbMappings.AUTHORITY_USERNAME_COL + "," + DbMappings.AUTHORITY_AUTHORITY_COL + ")" +
            " VALUES (:" + DbMappings.AUTHORITY_USERNAME_COL + ", :" + DbMappings.AUTHORITY_AUTHORITY_COL + ")"; 
    private static String DELETE_AUTHORITY_SQL = "DELETE FROM `"+ 
            DbMappings.DATABASE +"`.`" + DbMappings.AUTHORITY_TABLE + "` " +
            "WHERE `" + DbMappings.AUTHORITY_USERNAME_COL + "`= :" + DbMappings.AUTHORITY_USERNAME_COL + "";
    private static String SELECT_ALL_AUTHORITYS_SQL = "SELECT * FROM `"+ 
            DbMappings.DATABASE +"`.`" + DbMappings.AUTHORITY_TABLE + "`" +
            "WHERE `" + DbMappings.AUTHORITY_USERNAME_COL + "`= :" + DbMappings.AUTHORITY_USERNAME_COL + "";

	@Override
	public List<Authority> selectAuthorities(String username) {
        Map<String, Object> params = new HashMap<>();     
        params.put(HibernateConsts.USER_NAME, username);
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
        params.put(DbMappings.AUTHORITY_USERNAME_COL, authority.getUsername());
        params.put(DbMappings.AUTHORITY_AUTHORITY_COL, authority.getAuthority());
        jdbcTemplate.update(INSERT_AUTHORITY_SQL, new MapSqlParameterSource(params), keyHolder);
	}

	@Override
	public void deleteAuthority(String username) {
        Map<String, Object> params = new HashMap<>();
        params.put(DbMappings.AUTHORITY_USERNAME_COL, username);
        jdbcTemplate.update(DELETE_AUTHORITY_SQL, new MapSqlParameterSource(params));
	}
}
