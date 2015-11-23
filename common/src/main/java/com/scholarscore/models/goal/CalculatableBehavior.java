package com.scholarscore.models.goal;

import com.scholarscore.models.BehaviorCategory;
import com.scholarscore.models.user.Student;

import java.time.LocalDate;

/**
 * Interface for defining a goal/component based on behavior is calculatable
 * Created by cwallace on 10/14/2015.
 */
public interface CalculatableBehavior {

    public LocalDate getStartDate();

    public void setStartDate(LocalDate date);

    public LocalDate getEndDate();

    public void  setEndDate(LocalDate date);

    public BehaviorCategory getBehaviorCategory();

    public void setBehaviorCategory(BehaviorCategory behaviorCategory);

    public void setStudent(Student student);

    public Student getStudent();
}
