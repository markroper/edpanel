package com.scholarscore.api.manager;

import com.scholarscore.api.persistence.EntityPersistence;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.Course;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

/**
 * Created by cwallace on 9/16/2015.
 */
public class CourseManagerImpl implements CourseManager {

    @Autowired
    private EntityPersistence<Course> coursePersistence;

    @Autowired
    private OrchestrationManager pm;

    private static final String COURSE = "course";

    public void setCoursePersistence(EntityPersistence<Course> coursePersistence) {
        this.coursePersistence = coursePersistence;
    }

    public void setPm(OrchestrationManager pm) {
        this.pm = pm;
    }

    @Override
    public ServiceResponse<Collection<Course>> getAllCourses(long schoolId) {
        StatusCode code = pm.getSchoolManager().schoolExists(schoolId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        return new ServiceResponse<Collection<Course>>(
                coursePersistence.selectAll(schoolId));
    }

    @Override
    public StatusCode courseExists(long schoolId, long courseId) {
        StatusCode code = pm.getSchoolManager().schoolExists(schoolId);
        if(!code.isOK()) {
            return code;
        }
        Course course = coursePersistence.select(schoolId, courseId);
        if(null == course) {
            return StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{COURSE, courseId});
        }
        return StatusCodes.getStatusCode(StatusCodeType.OK);
    }

    @Override
    public ServiceResponse<Course> getCourse(long schoolId, long courseId) {
        StatusCode code = courseExists(schoolId, courseId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        return new ServiceResponse<>(coursePersistence.select(schoolId, courseId));
    }

    @Override
    public ServiceResponse<Long> createCourse(long schoolId, Course course) {
        StatusCode code = pm.getSchoolManager().schoolExists(schoolId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        return new ServiceResponse<>(coursePersistence.insert(schoolId, course));
    }

    @Override
    public ServiceResponse<Long> replaceCourse(long schoolId, long courseId,
                                               Course course) {
        StatusCode code = courseExists(schoolId, courseId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        coursePersistence.update(schoolId, courseId, course);
        return new ServiceResponse<>(courseId);
    }

    @Override
    public ServiceResponse<Long> updateCourse(long schoolId, long courseId,
                                              Course course) {
        StatusCode code = courseExists(schoolId, courseId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        course.mergePropertiesIfNull(coursePersistence.select(schoolId, courseId));
        coursePersistence.update(schoolId, courseId, course);
        return new ServiceResponse<>(courseId);
    }

    @Override
    public ServiceResponse<Long> deleteCourse(long schoolId, long courseId) {
        StatusCode code = courseExists(schoolId, courseId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        //Only need to delete the parent record, our deletes cascade
        coursePersistence.delete(courseId);
        return new ServiceResponse<Long>((Long) null);
    }
}
