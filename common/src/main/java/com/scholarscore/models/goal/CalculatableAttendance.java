package com.scholarscore.models.goal;

import com.scholarscore.models.user.Student;

import java.time.LocalDate;

/**
 * Interface for defining that a goal/component
 * based on attendance is calculatable
 * Created by cwallace on 10/15/2015.
 */
public interface CalculatableAttendance {

    public LocalDate getEndDate();

    public void setEndDate(LocalDate date);

    public LocalDate getStartDate();

    public void setStartDate(LocalDate date);

    public Long getParentId();

    public void setParentId(Long parentId);

    public void setStudent(Student student);

    public Student getStudent();
}
