package com.scholarscore.api.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.scholarscore.models.Assignment;
import com.scholarscore.api.util.ErrorCode;

/**
 * The class defines the REST controller end points for create, read, update and delete operations on the Assignment resource.
 * 
 * @author markroper
 * @see Assignment
 */
@Controller
@RequestMapping("/api/v1/assignment")
public class AssignmentController {
	//TODO: @mroper we need to add a real persistence layer that we call instead of manipulating this map
	public static final String JSON_ACCEPT_HEADER = "application/json";
	public static Map<Long, Assignment> assignments = new HashMap<>();
	public static Long nextAssignmentId = 0l;

	 @RequestMapping(value = "/{assignmentId}", method = RequestMethod.GET, produces = {JSON_ACCEPT_HEADER})
	 @SuppressWarnings("rawtypes")
	 public @ResponseBody ResponseEntity getAssignment(
			 @PathVariable(value="assignmentId") Long assignmentId) {
		 Assignment assignment = null;
		 HttpStatus status = HttpStatus.NOT_FOUND;
		 if(assignments.containsKey(assignmentId)) {
			 assignment = assignments.get(assignmentId);
			 status = HttpStatus.OK;
		 }
		 return new ResponseEntity<>(assignment, status);
	 }

	 @RequestMapping(value = "/", method = RequestMethod.POST, produces = {JSON_ACCEPT_HEADER})
	 @SuppressWarnings("rawtypes")
	 public @ResponseBody ResponseEntity createAssignment(@RequestBody Assignment assignment) {
		 assignment.setId(nextAssignmentId++);
		 assignments.put(assignment.getId(), assignment);
		 return new ResponseEntity<>(assignment, HttpStatus.OK);
	 }
	 
	 @RequestMapping(value = "/{assignmentId}", method = RequestMethod.PUT, produces = {JSON_ACCEPT_HEADER})
	 @SuppressWarnings("rawtypes")
	 public @ResponseBody ResponseEntity replaceAssignment(
			 @PathVariable(value="assignmentId") Long assignmentId,
			 @RequestBody Assignment assignment) {
		 ResponseEntity returnValue = null;
		 if(null != assignmentId && assignments.containsKey(assignmentId)) {
			 returnValue = new ResponseEntity<>(assignments.get(assignmentId), HttpStatus.OK);
		 } else {
			 returnValue = new ResponseEntity<>((Assignment) null, HttpStatus.NOT_FOUND);
		 }
		 return returnValue;
	 }
	 
	 @RequestMapping(value = "/{assignmentId}", method = RequestMethod.DELETE, produces = {JSON_ACCEPT_HEADER})
	 @SuppressWarnings("rawtypes")
	 public @ResponseBody ResponseEntity deleteAssignment(
			 @PathVariable(value="assignmentId") Long assignmentId) {
		 HttpStatus status = HttpStatus.OK;
		 if(null == assignmentId) {
			 status = HttpStatus.BAD_REQUEST;
		 } else if (!assignments.containsKey(assignmentId)) {
			 status = HttpStatus.NOT_FOUND;
		 }
		 assignments.remove(assignmentId);
		 return new ResponseEntity<>((Assignment) null, status);
	 }
}
