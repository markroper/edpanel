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
import com.scholarscore.api.persistence.DbMappings;
import com.scholarscore.api.persistence.QueryPersistence;
import com.scholarscore.api.persistence.mysql.mapper.QueryMapper;
import com.scholarscore.api.persistence.mysql.mapper.QueryResultsMapper;
import com.scholarscore.api.persistence.mysql.querygenerator.QuerySqlGenerator;
import com.scholarscore.api.persistence.mysql.querygenerator.SqlGenerationException;
import com.scholarscore.api.persistence.mysql.querygenerator.SqlWithParameters;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.query.Query;
import com.scholarscore.models.query.QueryResults;
import com.scholarscore.models.query.Record;

public class QueryJdbc extends BaseJdbc implements QueryPersistence {
    private static String INSERT_REPORT_SQL = "INSERT INTO `"+ 
            DbMappings.DATABASE +"`.`" + DbMappings.REPORT_TABLE + "` " +
            "(" + HibernateConsts.SCHOOL_FK + ", " + DbMappings.REPORT_COL + ")" +
            " VALUES (:schoolfk, :report)";   
    
    private static String DELETE_REPORT_SQL = "DELETE FROM `"+ 
            DbMappings.DATABASE +"`.`" + DbMappings.REPORT_TABLE + "` " +
            "WHERE `" + HibernateConsts.SCHOOL_FK + "`= :schoolfk AND " + 
            "`" + DbMappings.REPORT_ID_COL + "`= :reportid";
    
    private static String SELECT_REPORTS_IN_SCHOOL_SQL = "SELECT * FROM `"+ 
            DbMappings.DATABASE +"`.`" + DbMappings.REPORT_TABLE + "`"
                    + " WHERE `" + HibernateConsts.SCHOOL_FK + "` = :schoolfk";
    
    private static String SELECT_REPORT_SQL = SELECT_REPORTS_IN_SCHOOL_SQL + 
            " AND `" + DbMappings.REPORT_ID_COL + "`= :reportid";
    
    @Override
    public Query selectQuery(Long schoolId, Long reportId) {
        Map<String, Object> params = new HashMap<>();     
        params.put("reportid", new Long(reportId));
        params.put("schoolfk", new Long(schoolId));
        List<Query> queries = jdbcTemplate.query(
                SELECT_REPORT_SQL, 
                params, 
                new QueryMapper());
        Query query = null;
        if(null != queries && !queries.isEmpty()) {
            query = queries.get(0);
        }
        return query;
    }
    
    @Override
    public Collection<Query> selectQueries(Long schoolId) {
        Map<String, Object> params = new HashMap<>();     
        params.put("schoolfk", new Long(schoolId));
        List<Query> queries = jdbcTemplate.query(
                SELECT_REPORTS_IN_SCHOOL_SQL, 
                params, 
                new QueryMapper());
        return queries;
    }

    @Override
    public Long createQuery(Long schoolId, Query query) 
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
    public Long deleteQuery(Long schoolId, Long reportId) {
        Map<String, Object> params = new HashMap<>();
        params.put("schoolfk", schoolId);
        params.put("reportid", reportId);
        jdbcTemplate.update(DELETE_REPORT_SQL, new MapSqlParameterSource(params));
        return reportId;
    }

    @Override
    public QueryResults generateQueryResults(Long schoolId, Long reportId) {
        // TODO: Implement query generator
        return null;
    }

    @Override
    public QueryResults generateQueryResults(Query query) {
        SqlWithParameters sqlQuery = null;
        try {
            sqlQuery = QuerySqlGenerator.generate(query);
        } catch(SqlGenerationException e) {
            return null;
        }
        List<Record> results = jdbcTemplate.query(
                sqlQuery.getSql(), 
                sqlQuery.getParams(), 
                new QueryResultsMapper());
        return new QueryResults(results);
    }

}
