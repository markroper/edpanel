package com.scholarscore.api.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.School;
import com.scholarscore.models.WeightedGradable;
import com.scholarscore.util.GradeUtil;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Controller
@RequestMapping(ApiConsts.API_V1_ENDPOINT + "/schools")
public class SchoolController extends BaseController {


    @ApiOperation(
            value = "Get all schools within a district", 
            notes = "Retrieve all schools", 
            response = List.class)
    @RequestMapping(
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getAllSchools() {
        return respond(new ArrayList<>(pm.getSchoolManager().getAllSchools()));
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
    public @ResponseBody ResponseEntity getSchool(
            @ApiParam(name = "schoolId", required = true, value = "The school long ID")
            @PathVariable(value="schoolId") Long schoolId) {
        return respond(pm.getSchoolManager().getSchool(schoolId));
    }

    @ApiOperation(
            value = "Create a school within the district", 
            notes = "Creates, assigns an ID to, persists and returns a school",
            response = EntityId.class)
    @RequestMapping(
            method = RequestMethod.POST, 
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity createSchool(@RequestBody @Valid School school) {
        return respond(pm.getSchoolManager().createSchool(school));
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
    public @ResponseBody ResponseEntity replaceSchool(
            @ApiParam(name = "schoolId", required = true, value = "The school ID")
            @PathVariable(value="schoolId") Long schoolId,
            @RequestBody @Valid School school) {
        return respond(pm.getSchoolManager().replaceSchool(schoolId, school));
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
    public @ResponseBody ResponseEntity updateSchool(
            @ApiParam(name = "schoolId", required = true, value = "The school ID")
            @PathVariable(value="schoolId") Long schoolId,
            @RequestBody @Valid School school) {
        return respond(pm.getSchoolManager().updateSchool(schoolId, school));
    }

    @ApiOperation(
            value = "Delete a school from a district by ID", 
            response = Void.class)
    @RequestMapping(
            value = "/{schoolId}", 
            method = RequestMethod.DELETE, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity deleteSchool(
            @ApiParam(name = "schoolId", required = true, value = "The school ID")
            @PathVariable(value="schoolId") Long schoolId) {
        return respond(pm.getSchoolManager().deleteSchool(schoolId));
    }
    
    @ApiOperation(
            value = "Get a map of studentId to GPA",
            notes = "Get a map of studentId to GPA for a list of provided student IDs",
            response = Map.class)
    @RequestMapping(
            value = "/{schoolId}/gpas/{gpaScale}",
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getGpas(
            @ApiParam(name = "schoolId", required = true, value = "The school ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "gpaScale", required = true)
            @PathVariable(value="gpaScale") Integer gpaScale,
            @ApiParam(name = "id", required = true)
            @RequestParam(value="id") Integer[] id)
    {
        Map<Integer, Double> studentToGpa = new HashMap<>();
        for(int studentId : id) {
            Collection<? extends WeightedGradable> courseGrades =
                    pm.getStudentSectionGradeManager().getSectionGradesForStudent(studentId).getValue();
            studentToGpa.put(studentId, GradeUtil.calculateGPA(gpaScale, courseGrades));
        }
        return respond(studentToGpa);
    }
}
