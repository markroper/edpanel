package com.scholarscore.models.goal;

import com.scholarscore.models.BehaviorCategory;
import com.scholarscore.models.user.Student;

import java.util.Date;

/**
 * Created by cwallace on 10/14/2015.
 */
public interface CalculatableBehavior {

    public Date getStartDate();

    public void setStartDate(Date date);

    public Date getEndDate();

    public void  setEndDate(Date date);

    public BehaviorCategory getBehaviorCategory();

    public void setBehaviorCategory(BehaviorCategory behaviorCategory);

    public void setStudent(Student student);

    public Student getStudent();
}
