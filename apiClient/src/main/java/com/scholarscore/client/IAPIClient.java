package com.scholarscore.client;

import com.scholarscore.models.*;

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
    
    Collection<Teacher> getTeachers();
    
    Collection<Behavior> getBehaviors(Long studentId);
    Behavior createBehavior(Long studentId, Behavior behavior);
    
    Teacher createTeacher(Teacher teacher);
    Administrator createAdministrator(Administrator administrator);
    User createUser(User login);
    Course createCourse(Long schoolId, Course course);
}
