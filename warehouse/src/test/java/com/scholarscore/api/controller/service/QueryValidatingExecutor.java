package com.scholarscore.api.controller.service;

import java.util.ArrayList;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.testng.Assert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.query.Query;

public class QueryValidatingExecutor {
    private final IntegrationBase serviceBase;
    
    public QueryValidatingExecutor(IntegrationBase sb) {
        this.serviceBase = sb;
    }

    public Query get(Long schoolId, Long queryId, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getQueryEndpoint(schoolId, queryId),
                null);
        Query query = serviceBase.validateResponse(response, new TypeReference<Query>(){});
        Assert.assertNotNull(query, "Unexpected null query returned for case: " + msg);
        
        return query;
    }
    
    public void getAll(Long schoolId, String msg, int numberOfItems) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getQueryEndpoint(schoolId), 
                null);
        ArrayList<Query> querys = serviceBase.validateResponse(response, new TypeReference<ArrayList<Query>>(){});
        Assert.assertNotNull(querys, "Unexpected null query returned for case: " + msg);
        Assert.assertEquals(querys.size(), numberOfItems, "Unexpected number of items returned for case: " + msg);
    }
    
    public void getNegative(Long schoolId, Long queryId, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getQueryEndpoint(schoolId, queryId),
                null);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(), 
                "Unexpected status code returned while retreiving query: " + msg);
    }
    
    public Query create(Long schoolId, Query query, String msg) {
        //Create the query
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getQueryEndpoint(schoolId), null, query);
        EntityId returnedQueryId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(returnedQueryId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedQuery(schoolId, returnedQueryId.getId(), query, HttpMethod.POST, msg);
    }
    
    public void createNegative(Long schoolId, Query query, HttpStatus expectedCode, String msg) {
        //Attempt to create the query
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getQueryEndpoint(schoolId), null, query);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(), 
                "Unexpected status code returned for case: " + msg);
    }
    
    public void delete(Long schoolId, Long queryId, String msg) {
        //Delete the query
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE, 
                serviceBase.getQueryEndpoint(schoolId, queryId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), HttpStatus.OK.value(),
                "Non 200 HttpStatus returned on delete for case: " + msg);
        getNegative(schoolId, queryId, HttpStatus.NOT_FOUND, msg);
    }
    
    public void deleteNegative(Long schoolId, Long queryId, HttpStatus expectedCode, String msg) {
        //Attempt to delete the query
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE, 
                serviceBase.getQueryEndpoint(schoolId, queryId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for DELETE: " + msg);
    }

    public Query replace(Long schoolId, Long queryId, Query query, String msg) {
        //Create the query
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT, 
                serviceBase.getQueryEndpoint(schoolId, queryId), 
                null, 
                query);
        EntityId returnedQueryId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(returnedQueryId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedQuery(schoolId, queryId, query, HttpMethod.PUT, msg);
    }

    public void replaceNegative(Long schoolId, Long queryId, Query query, HttpStatus expectedCode, String msg) {
        //Create the query
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT, 
                serviceBase.getQueryEndpoint(schoolId, queryId), 
                null, 
                query);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for PUT: " + msg);
    }

    public Query update(Long schoolId, Long queryId, Query query, String msg) {
      //Create the query
        ResultActions response = serviceBase.makeRequest(HttpMethod.PATCH, 
                serviceBase.getQueryEndpoint(schoolId, queryId), 
                null, 
                query);
        EntityId returnedQueryId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(returnedQueryId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedQuery(schoolId, returnedQueryId.getId(), query, HttpMethod.PATCH, msg);
    }

    public void updateNegative(Long schoolId, Long queryId, Query query, HttpStatus expectedCode, String msg) {
      //Create the query
        ResultActions response = serviceBase.makeRequest(HttpMethod.PATCH, 
                serviceBase.getQueryEndpoint(schoolId, queryId), 
                null, 
                query);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for PUT: " + msg);
    }

    public IntegrationBase getServiceBase() {
        return serviceBase;
    }
    
    /**
     * Given an ID and the value submitted to a GET/PUT endpoint, retrieve the query via GET, validate it, and 
     * return it to the caller.
     * 
     * @param queryId
     * @param submittedQuery
     * @param msg
     * @return
     */
    protected Query retrieveAndValidateCreatedQuery( Long schoolId, Long queryId, Query submittedQuery, HttpMethod method, String msg) {
        //Retrieve and validate the created query
        Query createdQuery = this.get(schoolId, queryId, msg);
        Query expectedQuery = generateExpectationQuery(submittedQuery, createdQuery, method);
        Assert.assertEquals(createdQuery, expectedQuery, "Unexpected query created for case: " + msg);
        
        return createdQuery;
    }
    /**
     * Given a submitted query object and an query instance returned by the API after creation,
     * this method returns a new Query instance that represents the expected state of the submitted 
     * Query after creation.  The reason that there are differences in the submitted and expected 
     * instances is that there may be system assigned values not in the initially submitted object, for 
     * example, the id property.
     * 
     * @param submitted
     * @param created
     * @return
     */
    protected Query generateExpectationQuery(Query submitted, Query created, HttpMethod method) {
        Query returnQuery = new Query(submitted);
        
        if(method == HttpMethod.PATCH) {
            returnQuery.mergePropertiesIfNull(created);
        } else if(null != returnQuery && null == returnQuery.getId()) {
            returnQuery.setId(created.getId());
        }
        return returnQuery;
    }
}
