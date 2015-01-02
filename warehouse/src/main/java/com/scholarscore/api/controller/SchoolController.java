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
        return respond(getSchool(schoolId));
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
        return respond(createSchool(school));
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
        return respond(replaceSchool(schoolId, school));
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
        return respond(updateSchool(schoolId, school));
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
        return respond(deleteSchool(schoolId));
    }
}
