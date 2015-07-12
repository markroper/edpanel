package com.scholarscore.api.persistence.mysql.mapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.models.query.Query;

public class QueryMapper implements RowMapper<Query> {

    @Override
    public Query mapRow(ResultSet rs, int rowNum) throws SQLException {
        ObjectMapper mapper = new ObjectMapper();
        Query query;
        try {
            query = mapper.readValue(rs.getString(DbConst.REPORT_COL), Query.class);
            query.setId(rs.getLong(DbConst.REPORT_ID_COL));
            //TODO: Should we add the school FK to the Query POJO?
            //rs.getString(DbConst.SCHOOL_FK_COL);
        } catch (IOException e) {
            query = null;
        }
        return query;
    }

}
