package com.scholarscore.api.persistence.mysql.mapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.models.GradeFormula;
import com.scholarscore.models.Section;

public class SectionMapper implements RowMapper<Section> {
    @Override
    public Section mapRow(ResultSet rs, int rowNum) throws SQLException {
        Section section = new Section();
        section.setName(rs.getString(DbConst.NAME_COL));
        section.setId(rs.getLong(DbConst.SECTION_ID_COL));
        section.setStartDate(rs.getTimestamp(DbConst.START_DATE_COL));
        section.setEndDate(rs.getTimestamp(DbConst.END_DATE_COL));
        section.setRoom(rs.getString(DbConst.ROOM_COL));
        try {
            section.setGradeFormula(new GradeFormula(rs.getString(DbConst.GRADE_FORMULA_COL)));
        } catch (IOException e) {
            //TODO: handle this case...
            section.setGradeFormula(null);
        }
        return section;
    }

}
