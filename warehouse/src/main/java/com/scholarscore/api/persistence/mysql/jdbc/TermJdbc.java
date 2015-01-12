    package com.scholarscore.api.persistence.mysql.jdbc;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.api.persistence.mysql.EntityPersistence;
import com.scholarscore.api.persistence.mysql.mapper.TermMapper;
import com.scholarscore.models.Term;

public class TermJdbc extends BaseJdbc implements EntityPersistence<Term> {
    private static String INSERT_TERM_SQL = "INSERT INTO `"+ 
            DbConst.DATABASE +"`.`" + DbConst.TERM_TABLE + "` " +
            "(" + DbConst.TERM_NAME_COL + ", " + DbConst.SCHOOL_YEAR_FK_COL + ", " + 
            DbConst.TERM_START_DATE_COL + ", " + DbConst.TERM_END_DATE_COL + ")" +
            " VALUES (:" + DbConst.TERM_NAME_COL + ", :" + DbConst.SCHOOL_YEAR_FK_COL + 
            ", :" + DbConst.TERM_START_DATE_COL + ", :" + DbConst.TERM_END_DATE_COL + ")";
    
    private static String UPDATE_TERM_SQL = 
            "UPDATE `" + DbConst.DATABASE + "`.`" + DbConst.TERM_TABLE + "` " + 
            "SET `" + DbConst.TERM_NAME_COL + "`= :" + DbConst.TERM_NAME_COL + ", `" +
            DbConst.SCHOOL_YEAR_FK_COL + "`= :" + DbConst.SCHOOL_YEAR_FK_COL + ", `" +
            DbConst.TERM_START_DATE_COL + "`= :" + DbConst.TERM_START_DATE_COL + ", `" +
            DbConst.TERM_END_DATE_COL + "`= :" + DbConst.TERM_END_DATE_COL + " " +
            "WHERE `" + DbConst.TERM_ID_COL + "`= :" + DbConst.TERM_ID_COL + "";
    
    private static String DELETE_TERM_SQL = "DELETE FROM `"+ 
            DbConst.DATABASE +"`.`" + DbConst.TERM_TABLE + "` " +
            "WHERE `" + DbConst.TERM_ID_COL + "`= :" + DbConst.TERM_ID_COL + "";
    
    private static String SELECT_ALL_TERMS_SQL = "SELECT * FROM `"+ 
            DbConst.DATABASE +"`.`" + DbConst.TERM_TABLE + "` " +
            "WHERE `" + DbConst.SCHOOL_YEAR_FK_COL + "` = :" + DbConst.SCHOOL_YEAR_FK_COL;
    
    private static String SELECT_TERM_SQL = SELECT_ALL_TERMS_SQL + 
            " AND `" + DbConst.TERM_ID_COL + "`= :" + DbConst.TERM_ID_COL;
    
    @Override
    public Collection<Term> selectAll(long schoolYearId) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.SCHOOL_YEAR_FK_COL, new Long(schoolYearId));
        Collection<Term> terms = jdbcTemplate.query(
                SELECT_ALL_TERMS_SQL, 
                params,
                new TermMapper());
        return terms;
    }

    @Override
    public Term select(long schoolYearId, long termId) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.SCHOOL_YEAR_FK_COL, new Long(schoolYearId));
        params.put(DbConst.TERM_ID_COL, new Long(termId));
        
        List<Term> terms = jdbcTemplate.query(
                SELECT_TERM_SQL, 
                params, 
                new TermMapper());
        Term term = null;
        if(null != terms && !terms.isEmpty()) {
            term = terms.get(0);
        }
        return term;
    }

    @Override
    public Long insert(long schoolYearId, Term term) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.TERM_NAME_COL, term.getName());
        params.put(DbConst.SCHOOL_YEAR_FK_COL, new Long(schoolYearId));
        params.put(DbConst.TERM_START_DATE_COL, DbConst.resolveTimestamp(term.getStartDate()));
        params.put(DbConst.TERM_END_DATE_COL, DbConst.resolveTimestamp(term.getEndDate()));
        jdbcTemplate.update(
                INSERT_TERM_SQL, 
                new MapSqlParameterSource(params), 
                keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Long update(long schoolYearId, long termId, Term term) {
        Map<String, Object> params = new HashMap<>();     
        params.put(DbConst.TERM_NAME_COL, term.getName());
        params.put(DbConst.SCHOOL_YEAR_FK_COL, new Long(schoolYearId));
        params.put(DbConst.TERM_START_DATE_COL, DbConst.resolveTimestamp(term.getStartDate()));
        params.put(DbConst.TERM_END_DATE_COL, DbConst.resolveTimestamp(term.getEndDate()));
        params.put(DbConst.TERM_ID_COL, new Long(termId));
        jdbcTemplate.update(
                UPDATE_TERM_SQL, 
                new MapSqlParameterSource(params));
        return termId;
    }

    @Override
    public Long delete(long termId) {
        Map<String, Object> params = new HashMap<>();
        params.put(DbConst.TERM_ID_COL, new Long(termId));
        jdbcTemplate.update(DELETE_TERM_SQL, new MapSqlParameterSource(params));
        return termId;
    }

}