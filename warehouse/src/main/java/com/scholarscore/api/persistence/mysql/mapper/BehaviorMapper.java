package com.scholarscore.api.persistence.mysql.mapper;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.models.Behavior;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * User: jordan
 * Date: 8/16/15
 * Time: 3:04 PM
 */
public class BehaviorMapper implements RowMapper<Behavior> {
    @Override
    public Behavior mapRow(ResultSet rs, int rowNum) throws SQLException {
        Behavior behavior = new Behavior();
        behavior.setId(rs.getLong(DbConst.BEHAVIOR_ID_COL));
        behavior.setRemoteSystemEventId(rs.getString(DbConst.BEHAVIOR_REMOTE_STUDENT_ID_COL));
        behavior.setName(rs.getString(DbConst.BEHAVIOR_NAME_COL));
        behavior.setBehaviorDate(rs.getString(DbConst.BEHAVIOR_DATE_COL));
        behavior.setBehaviorCategory(rs.getString(DbConst.BEHAVIOR_CATEGORY_COL));
        behavior.setPointValue(rs.getString(DbConst.BEHAVIOR_POINT_VALUE_COL));
        behavior.setRoster(rs.getString(DbConst.BEHAVIOR_ROSTER_COL));

        // TODO jordan: join to student and lookup/populate transient student on behavior
        // behavior.setStaffName(rs.getString(DbConst.BEHAVIOR_STAFF_NAME_COL));
        // behavior.setStudentName();

        return behavior;
        
        /* 
        * 
        *         SchoolYear year = new SchoolYear();
        year.setId(rs.getLong(DbConst.SCHOOL_YEAR_ID_COL));
        year.setName(rs.getString(DbConst.SCHOOL_YEAR_NAME_COL));
        year.setStartDate(rs.getTimestamp(DbConst.SCHOOL_YEAR_START_DATE_COL));
        year.setEndDate(rs.getTimestamp(DbConst.SCHOOL_YEAR_END_DATE_COL));
        return year;
* * */
    }
}
