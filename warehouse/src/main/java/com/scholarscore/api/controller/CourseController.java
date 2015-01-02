package com.scholarscore.api.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.scholarscore.api.persistence.PersistenceManager;
import com.scholarscore.api.util.ErrorCodes;
import com.scholarscore.models.Course;
import com.scholarscore.models.EntityId;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Controller
@RequestMapping("/api/v1/schools/{schoolId}/courses")
public class CourseController extends BaseController {
    @ApiOperation(
            value = "Get all courses within a school", 
            notes = "Retrieve all courses", 
            response = List.class)
    @RequestMapping(
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getAllCourses(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId) {
        return respond(new ArrayList<>(PersistenceManager.courses.get(schoolId).values()));
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
    public @ResponseBody ResponseEntity getCourse(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "courseId", required = true, value = "Course ID")
            @PathVariable(value="courseId") Long courseId) {
        if(!PM.schoolExists(schoolId).equals(ErrorCodes.OK)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { PersistenceManager.SCHOOL, schoolId });
        }
        if(!PersistenceManager.courses.containsKey(schoolId) || !PersistenceManager.courses.get(schoolId).containsKey(courseId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { PersistenceManager.COURSE, courseId });
        } else {
            return respond(PersistenceManager.courses.get(schoolId).get(courseId));
        }
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
        if(!PM.schoolExists(schoolId).equals(ErrorCodes.OK)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { PersistenceManager.SCHOOL, schoolId });
        }
        course.setId(PersistenceManager.courseCounter.incrementAndGet());
        if(!PersistenceManager.courses.containsKey(schoolId)) {
            PersistenceManager.courses.put(schoolId, new HashMap<Long, Course>());
        }
        PersistenceManager.courses.get(schoolId).put(course.getId(), course);
        return respond(new EntityId(course.getId()));
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
        if(!PM.schoolExists(schoolId).equals(ErrorCodes.OK)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { PersistenceManager.SCHOOL, schoolId });
        }
        if(null != courseId && PersistenceManager.courses.containsKey(schoolId) && PersistenceManager.courses.get(schoolId).containsKey(courseId)) {
            PersistenceManager.courses.get(schoolId).put(courseId, course);
            return respond(new EntityId(courseId));
        } else {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ PersistenceManager.COURSE, courseId });
        }
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
        if(null == schoolId || !PM.schoolExists(schoolId).equals(ErrorCodes.OK)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ PersistenceManager.SCHOOL, schoolId });
        }
        if(null != course && null != courseId && PersistenceManager.courses.containsKey(schoolId) 
                && PersistenceManager.courses.get(schoolId).containsKey(courseId)) {
            course.mergePropertiesIfNull(PersistenceManager.courses.get(schoolId).get(courseId));
            PersistenceManager.courses.get(schoolId).put(courseId, course);
            return respond(new EntityId(courseId));
        } else {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ PersistenceManager.COURSE, courseId });
        }
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
        if(null == schoolId || !PM.schoolExists(schoolId).equals(ErrorCodes.OK)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ PersistenceManager.SCHOOL, schoolId });
        }
        if(null == courseId || !PersistenceManager.courses.containsKey(schoolId) 
                || !PersistenceManager.courses.get(schoolId).containsKey(courseId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ PersistenceManager.COURSE, courseId });
        }
        PersistenceManager.courses.get(schoolId).remove(courseId);
        return respond((Course) null);
    }
}
