package com.scholarscore.api.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.scholarscore.models.query.Query;
import com.scholarscore.models.query.QueryResults;

import java.util.Collection;

public interface QueryPersistence {
    /**
     * Retrieves the definition for the school with ID schoolId and query ID queryId.
     * @param schoolId
     * @param queryId
     * @return
     */
    public Query selectQuery(Long schoolId, Long queryId);
    
    /**
     * Persists a query definition for the school with schoolId and the query schema as provided.
     * @param schoolId
     * @param query
     * @return
     * @throws JsonProcessingException 
     */
    public Long createQuery(Long schoolId, Query query) throws JsonProcessingException;
    
    /**
     * Deletes an existing query in the school with schoolId and the query with queryId.
     * @param schoolId
     * @param queryId
     * @return
     */
    public Long deleteQuery(Long schoolId, Long queryId);
    
    /**
     * Executes a saved query schema and returns the results.
     * 
     * @param schoolId
     * @param queryId
     * @return
     */
    public QueryResults generateQueryResults(Long schoolId, Long queryId);
    
    /**
     * Executes query and returns the results.
     * 
     * @param query
     * @return
     */
    public QueryResults generateQueryResults(Query query);

    /**
     * Return all saved queries within a school
     * @param schoolId
     * @return
     */
    Collection<Query> selectQueries(Long schoolId);
}
