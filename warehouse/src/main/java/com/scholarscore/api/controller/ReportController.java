package com.scholarscore.api.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.query.Query;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Validated
@RequestMapping(ApiConsts.API_V1_ENDPOINT + "/schools/{schoolId}/reports")
public class ReportController extends BaseController {
    @ApiOperation(
            value = "Get a report by ID", 
            notes = "Given a report ID, returns the report", 
            response = Query.class)
    @RequestMapping(
            value = "/{reportId}", 
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getReport(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "reportId", required = true, value = "Report ID")
            @PathVariable(value="reportId") Long reportId) {
        return respond(getReportManager().getReport(schoolId, reportId));
    }
    
    @ApiOperation(
            value = "Get all reports for a school", 
            notes = "Returns all saved reports for a school with ID schoolId", 
            response = List.class)
    @RequestMapping( 
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getReport(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId) {
        return respond(getReportManager().getReports(schoolId));
    }

    @ApiOperation(
            value = "Create a report within a school", 
            notes = "Creates, assigns an ID, persists and returns the report ID",
            response = EntityId.class)
    @RequestMapping(
            method = RequestMethod.POST, 
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity createReport(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @RequestBody @Valid Query query) {
        return respond(getReportManager().createReport(schoolId, query));
    }
    
    @ApiOperation(
            value = "Delete a report", 
            response = Void.class)
    @RequestMapping(
            value = "/{reportId}", 
            method = RequestMethod.DELETE, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity deleteCourse(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "reportId", required = true, value = "Report ID")
            @PathVariable(value="reportId") Long reportId) {
        return respond(getReportManager().deleteReport(schoolId, reportId));
    }
    
    @ApiOperation(
            value = "Execute a report and return results", 
            notes = "Given a report ID, returns the report", 
            response = Query.class)
    @RequestMapping(
            value = "/{reportId}/results", 
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getReportResults(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "reportId", required = true, value = "Report ID")
            @PathVariable(value="reportId") Long reportId) {
        return respond(getReportManager().getReportResults(schoolId, reportId));
    }
}
