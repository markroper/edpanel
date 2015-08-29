package com.scholarscore.api.persistence.mysql.mapper;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.models.Behavior;
import com.scholarscore.models.Student;
import com.scholarscore.models.Teacher;
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
        behavior.setRemoteStudentId(rs.getString(DbConst.BEHAVIOR_REMOTE_STUDENT_ID_COL));
        behavior.setName(rs.getString(DbConst.BEHAVIOR_NAME_COL));
        behavior.setBehaviorDate(rs.getTimestamp(DbConst.BEHAVIOR_DATE_COL));
        behavior.setBehaviorCategory(rs.getString(DbConst.BEHAVIOR_CATEGORY_COL));
        behavior.setPointValue(rs.getString(DbConst.BEHAVIOR_POINT_VALUE_COL));
        behavior.setRoster(rs.getString(DbConst.BEHAVIOR_ROSTER_COL));

        StudentMapper studentMapper = new StudentMapper();
        behavior.setStudent(studentMapper.mapRow(rs, rowNum));
        TeacherMapper teacherMapper = new TeacherMapper();
        behavior.setTeacher(teacherMapper.mapRow(rs, rowNum));

        return behavior;
    }
}
