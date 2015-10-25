package com.scholarscore.api.persistence.mysql.mapper;

import com.scholarscore.api.persistence.DbMappings;
import com.scholarscore.models.Authority;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class 
AuthorityMapper implements RowMapper<Authority> {

	@Override
	public Authority mapRow(ResultSet rs, int rowNum) throws SQLException {
		Authority authority = new Authority();
		authority.setUserId(rs.getLong(DbMappings.AUTHORITY_USER_ID_COL));
		authority.setAuthority(rs.getString(DbMappings.AUTHORITY_AUTHORITY_COL));
		return authority;
	}
}
