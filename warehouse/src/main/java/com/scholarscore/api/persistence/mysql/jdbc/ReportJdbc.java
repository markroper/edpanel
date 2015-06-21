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
import com.scholarscore.api.persistence.mysql.ReportPersistence;
import com.scholarscore.api.persistence.mysql.mapper.ReportMapper;
import com.scholarscore.models.query.Query;
import com.scholarscore.models.query.QueryResults;

public class ReportJdbc extends BaseJdbc implements ReportPersistence {
    private static String INSERT_REPORT_SQL = "INSERT INTO `"+ 
            DbConst.DATABASE +"`.`" + DbConst.REPORT_TABLE + "` " +
            "(" + DbConst.SCHOOL_FK_COL + ", " + DbConst.REPORT_ID_COL + ", " + DbConst.REPORT_COL + ")" +
            " VALUES (:schoolfk, :reportid, :report)";   
    
    private static String DELETE_REPORT_SQL = "DELETE FROM `"+ 
            DbConst.DATABASE +"`.`" + DbConst.REPORT_TABLE + "` " +
            "WHERE `" + DbConst.SCHOOL_FK_COL + "`= :schoolfk AND " + 
            "`" + DbConst.REPORT_ID_COL + "`= :reportid";
    
    private static String SELECT_REPORTS_IN_SCHOOL_SQL = "SELECT * FROM `"+ 
            DbConst.DATABASE +"`.`" + DbConst.REPORT_TABLE + "`"
                    + " WHERE `" + DbConst.SCHOOL_FK_COL + "` = schoolfk";
    
    private static String SELECT_REPORT_SQL = SELECT_REPORTS_IN_SCHOOL_SQL + 
            " AND `" + DbConst.REPORT_ID_COL + "`= :reportid";
    
    @Override
    public Query selectReport(Long schoolId, Long reportId) {
        Map<String, Object> params = new HashMap<>();     
        params.put("reportid", new Long(reportId));
        params.put("schoolfk", new Long(schoolId));
        List<Query> queries = jdbcTemplate.query(
                SELECT_REPORT_SQL, 
                params, 
                new ReportMapper());
        Query query = null;
        if(null != queries && !queries.isEmpty()) {
            query = queries.get(0);
        }
        return query;
    }
    
    @Override
    public Collection<Query> selectReports(Long schoolId) {
        Map<String, Object> params = new HashMap<>();     
        params.put("schoolfk", new Long(schoolId));
        List<Query> queries = jdbcTemplate.query(
                SELECT_REPORTS_IN_SCHOOL_SQL, 
                params, 
                new ReportMapper());
        return queries;
    }

    @Override
    public Long createReport(Long schoolId, Query query) 
            throws JsonProcessingException {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> params = new HashMap<>();     
        params.put("schoolfk", schoolId);
        params.put("report", mapper.writeValueAsString(query));
        jdbcTemplate.update(INSERT_REPORT_SQL, new MapSqlParameterSource(params), keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Long deleteReport(Long schoolId, Long reportId) {
        Map<String, Object> params = new HashMap<>();
        params.put("schoolfk", schoolId);
        params.put("reportid", reportId);
        jdbcTemplate.update(DELETE_REPORT_SQL, new MapSqlParameterSource(params));
        return reportId;
    }

    @Override
    public QueryResults generateReportResults(Long schoolId, Long reportId) {
        // TODO: Implement query generator
        return null;
    }

}
