package com.scholarscore.models.goal;

import com.scholarscore.models.user.Student;

/**
 * Interface for defining that a goal/component
 * Created by cwallace on 10/15/2015.
 */
public interface CalculatableCumulative {

    public Long getParentId();

    public void setParentId(Long parentId);

    public void setStudent(Student student);

    public Student getStudent();
}
