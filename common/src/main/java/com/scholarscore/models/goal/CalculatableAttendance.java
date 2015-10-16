package com.scholarscore.models.goal;

import com.scholarscore.models.user.Student;

import java.util.Date;

/**
 * Interface for defining that a goal/component
 * based on attendance is calculatable
 * Created by cwallace on 10/15/2015.
 */
public interface CalculatableAttendance {

    public Date getEndDate();

    public void setEndDate(Date date);

    public Date getStartDate();

    public void setStartDate(Date date);

    public Long getParentId();

    public void setParentId(Long parentId);

    public void setStudent(Student student);

    public Student getStudent();
}
