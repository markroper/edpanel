package com.scholarscore.models.goal;

import com.scholarscore.models.user.Student;

/**
 * Created by cwallace on 10/14/2015.
 */
public interface GoalComponent {

    public void setStudent(Student student);

    public Student getStudent();
}
