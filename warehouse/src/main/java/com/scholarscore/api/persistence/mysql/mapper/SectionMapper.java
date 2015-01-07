package com.scholarscore.api.persistence.mysql.mapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.models.AssignmentType;
import com.scholarscore.models.GradeFormula;
import com.scholarscore.models.Section;

public class SectionMapper implements RowMapper<Section> {
    @Override
    public Section mapRow(ResultSet rs, int rowNum) throws SQLException {
        Section section = new Section();
        section.setName(rs.getString(DbConst.SECTION_NAME_COL));
        section.setId(rs.getLong(DbConst.SECTION_ID_COL));
        section.setStartDate(rs.getTimestamp(DbConst.SECTION_START_DATE_COL));
        section.setEndDate(rs.getTimestamp(DbConst.SECTION_END_DATE_COL));
        section.setRoom(rs.getString(DbConst.ROOM_COL));
        try {
            Map<AssignmentType, Integer> weights = new ObjectMapper().readValue(
                    rs.getString(DbConst.GRADE_FORMULA_COL), 
                    new TypeReference<HashMap<AssignmentType, Integer>>(){});
            if(null != weights && !weights.isEmpty()) {
                section.setGradeFormula(new GradeFormula(weights));
            }
        } catch (IOException e) {
            //TODO: handle this case...
            section.setGradeFormula(null);
        }
        CourseMapper cm = new CourseMapper();
        section.setCourse(cm.mapRow(rs, rowNum));
        return section;
    }

}
