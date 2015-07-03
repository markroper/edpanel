package com.scholarscore.api.persistence.mysql.jdbc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.api.persistence.mysql.EntityPersistence;
import com.scholarscore.api.persistence.mysql.mapper.SectionMapper;
import com.scholarscore.models.Section;

public class SectionJdbc extends EnhancedBaseJdbc<Section> implements EntityPersistence<Section> {
    private static String INSERT_SECTION_SQL = "INSERT INTO `"+ 
            DbConst.DATABASE +"`.`" + DbConst.SECTION_TABLE + "` " +
            "(`" + DbConst.SECTION_NAME_COL + "`, `" + DbConst.TERM_FK_COL + "`, `" + 
            DbConst.COURSE_FK_COL + "`, `" +
            DbConst.ROOM_COL + "`, `" + DbConst.GRADE_FORMULA_COL + "`, `" + 
            DbConst.SECTION_START_DATE_COL + "`, `" + DbConst.SECTION_END_DATE_COL + "`)" +
            " VALUES (:" + DbConst.SECTION_NAME_COL + ", :" + DbConst.TERM_FK_COL + 
            ", :" + DbConst.COURSE_FK_COL +
            ", :" + DbConst.ROOM_COL + ", :" + DbConst.GRADE_FORMULA_COL +
            ", :" + DbConst.SECTION_START_DATE_COL + ", :" + DbConst.SECTION_END_DATE_COL + ")";
    
    private static String UPDATE_SECTION_SQL = 
            "UPDATE `" + DbConst.DATABASE + "`.`" + DbConst.SECTION_TABLE + "` " + 
            "SET `" + DbConst.SECTION_NAME_COL + "`= :" + DbConst.SECTION_NAME_COL + ", `" +
            DbConst.TERM_FK_COL + "`= :" + DbConst.TERM_FK_COL + ", `" +
            DbConst.COURSE_FK_COL + "`= :" + DbConst.COURSE_FK_COL + ", `" +
            DbConst.ROOM_COL + "`= :" + DbConst.ROOM_COL + ", `" +
            DbConst.GRADE_FORMULA_COL + "`= :" + DbConst.GRADE_FORMULA_COL + ", `" +
            DbConst.SECTION_START_DATE_COL + "`= :" + DbConst.SECTION_START_DATE_COL + ", `" +
            DbConst.SECTION_END_DATE_COL + "`= :" + DbConst.SECTION_END_DATE_COL + " " +
            "WHERE `" + DbConst.SECTION_ID_COL + "`= :" + DbConst.SECTION_ID_COL + "";
    
    private static final String SELECT_ALL_SECTIONS_SQL = "SELECT * FROM `"+
            DbConst.DATABASE +"`.`" + DbConst.SECTION_TABLE + "` " +
            "INNER JOIN `" + DbConst.DATABASE +"`.`" + DbConst.COURSE_TABLE + "` ON `" + 
            DbConst.COURSE_ID_COL + "` = `" + DbConst.COURSE_FK_COL +
            "` WHERE `" + DbConst.TERM_FK_COL + "` = :" + DbConst.TERM_FK_COL;
    
    private final String SELECT_SECTION_SQL = SELECT_ALL_SECTIONS_SQL +
            " AND `" + DbConst.SECTION_ID_COL + "`= :" + DbConst.SECTION_ID_COL;
    
    @Override
    public Collection<Section> selectAll(long termId) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.TERM_FK_COL, new Long(termId));
        return super.selectAll(params, SELECT_ALL_SECTIONS_SQL);
    }

    @Override
    public Section select(long termId, long sectionId) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.TERM_FK_COL, new Long(termId));
        params.put(DbConst.SECTION_ID_COL, new Long(sectionId));
        return super.select(params, SELECT_SECTION_SQL);
    }

    @Override
    public Long insert(long termId, Section term) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.SECTION_NAME_COL, term.getName());
        params.put(DbConst.TERM_FK_COL, new Long(termId));
        Long courseFk = null;
        if(null != term.getCourse()) {
            courseFk = new Long(term.getCourse().getId());
        }
        params.put(DbConst.COURSE_FK_COL, courseFk);
        params.put(DbConst.SECTION_START_DATE_COL, DbConst.resolveTimestamp(term.getStartDate()));
        params.put(DbConst.SECTION_END_DATE_COL, DbConst.resolveTimestamp(term.getEndDate()));
        params.put(DbConst.ROOM_COL, term.getRoom());
        try {
            params.put(DbConst.GRADE_FORMULA_COL, new ObjectMapper().writeValueAsString(term.getGradeFormula()));
        } catch(JsonProcessingException e) {
            //no op
        }
        jdbcTemplate.update(
                INSERT_SECTION_SQL, 
                new MapSqlParameterSource(params), 
                keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Long update(long termId, long sectionId, Section section) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.SECTION_NAME_COL, section.getName());
        params.put(DbConst.TERM_FK_COL, new Long(termId));
        Long courseFk = null;
        if(null != section.getCourse()) {
            courseFk = new Long(section.getCourse().getId());
        }
        params.put(DbConst.COURSE_FK_COL, courseFk);
        params.put(DbConst.SECTION_START_DATE_COL, DbConst.resolveTimestamp(section.getStartDate()));
        params.put(DbConst.SECTION_END_DATE_COL, DbConst.resolveTimestamp(section.getEndDate()));
        params.put(DbConst.ROOM_COL, section.getRoom());
        try {
            params.put(DbConst.GRADE_FORMULA_COL, new ObjectMapper().writeValueAsString(section.getGradeFormula()));
        } catch(JsonProcessingException e) {
            //No op
        }
        params.put(DbConst.SECTION_ID_COL, new Long(sectionId));
        jdbcTemplate.update(
                UPDATE_SECTION_SQL, 
                new MapSqlParameterSource(params));
        return sectionId;
    }

    @Override
    public RowMapper<Section> getMapper() {
        return new SectionMapper();
    }

    @Override
    public String getTableName() {
        return DbConst.SECTION_TABLE;
    }

}
