package com.scholarscore.api.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.scholarscore.api.util.ErrorCodes;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.School;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Controller
@RequestMapping("/api/v1/schools")
public class SchoolController extends BaseController {
    @ApiOperation(
            value = "Get all schools within a district", 
            notes = "Retrieve all schools", 
            response = List.class)
    @RequestMapping(
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getAll() {
        return respond(new ArrayList<>(getAllSchools()));
    }
    
    @ApiOperation(
            value = "Get a school by ID", 
            notes = "Given a school ID, the endpoint returns the school", 
            response = School.class)
    @RequestMapping(
            value = "/{schoolId}", 
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity get(
            @ApiParam(name = "schoolId", required = true, value = "The school long ID")
            @PathVariable(value="schoolId") Long schoolId) {
        if(schoolExists(schoolId)) {
            return respond(getSchool(schoolId));
        }
        return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId });
    }

    @ApiOperation(
            value = "Create a school within the district", 
            notes = "Creates, assigns an ID to, persists and returns a school",
            response = EntityId.class)
    @RequestMapping(
            method = RequestMethod.POST, 
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity create(@RequestBody @Valid School school) {
        return respond(new EntityId(createSchool(school)));
    }

    @ApiOperation(
            value = "Overwrite an existing school within a district", 
            notes = "Overwrites an existing school entity within a district with the ID provided",
            response = EntityId.class)
    @RequestMapping(
            value = "/{schoolId}", 
            method = RequestMethod.PUT, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity replace(
            @ApiParam(name = "schoolId", required = true, value = "The school ID")
            @PathVariable(value="schoolId") Long schoolId,
            @RequestBody @Valid School school) {
        if(null != schoolId && schoolExists(schoolId)) {
            school.setId(schoolId);
            saveSchool(school);
            return respond(new EntityId(schoolId));
        } else {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SCHOOL, schoolId });
        }
    }
    
    @ApiOperation(
            value = "Update an existing school within a district", 
            notes = "Updates an existing school properties. Will not overwrite existing values with null.",
            response = EntityId.class)
    @RequestMapping(
            value = "/{schoolId}", 
            method = RequestMethod.PATCH, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity update(
            @ApiParam(name = "schoolId", required = true, value = "The school ID")
            @PathVariable(value="schoolId") Long schoolId,
            @RequestBody @Valid School school) {
        if(null != school && null != schoolId && schoolExists(schoolId)) {
            school.setId(schoolId);
            school.mergePropertiesIfNull(getSchool(schoolId));
            saveSchool(school);
            return respond(new EntityId(schoolId));
        } else {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SCHOOL, schoolId });
        }
    }

    @ApiOperation(
            value = "Delete a school from a district by ID", 
            response = Void.class)
    @RequestMapping(
            value = "/{schoolId}", 
            method = RequestMethod.DELETE, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity delete(
            @ApiParam(name = "schoolId", required = true, value = "The school ID")
            @PathVariable(value="schoolId") Long schoolId) {
        if(null == schoolId) {
            return respond(ErrorCodes.BAD_REQUEST_CANNOT_PARSE_BODY);
        } else if (!schoolExists(schoolId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId });
        }
        deleteSchool(schoolId);
        return respond((School) null);
    }
}
