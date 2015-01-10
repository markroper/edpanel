package com.scholarscore.api.persistence.mysql.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.models.Assignment;
import com.scholarscore.models.AssignmentType;
import com.scholarscore.models.AttendanceAssignment;
import com.scholarscore.models.GradedAssignment;

public class AssignmentMapper implements RowMapper<Assignment>{

    @Override
    public Assignment mapRow(ResultSet rs, int rowNum)
            throws SQLException {
        //TODO: remove this when we remove the distinction between Assignment and SectionAssignment
        AssignmentType type = AssignmentType.toAssignmentType(rs.getString(DbConst.TYPE_FK_COL));
        Assignment ass = null;
        switch(type) {
            case GRADED:
                ass = new GradedAssignment();
                ((GradedAssignment)ass).setAssignedDate(rs.getTimestamp(DbConst.ASSIGNED_DATE_COL));
                break;
            case ATTENDANCE:
                ass = new AttendanceAssignment();
                break;
            default:
                throw new RuntimeException("Assignment type not supported: " + type);
        }
        ass.setType(type);
        Long availPoints = rs.getLong(DbConst.AVAILABLE_POINTS_COL);
        if(!rs.wasNull()) {
            ass.setAvailablePoints(availPoints);
        }
        ass.setDueDate(rs.getTimestamp(DbConst.DUE_DATE_COL));
        ass.setId(rs.getLong(DbConst.ASSIGNMENT_ID_COL));
        ass.setName(rs.getString(DbConst.ASSIGNMENT_NAME_COL));
        return ass;
    }

}
