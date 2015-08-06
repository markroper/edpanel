package com.scholarscore.client;

import com.scholarscore.models.*;

/**
 * Created by mattg on 7/3/15.
 */
public interface IAPIClient {
    School createSchool(School school);
    School getSchool(Long id);
    Student createStudent(Student student);
    Teacher createTeacher(Teacher teacher);
    Administrator createAdministrator(Administrator administrator);
    User createUser(User login);
    Course createCourse(Long schoolId, Course course);
}
