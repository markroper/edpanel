package com.scholarscore.api.persistence.mysql.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.models.StudentAssignment;

public class StudentAssignmentMapper implements RowMapper<StudentAssignment> {

    @Override
    public StudentAssignment mapRow(ResultSet rs, int rowNum)
            throws SQLException {
        StudentAssignment ass = new StudentAssignment();
        Boolean completed = rs.getBoolean(DbConst.STUD_COMPLETED_COL);
        if(!rs.wasNull()) {
            ass.setCompleted(completed);
        }
        ass.setId(rs.getLong(DbConst.STUD_ASSIGNMENT_ID_COL));
        ass.setName(rs.getString(DbConst.STUD_ASSIGNMENT_NAME_COL));
        ass.setCompletionDate(rs.getTimestamp(DbConst.STUD_COMPLETION_DATE_COL));
        Long awardedPoints = rs.getLong(DbConst.STUD_AWARDED_POINTS);
        if(!rs.wasNull()) {
            ass.setAwardedPoints(awardedPoints);
        }
        AssignmentMapper assMapper = new AssignmentMapper();
        ass.setAssignment(assMapper.mapRow(rs, rowNum));
        
        StudentMapper studMapper = new StudentMapper();
        ass.setStudent(studMapper.mapRow(rs, rowNum));
        return ass;
    }

}
