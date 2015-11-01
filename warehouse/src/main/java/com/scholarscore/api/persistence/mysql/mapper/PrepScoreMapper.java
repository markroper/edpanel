package com.scholarscore.api.persistence.mysql.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scholarscore.api.persistence.DbMappings;
import com.scholarscore.models.PrepScore;
import org.springframework.jdbc.core.RowMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * User: jordan
 * Date: 10/29/15
 * Time: 3:47 PM
 */
public class PrepScoreMapper implements RowMapper<PrepScore> {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public PrepScore mapRow(ResultSet rs, int rowNum) throws SQLException {
        PrepScore prepScore;
        try {
            // TODO Jordan: update this accordingly
            prepScore = mapper.readValue("value", PrepScore.class);
            // set id?
        } catch (IOException e) {
            prepScore = null;
        }
        return prepScore;
    }
}
