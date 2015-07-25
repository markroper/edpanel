package com.scholarscore.api.controller;

import java.util.List;

import javax.validation.Valid;

import com.scholarscore.models.Administrator;
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
@RequestMapping(ApiConsts.API_V1_ENDPOINT + "/administrators")
public class AdministratorController extends BaseController {
    @ApiOperation(
            value = "Get all administrators",
            notes = "Retrieve all administrators within a district",
            response = List.class)
    @RequestMapping(
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getAll() {
        return respond(getTeacherManager().getAllTeachers());
    }

    @ApiOperation(
            value = "Get a teacher by ID",
            notes = "Given a teacher ID, the endpoint returns the teacher",
            response = Teacher.class)
    @RequestMapping(
            value = "/{administratorId}",
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity get(
            @ApiParam(name = "administratorId", required = true, value = "Administrator ID")
            @PathVariable(value="administratorId") Long teacherId) {
        return respond(getTeacherManager().getTeacher(teacherId));
    }

    @ApiOperation(
            value = "Create an administrator",
            notes = "Creates, assigns an ID to, persists and returns a administrator",
            response = EntityId.class)
    @RequestMapping(
            method = RequestMethod.POST,
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity create(@RequestBody @Valid Administrator admin) {
        return respond(getAdminManager().createAdministrator(admin));
    }

    @ApiOperation(
            value = "Overwrite an existing administrator",
            notes = "Overwrites an existing administrator entity within a district with the ID provided",
            response = EntityId.class)
    @RequestMapping(
            value = "/{administratorId}",
            method = RequestMethod.PUT,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity replace(
            @ApiParam(name = "administratorId", required = true, value = "Administrator ID")
            @PathVariable(value="administratorId") Long administratorId,
            @RequestBody @Valid Administrator admin) {
        return respond(getAdminManager().replaceAdmin(administratorId, admin));
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
        return respond(getTeacherManager().updateTeacher(teacherId, teacher));
    }

    @ApiOperation(
            value = "Delete a administrator from a district by ID",
            response = Void.class)
    @RequestMapping(
            value = "/{administratorId}",
            method = RequestMethod.DELETE,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity delete(
            @ApiParam(name = "administratorId", required = true, value = "Administrator ID")
            @PathVariable(value="administratorId") Long administratorId) {
        return respond(getAdminManager().deleteAdministrator(administratorId));
    }
}
