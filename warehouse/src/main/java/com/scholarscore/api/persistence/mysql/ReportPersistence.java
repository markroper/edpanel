package com.scholarscore.api.persistence.mysql;

import java.util.Collection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.scholarscore.models.query.Query;
import com.scholarscore.models.query.QueryResults;

public interface ReportPersistence {
    /**
     * Retrieves the definition for the school with ID schoolId and report ID reportId.
     * @param schoolId
     * @param reportId
     * @return
     */
    public Query selectReport(Long schoolId, Long reportId);
    
    /**
     * Persists a report definition for the school with schoolId and the query schema as provided.
     * @param schoolId
     * @param query
     * @return
     * @throws JsonProcessingException 
     */
    public Long createReport(Long schoolId, Query query) throws JsonProcessingException;
    
    /**
     * Deletes an existing report in the school with schoolId and the report with reportId.
     * @param schoolId
     * @param reportId
     * @return
     */
    public Long deleteReport(Long schoolId, Long reportId);
    
    /**
     * Executes a saves report schema and returns the results.
     * 
     * @param schoolId
     * @param reportId
     * @return
     */
    public QueryResults generateReportResults(Long schoolId, Long reportId);

    /**
     * Return all saved reports within a school
     * @param schoolId
     * @return
     */
    Collection<Query> selectReports(Long schoolId);
}
