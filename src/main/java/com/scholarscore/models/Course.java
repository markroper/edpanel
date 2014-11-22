package com.scholarscore.models;

import java.util.List;
import java.util.Map;

/**
 * Expresses a course as a collection of specific assignments.
 * 
 * @author markroper
 *
 */
public class Course {
	private long id;
	private Map<String, List<Assignment>> assignmentsByType;
	
	public Course() {
	}
	
	public Map<String, List<Assignment>> getAssignmentsByType() {
		return assignmentsByType;
	}

	public void setAssignmentsByType(Map<String, List<Assignment>> assignmentsByType) {
		this.assignmentsByType = assignmentsByType;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
}
