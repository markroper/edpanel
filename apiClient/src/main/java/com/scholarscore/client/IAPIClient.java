package com.scholarscore.client;

import com.scholarscore.models.School;
import com.scholarscore.models.Student;

/**
 * Created by mattg on 7/3/15.
 */
public interface IAPIClient {
    School createSchool(School school);
    School getSchool(Long id);

    Student createStudent(Student student);
}
