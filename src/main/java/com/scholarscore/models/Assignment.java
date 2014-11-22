package com.scholarscore.models;

/**
 * Base class for all assignment subclasses encapsulating shared attributes and behaviors.
 * 
 * @author markroper
 *
 */
public class Assignment {
	private long id;
	private String name;
	private long courseId;
	
	public Assignment() {
		
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getCourseId() {
		return courseId;
	}

	public void setCourseId(long courseId) {
		this.courseId = courseId;
	}
}
