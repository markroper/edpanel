package com.scholarscore.models;

import java.util.Date;

/**
 * Expresses attendance to a single class on a specific date as a subclass of Assignment.
 * 
 * @author markroper
 * @see Assignment
 *
 */
public class AttendanceAssignment extends Assignment {
	private Date date;
	
	public AttendanceAssignment() {
		
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
