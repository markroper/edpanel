package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.AuthorityPersistence;
import com.scholarscore.api.persistence.DbMappings;
import com.scholarscore.api.persistence.mysql.mapper.AuthorityMapper;
import com.scholarscore.models.Authority;
import com.scholarscore.models.HibernateConsts;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maintains authority or role information for identities that can authenticate for Spring Security
 * 
 * @author mattg
 */
public class AuthorityJdbc extends BaseJdbc implements AuthorityPersistence {

	private static String INSERT_AUTHORITY_SQL = "INSERT INTO `"+ 
            DbMappings.AUTHORITY_TABLE + "` " +
            "(" + DbMappings.AUTHORITY_USER_ID_COL + "," + DbMappings.AUTHORITY_AUTHORITY_COL + ")" +
            " VALUES (:" + DbMappings.AUTHORITY_USER_ID_COL + ", :" + DbMappings.AUTHORITY_AUTHORITY_COL + ")";
    private static String DELETE_AUTHORITY_SQL = "DELETE FROM `"+ 
            DbMappings.AUTHORITY_TABLE + "` " +
            "WHERE `" + DbMappings.AUTHORITY_USER_ID_COL + "`= :" + DbMappings.AUTHORITY_USER_ID_COL + "";
    private static String SELECT_ALL_AUTHORITIES_JOIN_USER_SQL = "SELECT * FROM `"+
            DbMappings.AUTHORITY_TABLE + "`" +
            HibernateConsts.USERS_TABLE + "` ON " +
            "`" + HibernateConsts.USERS_TABLE + "`.`" + HibernateConsts.USER_ID + "`=`" +
            DbMappings.AUTHORITY_TABLE + "`.`" + DbMappings.AUTHORITY_USER_ID_COL + "` " +
             "WHERE `" + HibernateConsts.USERS_TABLE + "`.`" + HibernateConsts.USER_NAME + "`= :" + HibernateConsts.USER_NAME;

    
	@Override
	public List<Authority> selectAuthorities(String username) {
        Map<String, Object> params = new HashMap<>();     
        params.put(HibernateConsts.USER_NAME, username);
        List<Authority> authorities = jdbcTemplate.query(
                SELECT_ALL_AUTHORITIES_JOIN_USER_SQL,
                params,
                new AuthorityMapper());
		return authorities;
	}

	@Override
	public void createAuthority(Authority authority) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Map<String, Object> params = new HashMap<>();     
        params.put(DbMappings.AUTHORITY_USER_ID_COL, authority.getUserId());
        params.put(DbMappings.AUTHORITY_AUTHORITY_COL, authority.getAuthority());
        jdbcTemplate.update(INSERT_AUTHORITY_SQL, new MapSqlParameterSource(params), keyHolder);
	}

	@Override
	public void deleteAuthority(Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put(DbMappings.AUTHORITY_USER_ID_COL, userId);
        jdbcTemplate.update(DELETE_AUTHORITY_SQL, new MapSqlParameterSource(params));
	}
}
