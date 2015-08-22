package com.scholarscore.client;

import com.scholarscore.models.Behavior;
import com.scholarscore.models.School;
import com.scholarscore.models.Student;

import java.util.Collection;

/**
 * Created by mattg on 7/3/15.
 */
public interface IAPIClient {
    School createSchool(School school);
    School getSchool(Long id);

    Student createStudent(Student student);
    Student updateStudent(Long studentId, Student student);
    Collection<Student> getStudents();
    
    Collection<Behavior> getBehaviors(Long studentId);
    Behavior createBehavior(Long studentId, Behavior behavior);
    
}
