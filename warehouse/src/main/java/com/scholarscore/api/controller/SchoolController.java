package com.scholarscore.api.controller;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.api.annotation.StudentAccessible;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.School;
import com.scholarscore.models.gpa.Gpa;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @StudentAccessible
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
    @StudentAccessible
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
            value = "/{schoolId}/gpas",
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    @StudentAccessible(paramName = "id")
    public @ResponseBody ResponseEntity getGpas(
            @ApiParam(name = "schoolId", required = true, value = "The school ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "id", required = true)
            @RequestParam(value="id") Long[] id)
    {
        Map<Long, Double> studentToGpa = new HashMap<>();
        ArrayList<Long> studentIds = new ArrayList<Long>(Arrays.asList(id));
        ServiceResponse<Collection<Gpa>> resp = pm.getGpaManager().getAllGpasForStudents(studentIds, null, null);
        if(null != resp.getValue()) {
            for(Gpa g: resp.getValue()) {
                studentToGpa.put(g.getStudentId(), g.getScore());
            }
            return respond(new ServiceResponse<>(studentToGpa));
        } else {
            return respond(resp);
        }
    }

    @ApiOperation(
            value = "Get all sections in a school",
            notes = "Retrieve all sections school",
            response = List.class)
    @RequestMapping(
            value = "/{schoolId}/sections",
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    @StudentAccessible
    public @ResponseBody ResponseEntity getAllSectionsInSchool(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId) {
        return respond(pm.getSectionManager().getAllSectionsInSchool(schoolId));
    }


    @ApiOperation(
            value = "Sets the student's advisor",
            notes = "Sync the student advisors for each school",
            response = EntityId.class)
    @RequestMapping(
            value = "/{schoolId}/students/advisor",
            method = RequestMethod.POST,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity associateAdvisors(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId
    ) {
        return respond(pm.getSchoolManager().associateAdvisors(schoolId));
    }

    @ApiOperation(
            value = "Get all teachers in school",
            notes = "Retrieve all teachers within a school",
            response = List.class)
    @RequestMapping(
            value = "/{schoolId}/teachers",
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    @StudentAccessible
    public @ResponseBody ResponseEntity getTeachersInSchool(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId) {
        return respond(pm.getTeacherManager().getAllTeachers(schoolId));
    }
}
