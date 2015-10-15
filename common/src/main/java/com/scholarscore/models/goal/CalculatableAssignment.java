package com.scholarscore.models.goal;

import com.scholarscore.models.user.Student;

import java.util.Date;

/**
 * Created by cwallace on 10/15/2015.
 */
public interface CalculatableAssignment {

    public Long getParentId();

    public void setParentId(Long parentId);

    public void setStudent(Student student);

    public Student getStudent();
}
