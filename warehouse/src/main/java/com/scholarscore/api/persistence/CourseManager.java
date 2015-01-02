package com.scholarscore.api.persistence;

import java.util.Collection;

import com.scholarscore.api.util.ErrorCode;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.Course;

public interface CourseManager {
    
    public ServiceResponse<Collection<Course>> getAllCourses(long schoolId);

    public ErrorCode courseExists(long schoolId, long courseId);
    
    public ServiceResponse<Course> getCourse(long schoolId, long courseId);

    public ServiceResponse<Long> createCourse(long schoolId, Course course);

    public ServiceResponse<Long> replaceCourse(long schoolId, long courseId, Course course);
    
    public ServiceResponse<Long> updateCourse(long schoolId, long courseId, Course course);

    public ServiceResponse<Long> deleteCourse(long schoolId, long courseId);
}
