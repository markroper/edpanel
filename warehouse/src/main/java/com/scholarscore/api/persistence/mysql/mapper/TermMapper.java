package com.scholarscore.api.persistence.mysql.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.models.Term;

public class TermMapper implements RowMapper<Term> {
    @Override
    public Term mapRow(ResultSet rs, int rowNum) throws SQLException {
        Term term = new Term();
        term.setId(rs.getLong(DbConst.TERM_ID_COL));
        term.setName(rs.getString(DbConst.NAME_COL));
        term.setStartDate(rs.getTimestamp(DbConst.START_DATE_COL));
        term.setEndDate(rs.getTimestamp(DbConst.END_DATE_COL));
        return term;
    }
}
