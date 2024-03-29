package com.scholarscore.api.controller;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.api.annotation.StudentAccessible;
import com.scholarscore.models.Course;
import com.scholarscore.models.EntityId;
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
@RequestMapping(ApiConsts.API_V1_ENDPOINT + "/schools/{schoolId}/courses")
public class CourseController extends BaseController {


    @ApiOperation(
            value = "Get all courses within a school", 
            notes = "Retrieve all courses", 
            response = List.class)
    @RequestMapping(
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    @StudentAccessible
    public @ResponseBody ResponseEntity getAllCourses(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId) {
        return respond(pm.getCourseManager().getAllCourses(schoolId));
    }
    
    @ApiOperation(
            value = "Get a course by ID", 
            notes = "Given a course ID, returns the course", 
            response = Course.class)
    @RequestMapping(
            value = "/{courseId}", 
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    @StudentAccessible
    public @ResponseBody ResponseEntity getCourse(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "courseId", required = true, value = "Course ID")
            @PathVariable(value="courseId") Long courseId) {
        return respond(pm.getCourseManager().getCourse(schoolId, courseId));
    }

    @ApiOperation(
            value = "Create a course within a school", 
            notes = "Creates, assigns an ID, persists and returns the course ID",
            response = EntityId.class)
    @RequestMapping(
            method = RequestMethod.POST, 
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity createCourse(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @RequestBody @Valid Course course) {
        return respond(pm.getCourseManager().createCourse(schoolId, course));
    }

    @ApiOperation(
            value = "Overwrite an existing course", 
            notes = "Overwrites an existing course with the ID provided within a school",
            response = EntityId.class)
    @RequestMapping(
            value = "/{courseId}", 
            method = RequestMethod.PUT, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity replaceCourse(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "courseId", required = true, value = "Course ID")
            @PathVariable(value="courseId") Long courseId,
            @RequestBody @Valid Course course) {
        return respond(pm.getCourseManager().replaceCourse(schoolId, courseId, course));
    }
    
    @ApiOperation(
            value = "Update an existing course", 
            notes = "Updates an existing course. Will not overwrite existing values with null.",
            response = EntityId.class)
    @RequestMapping(
            value = "/{courseId}", 
            method = RequestMethod.PATCH, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity updateCourse(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "courseId", required = true, value = "Course ID")
            @PathVariable(value="courseId") Long courseId,
            @RequestBody @Valid Course course) {
        return respond(pm.getCourseManager().updateCourse(schoolId, courseId, course));
    }

    @ApiOperation(
            value = "Delete a course from a school", 
            response = Void.class)
    @RequestMapping(
            value = "/{courseId}", 
            method = RequestMethod.DELETE, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity deleteCourse(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "courseId", required = true, value = "Course ID")
            @PathVariable(value="courseId") Long courseId) {
        return respond(pm.getCourseManager().deleteCourse(schoolId, courseId));
    }
}
