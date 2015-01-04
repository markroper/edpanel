package com.scholarscore.api.persistence.mysql.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.models.School;

public class SchoolMapper implements RowMapper<School> {
    @Override
    public School mapRow(ResultSet rs, int rowNum) throws SQLException {
        School school = new School();
        school.setId(rs.getLong(DbConst.SCHOOL_ID_COL));
        school.setName(rs.getString(DbConst.NAME_COL));
        return school;
    }

}
