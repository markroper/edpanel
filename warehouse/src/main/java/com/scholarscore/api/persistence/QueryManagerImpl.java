package com.scholarscore.api.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.scholarscore.api.persistence.mysql.QueryPersistence;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.query.Query;
import com.scholarscore.models.query.QueryResults;

import java.util.Collection;

/**
 * Created by cwallace on 9/16/2015.
 */
public class QueryManagerImpl implements QueryManager {

    private QueryPersistence queryPersistence;

    private PersistenceManager pm;

    private static final String QUERY = "query";

    public void setQueryPersistence(QueryPersistence queryPersistence) {
        this.queryPersistence = queryPersistence;
    }

    public void setPm(PersistenceManager pm) {
        this.pm = pm;
    }

    @Override
    public ServiceResponse<Query> getQuery(Long schoolId, Long queryId) {
        StatusCode code = pm.getSchoolManager().schoolExists(schoolId);
        if(!code.isOK()) {
            return new ServiceResponse<Query>(code);
        }
        Query query =  queryPersistence.selectQuery(schoolId, queryId);
        if(null == query) {
            return new ServiceResponse<Query>(StatusCodes.getStatusCode(
                    StatusCodeType.MODEL_NOT_FOUND,
                    new Object[] { QUERY, queryId }));
        }
        return new ServiceResponse<Query>(query);
    }

    @Override
    public ServiceResponse<Collection<Query>> getQueries(Long schoolId) {
        StatusCode code = pm.getSchoolManager().schoolExists(schoolId);
        if(!code.isOK()) {
            return new ServiceResponse<Collection<Query>>(code);
        }
        return new ServiceResponse<Collection<Query>>(
                queryPersistence.selectQueries(schoolId));
    }

    @Override
    public ServiceResponse<Long> createQuery(Long schoolId, Query query) {
        if(!query.isValid()) {
            return new ServiceResponse<Long>(StatusCodes.getStatusCode(StatusCodeType.INVALID_QUERY));
        }
        StatusCode code = pm.getSchoolManager().schoolExists(schoolId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        try {
            return new ServiceResponse<Long>(
                    queryPersistence.createQuery(schoolId, query));
        } catch (JsonProcessingException e) {
            return new ServiceResponse<Long>(
                    StatusCodes.getStatusCode(StatusCodeType.BAD_REQUEST_CANNOT_PARSE_BODY));
        }
    }

    @Override
    public ServiceResponse<Long> deleteQuery(Long schoolId, Long reportId) {
        StatusCode code = pm.getSchoolManager().schoolExists(schoolId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        return new ServiceResponse<Long>(
                queryPersistence.deleteQuery(schoolId, reportId));
    }

    @Override
    public ServiceResponse<QueryResults> getQueryResults(Long schoolId,
                                                         Long reportId) {
        // TODO: Implement query generation
        return new ServiceResponse<QueryResults>(
                StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND));
    }





    @Override
    public ServiceResponse<QueryResults> getQueryResults(Query query) {
        QueryResults results = queryPersistence.generateQueryResults(query);
        if(null == results) {
            return new ServiceResponse<QueryResults>(StatusCodes.getStatusCode(StatusCodeType.INVALID_QUERY));
        }
        return new ServiceResponse<QueryResults>(results);
    }
}
