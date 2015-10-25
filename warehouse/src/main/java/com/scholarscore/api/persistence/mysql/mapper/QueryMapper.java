package com.scholarscore.api.persistence.mysql.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scholarscore.api.persistence.DbMappings;
import com.scholarscore.models.query.Query;
import org.springframework.jdbc.core.RowMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QueryMapper implements RowMapper<Query> {
    private static final ObjectMapper mapper = new ObjectMapper();
    @Override
    public Query mapRow(ResultSet rs, int rowNum) throws SQLException {
        Query query;
        try {
            query = mapper.readValue(rs.getString(DbMappings.REPORT_COL), Query.class);
            query.setId(rs.getLong(DbMappings.REPORT_ID_COL));
            //TODO: Should we add the school FK to the Query POJO?
            //rs.getString(DbConst.SCHOOL_FK_COL);
        } catch (IOException e) {
            query = null;
        }
        return query;
    }

}
