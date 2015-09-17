package com.scholarscore.api.controller;

import java.util.List;

import javax.validation.Valid;

import com.scholarscore.api.persistence.SchoolYearManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.EntityId;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Controller
@RequestMapping(ApiConsts.API_V1_ENDPOINT + "/schools/{schoolId}/years")
public class SchoolYearController extends BaseController {

    @ApiOperation(
            value = "Get all school years", 
            notes = "Retrieve all school years", 
            response = List.class)
    @RequestMapping(
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getAllSchoolYears(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId) {
        return respond(pm.getSchoolYearManager().getAllSchoolYears(schoolId));
    }
    
    @ApiOperation(
            value = "Get a school year by ID", 
            notes = "Given a school year ID, the endpoint returns the school year", 
            response = SchoolYear.class)
    @RequestMapping(
            value = "/{schoolYearId}", 
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getSchoolYear(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "schoolYearId", required = true, value = "School year ID")
            @PathVariable(value="schoolYearId") Long schoolYearId) {
        return respond(pm.getSchoolYearManager().getSchoolYear(schoolId, schoolYearId));
    }

    @ApiOperation(
            value = "Create a school year", 
            notes = "Creates, assigns an ID, persists and returns a school year ID",
            response = EntityId.class)
    @RequestMapping(
            method = RequestMethod.POST, 
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity createSchoolYear(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @RequestBody @Valid SchoolYear schoolYear) {
        return respond(pm.getSchoolYearManager().createSchoolYear(schoolId, schoolYear));
    }

    @ApiOperation(
            value = "Overwrite an existing school year", 
            notes = "Overwrites an existing school year entity within a school with the ID provided",
            response = EntityId.class)
    @RequestMapping(
            value = "/{schoolYearId}", 
            method = RequestMethod.PUT, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity replaceSchoolYear(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "schoolYearId", required = true, value = "School year ID")
            @PathVariable(value="schoolYearId") Long schoolYearId,
            @RequestBody @Valid SchoolYear schoolYear) {
        return respond(pm.getSchoolYearManager().replaceSchoolYear(schoolId, schoolYearId, schoolYear));
    }
    
    @ApiOperation(
            value = "Update an existing school year", 
            notes = "Updates an existing school year properties. Will not overwrite existing values with null.",
            response = EntityId.class)
    @RequestMapping(
            value = "/{schoolYearId}", 
            method = RequestMethod.PATCH, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity updateSchoolYear(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "schoolYearId", required = true, value = "School year ID")
            @PathVariable(value="schoolYearId") Long schoolYearId,
            @RequestBody @Valid SchoolYear schoolYear) {
        return respond(pm.getSchoolYearManager().updateSchoolYear(schoolId, schoolYearId, schoolYear));
    }

    @ApiOperation(
            value = "Delete a school year", 
            response = Void.class)
    @RequestMapping(
            value = "/{schoolYearId}", 
            method = RequestMethod.DELETE, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity deleteSchoolYear(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "schoolYearId", required = true, value = "School year ID")
            @PathVariable(value="schoolYearId") Long schoolYearId) {
        return respond(pm.getSchoolYearManager().deleteSchoolYear(schoolId, schoolYearId));
    }
}
