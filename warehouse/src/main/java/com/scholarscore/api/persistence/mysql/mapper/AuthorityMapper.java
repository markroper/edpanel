package com.scholarscore.api.persistence.mysql.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.models.Authority;

public class 
AuthorityMapper implements RowMapper<Authority> {

	@Override
	public Authority mapRow(ResultSet rs, int rowNum) throws SQLException {
		Authority authority = new Authority();
		authority.setUsername(rs.getString(DbConst.AUTHORITY_USERNAME_COL));
		authority.setAuthority(rs.getString(DbConst.AUTHORITY_AUTHORITY_COL));
		return authority;
	}
}