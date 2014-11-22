package com.scholarscore.models;

import java.util.Date;

/**
 * Expresses graded assignments such a quiz, test, homework, lab, and so on.
 * 
 * @author markroper
 * @see Assignment
 *
 */
public class GradedAssignment extends Assignment {
	private Date assignedDate;
	private Date dueDate;
	private IGrade grade;
	
	public GradedAssignment() {
	}

	public Date getAssignedDate() {
		return assignedDate;
	}

	public void setAssignedDate(Date assignedDate) {
		this.assignedDate = assignedDate;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public IGrade getGrade() {
		return grade;
	}

	public void setGrade(IGrade grade) {
		this.grade = grade;
	}
	
}
