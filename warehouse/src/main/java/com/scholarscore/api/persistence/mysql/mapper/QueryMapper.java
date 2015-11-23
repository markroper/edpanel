package com.scholarscore.api.persistence.mysql.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.scholarscore.api.persistence.DbMappings;
import com.scholarscore.models.query.Query;
import org.springframework.jdbc.core.RowMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QueryMapper implements RowMapper<Query> {
    private static final ObjectMapper MAPPER = new ObjectMapper().
            setSerializationInclusion(JsonInclude.Include.NON_NULL).
            registerModule(new JavaTimeModule()).
            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    @Override
    public Query mapRow(ResultSet rs, int rowNum) throws SQLException {
        Query query;
        try {
            query = MAPPER.readValue(rs.getString(DbMappings.REPORT_COL), Query.class);
            query.setId(rs.getLong(DbMappings.REPORT_ID_COL));
            //TODO: Should we add the school FK to the Query POJO?
            //rs.getString(DbConst.SCHOOL_FK_COL);
        } catch (IOException e) {
            query = null;
        }
        return query;
    }

}
