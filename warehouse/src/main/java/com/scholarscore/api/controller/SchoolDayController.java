package com.scholarscore.api.controller;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.attendance.SchoolDay;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(ApiConsts.API_V1_ENDPOINT + "/schools/{schoolId}/days")
public class SchoolDayController extends BaseController {
    @ApiOperation(
            value = "Get all school days", 
            notes = "Retrieve all school days within a school", 
            response = List.class)
    @RequestMapping(
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getAll(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId) {
        return respond(pm.getSchoolDayManager().getAllDays(schoolId));
    }
    
    @ApiOperation(
            value = "Get all school days in a school year", 
            notes = "Retrieve all school days within a school year", 
            response = List.class)
    @RequestMapping(
            value = "/years/{schoolYearId}", 
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getAllDaysInYear(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "schoolYearId", required = true, value = "School year ID")
            @PathVariable(value="schoolYearId") Long schoolYearId) {
        return respond(pm.getSchoolDayManager().getAllDaysInYear(schoolId, schoolYearId));
    }
    
    @ApiOperation(
            value = "Get a school day by ID", 
            notes = "Retrieve a school day by its ID", 
            response = SchoolDay.class)
    @RequestMapping(
            value = "/{schoolDayId}", 
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getASchoolDay(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "schoolDayId", required = true, value = "School day ID")
            @PathVariable(value="schoolDayId") Long schoolDayId) {
        return respond(pm.getSchoolDayManager().getDay(schoolId, schoolDayId));
    }
    
    @ApiOperation(
            value = "Create a school day", 
            notes = "Creates, assigns an ID, persists and returns a school day ID",
            response = EntityId.class)
    @RequestMapping(
            method = RequestMethod.POST, 
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity createSchoolDay(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @RequestBody @Valid SchoolDay schoolDay) {
        return respond(pm.getSchoolDayManager().createSchoolDay(schoolId, schoolDay));
    }
    
    @ApiOperation(
            value = "Delete a school day", 
            response = Void.class)
    @RequestMapping(
            value = "/{schoolDayId}", 
            method = RequestMethod.DELETE, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity deleteSchoolYear(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "schoolDayId", required = true, value = "School day ID")
            @PathVariable(value="schoolDayId") Long schoolDayId) {
        return respond(pm.getSchoolDayManager().deleteSchoolDay(schoolId, schoolDayId));
    }
}
