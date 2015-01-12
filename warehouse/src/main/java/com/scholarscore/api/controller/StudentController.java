package com.scholarscore.api.controller;

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
import com.scholarscore.models.Student;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Controller
@RequestMapping("/api/v1/students")
public class StudentController extends BaseController {
    @ApiOperation(
            value = "Get all students", 
            notes = "Retrieve all students within a district", 
            response = List.class)
    @RequestMapping(
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getAll() {
        return respond(PM.getAllStudents());
    }
    
    @ApiOperation(
            value = "Get a student by ID", 
            notes = "Given a student ID, the endpoint returns the student", 
            response = Student.class)
    @RequestMapping(
            value = "/{studentId}", 
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity get(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId) {
        return respond(PM.getStudent(studentId));
    }

    @ApiOperation(
            value = "Create a student", 
            notes = "Creates, assigns an ID to, persists and returns a student",
            response = EntityId.class)
    @RequestMapping(
            method = RequestMethod.POST, 
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity create(@RequestBody @Valid Student student) {
        return respond(PM.createStudent(student));
    }

    @ApiOperation(
            value = "Overwrite an existing student", 
            notes = "Overwrites an existing student entity within a district with the ID provided",
            response = EntityId.class)
    @RequestMapping(
            value = "/{studentId}", 
            method = RequestMethod.PUT, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity replace(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @RequestBody @Valid Student student) {
        return respond(PM.replaceStudent(studentId, student));
    }
    
    @ApiOperation(
            value = "Update an existing student", 
            notes = "Updates an existing student properties. Will not overwrite existing values with null.",
            response = EntityId.class)
    @RequestMapping(
            value = "/{studentId}", 
            method = RequestMethod.PATCH, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity update(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @RequestBody @Valid Student student) {
        return respond(PM.updateStudent(studentId, student));
    }

    @ApiOperation(
            value = "Delete a student from a district by ID", 
            response = Void.class)
    @RequestMapping(
            value = "/{studentId}", 
            method = RequestMethod.DELETE, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity delete(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId) {
        return respond(PM.deleteStudent(studentId));
    }
}