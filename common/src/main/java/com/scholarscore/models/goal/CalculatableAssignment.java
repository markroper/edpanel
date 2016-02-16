package com.scholarscore.models.goal;

import com.scholarscore.models.assignment.StudentAssignment;
import com.scholarscore.models.user.Student;

/**
 * Interface that defines the functionality for making an assignment based
 * component/goal calculatable
 * Created by cwallace on 10/15/2015.
 */
public interface CalculatableAssignment {

    public StudentAssignment getStudentAssignment();

    public void setStudentAssignment(StudentAssignment assignment);

    public void setStudent(Student student);

    public Student getStudent();
}
