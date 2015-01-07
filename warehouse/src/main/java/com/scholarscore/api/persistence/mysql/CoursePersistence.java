package com.scholarscore.api.persistence.mysql;

import java.util.Collection;

import com.scholarscore.models.Course;

public interface CoursePersistence {
    public Collection<Course> selectAllCourses(long schoolId);

    public Course selectCourse(
            long schoolId, 
            long courseId);

    public Long insertCourse(
            long schoolId, 
            Course course);

    public Long updateCourse(
            long schoolId, 
            long courseId,
            Course course);

    //Only need to delete the parent record, our deletes cascade
    public Long deleteCourse(long courseId);
}
