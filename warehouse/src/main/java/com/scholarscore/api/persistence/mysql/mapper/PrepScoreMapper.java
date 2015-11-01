package com.scholarscore.api.persistence.mysql.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scholarscore.api.persistence.DbMappings;
import com.scholarscore.models.HibernateConsts;
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

    @Override
    public PrepScore mapRow(ResultSet rs, int rowNum) throws SQLException {
        PrepScore prepScore = new PrepScore();
        prepScore.setScore(rs.getLong(HibernateConsts.BEHAVIOR_POINT_VALUE));
        prepScore.setStartDate(rs.getDate(HibernateConsts.BEHAVIOR_DATE));
        // TODO Jordan: need endDate, and student_fk perhaps?
        return prepScore;
    }
}
