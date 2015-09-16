package com.scholarscore.api.controller;

import java.util.List;

import javax.validation.Valid;

import com.scholarscore.api.persistence.TeacherManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.Teacher;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Controller
@RequestMapping(ApiConsts.API_V1_ENDPOINT + "/teachers")
public class TeacherController extends BaseController {

    @Autowired
    TeacherManager teacherManager;

    @ApiOperation(
            value = "Get all teachers", 
            notes = "Retrieve all teachers within a district", 
            response = List.class)
    @RequestMapping(
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getAll() {
        return respond(teacherManager.getAllTeachers());
    }
    
    @ApiOperation(
            value = "Get a teacher by ID", 
            notes = "Given a teacher ID, the endpoint returns the teacher", 
            response = Teacher.class)
    @RequestMapping(
            value = "/{teacherId}", 
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity get(
            @ApiParam(name = "teacherId", required = true, value = "Teacher ID")
            @PathVariable(value="teacherId") Long teacherId) {
        return respond(teacherManager.getTeacher(teacherId));
    }

    @ApiOperation(
            value = "Create a teacher", 
            notes = "Creates, assigns an ID to, persists and returns a teacher",
            response = EntityId.class)
    @RequestMapping(
            method = RequestMethod.POST, 
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity create(@RequestBody @Valid Teacher teacher) {
        return respond(teacherManager.createTeacher(teacher));
    }

    @ApiOperation(
            value = "Overwrite an existing teacher", 
            notes = "Overwrites an existing teacher entity within a district with the ID provided",
            response = EntityId.class)
    @RequestMapping(
            value = "/{teacherId}", 
            method = RequestMethod.PUT, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity replace(
            @ApiParam(name = "teacherId", required = true, value = "Teacher ID")
            @PathVariable(value="teacherId") Long teacherId,
            @RequestBody @Valid Teacher teacher) {
        return respond(teacherManager.replaceTeacher(teacherId, teacher));
    }
    
    @ApiOperation(
            value = "Update an existing teacher", 
            notes = "Updates an existing teacher properties. Will not overwrite existing values with null.",
            response = EntityId.class)
    @RequestMapping(
            value = "/{teacherId}", 
            method = RequestMethod.PATCH, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity update(
            @ApiParam(name = "teacherId", required = true, value = "Teacher ID")
            @PathVariable(value="teacherId") Long teacherId,
            @RequestBody @Valid Teacher teacher) {
        return respond(teacherManager.updateTeacher(teacherId, teacher));
    }

    @ApiOperation(
            value = "Delete a teacher from a district by ID", 
            response = Void.class)
    @RequestMapping(
            value = "/{teacherId}", 
            method = RequestMethod.DELETE, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity delete(
            @ApiParam(name = "teacherId", required = true, value = "Teacher ID")
            @PathVariable(value="teacherId") Long teacherId) {
        return respond(teacherManager.deleteTeacher(teacherId));
    }
}
