package com.scholarscore.api.persistence.mysql.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.models.SchoolYear;

public class SchoolYearMapper implements RowMapper<SchoolYear> {
    @Override
    public SchoolYear mapRow(ResultSet rs, int rowNum) throws SQLException {
        SchoolYear year = new SchoolYear();
        year.setId(rs.getLong(DbConst.SCHOOL_YEAR_ID_COL));
        year.setName(rs.getString(DbConst.NAME_COL));
        year.setStartDate(rs.getTimestamp(DbConst.START_DATE_COL));
        year.setEndDate(rs.getTimestamp(DbConst.END_DATE_COL));
        return year;
    }

}
