package com.scholarscore.api.persistence.mysql.jdbc;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.api.persistence.mysql.SectionPersistence;
import com.scholarscore.api.persistence.mysql.mapper.SectionMapper;
import com.scholarscore.models.Section;

public class SectionJdbc extends BaseJdbc implements SectionPersistence {
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
    
    private static String DELETE_SECTION_SQL = "DELETE FROM `"+ 
            DbConst.DATABASE +"`.`" + DbConst.SECTION_TABLE + "` " +
            "WHERE `" + DbConst.SECTION_ID_COL + "`= :" + DbConst.SECTION_ID_COL + "";
    
    private static String SELECT_ALL_SECTIONS_SQL = "SELECT * FROM `"+ 
            DbConst.DATABASE +"`.`" + DbConst.SECTION_TABLE + "` " +
            "INNER JOIN `" + DbConst.DATABASE +"`.`" + DbConst.COURSE_TABLE + "` ON `" + 
            DbConst.COURSE_ID_COL + "` = `" + DbConst.COURSE_FK_COL +
            "` WHERE `" + DbConst.TERM_FK_COL + "` = :" + DbConst.TERM_FK_COL;
    
    private static String SELECT_SECTION_SQL = SELECT_ALL_SECTIONS_SQL + 
            " AND `" + DbConst.SECTION_ID_COL + "`= :" + DbConst.SECTION_ID_COL;
    
    @Override
    public Collection<Section> selectAllSections(long termId) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.TERM_FK_COL, new Long(termId));
        Collection<Section> sections = jdbcTemplate.query(
                SELECT_ALL_SECTIONS_SQL, 
                params,
                new SectionMapper());
        return sections;
    }

    @Override
    public Section selectSection(long termId, long sectionId) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.TERM_FK_COL, new Long(termId));
        params.put(DbConst.SECTION_ID_COL, new Long(sectionId));  
        List<Section> sections = jdbcTemplate.query(
                SELECT_SECTION_SQL, 
                params, 
                new SectionMapper());
        Section section = null;
        if(null != sections && !sections.isEmpty()) {
            section = sections.get(0);
        }
        return section;
    }

    @Override
    public Long insertSection(long termId, Section term) throws JsonProcessingException {
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
        params.put(DbConst.GRADE_FORMULA_COL, new ObjectMapper().writeValueAsString(term.getGradeFormula()));
        jdbcTemplate.update(
                INSERT_SECTION_SQL, 
                new MapSqlParameterSource(params), 
                keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Long updateSection(long termId, long sectionId, Section section) throws JsonProcessingException {
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
        params.put(DbConst.GRADE_FORMULA_COL, new ObjectMapper().writeValueAsString(section.getGradeFormula()));
        params.put(DbConst.SECTION_ID_COL, new Long(sectionId));
        jdbcTemplate.update(
                UPDATE_SECTION_SQL, 
                new MapSqlParameterSource(params));
        return sectionId;
    }

    @Override
    public Long deleteSection(long sectionId) {
        Map<String, Object> params = new HashMap<>();
        params.put(DbConst.SECTION_ID_COL, new Long(sectionId));
        jdbcTemplate.update(DELETE_SECTION_SQL, new MapSqlParameterSource(params));
        return sectionId;
    }

}
