package com.scholarscore.api.controller;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.api.annotation.StudentAccessible;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.query.Query;
import com.scholarscore.models.query.QueryComponents;
import com.scholarscore.models.query.QueryResults;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO: add owning user to Query and control for that based on requesting user
 */
@Validated
@Controller
@RequestMapping(ApiConsts.API_V1_ENDPOINT + "/schools/{schoolId}/queries")
public class QueryController extends BaseController {

    @ApiOperation(
            value = "Get a query by ID", 
            notes = "Given a query ID, returns the query", 
            response = Query.class)
    @RequestMapping(
            value = "/{queryId}", 
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    @StudentAccessible
    public @ResponseBody ResponseEntity getQuery(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "queryId", required = true, value = "Query ID")
            @PathVariable(value="queryId") Long queryId) {
        return respond(pm.getQueryManager().getQuery(schoolId, queryId));
    }
    
    @ApiOperation(
            value = "Get all queries for a school", 
            notes = "Returns all saved querys for a school with ID schoolId", 
            response = List.class)
    @RequestMapping( 
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getQuery(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId) {
        return respond(pm.getQueryManager().getQueries(schoolId));
    }

    @ApiOperation(
            value = "Create a query within a school", 
            notes = "Creates, assigns an ID, persists and returns the query ID",
            response = EntityId.class)
    @RequestMapping(
            method = RequestMethod.POST, 
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    @StudentAccessible
    public @ResponseBody ResponseEntity createQuery(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @RequestBody @Valid Query query) {
        return respond(pm.getQueryManager().createQuery(schoolId, query));
    }
    
    @ApiOperation(
            value = "Delete a query", 
            response = Void.class)
    @RequestMapping(
            value = "/{queryId}", 
            method = RequestMethod.DELETE, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    @StudentAccessible
    public @ResponseBody ResponseEntity deleteQuery(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "queryId", required = true, value = "Report ID")
            @PathVariable(value="queryId") Long queryId) {
        return respond(pm.getQueryManager().deleteQuery(schoolId, queryId));
    }
    
    @ApiOperation(
            value = "Execute a query and return results", 
            notes = "Given a query ID, returns the query", 
            response = QueryResults.class)
    @RequestMapping(
            value = "/{queryId}/results", 
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    @StudentAccessible
    public @ResponseBody ResponseEntity getQueryResults(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "queryId", required = true, value = "Query ID")
            @PathVariable(value="queryId") Long queryId) {
        //TODO: implement this
        return respond(pm.getQueryManager().getQueryResults(schoolId, queryId));
    }
    
    @ApiOperation(
            value = "Execute a query and return results", 
            notes = "Given a query object, executes the query returns the query", 
            response = QueryResults.class)
    @RequestMapping(
            value = "/results", 
            method = RequestMethod.POST, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    @StudentAccessible
    public @ResponseBody ResponseEntity generateQueryResults(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @RequestBody @Valid Query query) {
        return respond(pm.getQueryManager().getQueryResults(query));
    }
    
    @ApiOperation(
            value = "Get valid query components", 
            notes = "Returns a data structure expressing all eligible Dimensions and Measures from which queries can be built", 
            response = QueryComponents.class)
    @RequestMapping(
            value = "/components", 
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    @StudentAccessible
    public @ResponseBody ResponseEntity getQueryComponents(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId) {
        return respond(new ServiceResponse<QueryComponents>(new QueryComponents()));
    }
}
