package com.scholarscore.api.persistence;

import java.util.Collection;

import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.query.Query;
import com.scholarscore.models.query.QueryResults;

public interface QueryManager {
    /**
     * Retrieves the definition for the school with ID schoolId and report ID reportId.
     * @param schoolId
     * @param reportId
     * @return
     */
    public ServiceResponse<Query> getQuery(Long schoolId, Long queryId);
    
    /**
     * Persists a report definition for the school with schoolId and the query schema as provided.
     * @param schoolId
     * @param query
     * @return
     */
    public ServiceResponse<Long> createQuery(Long schoolId, Query query);
    
    /**
     * Deletes an existing report in the school with schoolId and the report with reportId.
     * @param schoolId
     * @param reportId
     * @return
     */
    public ServiceResponse<Long> deleteQuery(Long schoolId, Long queryId);
    
    /**
     * Executes a saves report schema and returns the results.
     * 
     * @param schoolId
     * @param reportId
     * @return
     */
    public ServiceResponse<QueryResults> getQueryResults(Long schoolId, Long queryId);

    /**
     * Returns all saved reports for the school with ID schoolId.
     * @param schoolId
     * @return
     */
    public ServiceResponse<Collection<Query>> getQueries(Long schoolId);
    
    public ServiceResponse<QueryResults> getQueryResults(Query query);

}
