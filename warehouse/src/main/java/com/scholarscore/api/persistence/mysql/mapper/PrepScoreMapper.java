package com.scholarscore.api.persistence.mysql.mapper;

import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.PrepScore;
import org.springframework.jdbc.core.RowMapper;

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
        prepScore.setStudentId(rs.getLong(HibernateConsts.STUDENT_USER_FK));
        prepScore.setStartDate(rs.getDate(HibernateConsts.PREPSCORE_START_DATE));
        prepScore.setEndDate(rs.getDate(HibernateConsts.PREPSCORE_END_DATE));
        return prepScore;
    }
}
