package com.scholarscore.api.persistence;

import java.util.Collection;
import java.util.List;

import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.query.Query;
import com.scholarscore.models.query.QueryResults;

public interface ReportManager {
    /**
     * Retrieves the definition for the school with ID schoolId and report ID reportId.
     * @param schoolId
     * @param reportId
     * @return
     */
    public ServiceResponse<Query> getReport(Long schoolId, Long reportId);
    
    /**
     * Persists a report definition for the school with schoolId and the query schema as provided.
     * @param schoolId
     * @param query
     * @return
     */
    public ServiceResponse<Long> createReport(Long schoolId, Query query);
    
    /**
     * Deletes an existing report in the school with schoolId and the report with reportId.
     * @param schoolId
     * @param reportId
     * @return
     */
    public ServiceResponse<Long> deleteReport(Long schoolId, Long reportId);
    
    /**
     * Executes a saves report schema and returns the results.
     * 
     * @param schoolId
     * @param reportId
     * @return
     */
    public ServiceResponse<QueryResults> getReportResults(Long schoolId, Long reportId);

    /**
     * Returns all saved reports for the school with ID schoolId.
     * @param schoolId
     * @return
     */
    public ServiceResponse<Collection<Query>> getReports(Long schoolId);

}
