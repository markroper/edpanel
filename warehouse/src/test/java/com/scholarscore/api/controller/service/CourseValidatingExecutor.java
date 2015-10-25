package com.scholarscore.api.controller.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.Course;
import com.scholarscore.models.EntityId;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.testng.Assert;

import java.util.ArrayList;

public class CourseValidatingExecutor {
    private final IntegrationBase serviceBase;
    
    public CourseValidatingExecutor(IntegrationBase sb) {
        this.serviceBase = sb;
    }
    
    public Course get(Long schoolId, Long courseId, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getCourseEndpoint(schoolId, courseId),
                null);
        Course course = serviceBase.validateResponse(response, new TypeReference<Course>(){});
        Assert.assertNotNull(course, "Unexpected null course returned for case: " + msg);
        
        return course;
    }
    
    public void getAll(Long schoolId, String msg, int numberOfItems) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getCourseEndpoint(schoolId), 
                null);
        ArrayList<Course> courses = serviceBase.validateResponse(response, new TypeReference<ArrayList<Course>>(){});
        Assert.assertNotNull(courses, "Unexpected null course returned for case: " + msg);
        Assert.assertEquals(courses.size(), numberOfItems, "Unexpected number of items returned for case: " + msg);
    }
    
    public void getNegative(Long schoolId, Long courseId, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getCourseEndpoint(schoolId, courseId),
                null);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(), 
                "Unexpected status code returned while retrieving course: " + msg);
    }
    
    public Course create(Long schoolId, Course course, String msg) {
        //Create the course
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getCourseEndpoint(schoolId), null, course);
        EntityId returnedCourseId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(returnedCourseId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedCourse(schoolId, returnedCourseId.getId(), course, HttpMethod.POST, msg);
    }
    
    public void createNegative(Long schoolId, Course course, HttpStatus expectedCode, String msg) {
        //Attempt to create the course
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getCourseEndpoint(schoolId), null, course);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(), 
                "Unexpected status code returned for case: " + msg);
    }
    
    public void delete(Long schoolId, Long courseId, String msg) {
        //Delete the course
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE, 
                serviceBase.getCourseEndpoint(schoolId, courseId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), HttpStatus.OK.value(),
                "Non 200 HttpStatus returned on delete for case: " + msg);
        getNegative(schoolId, courseId, HttpStatus.NOT_FOUND, msg);
    }
    
    public void deleteNegative(Long schoolId, Long courseId, HttpStatus expectedCode, String msg) {
        //Attempt to delete the course
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE, 
                serviceBase.getCourseEndpoint(schoolId, courseId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for DELETE: " + msg);
    }

    public Course replace(Long schoolId, Long courseId, Course course, String msg) {
        //Create the course
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT, 
                serviceBase.getCourseEndpoint(schoolId, courseId), 
                null, 
                course);
        EntityId returnedCourseId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(returnedCourseId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedCourse(schoolId, courseId, course, HttpMethod.PUT, msg);
    }

    public void replaceNegative(Long schoolId, Long courseId, Course course, HttpStatus expectedCode, String msg) {
        //Create the course
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT, 
                serviceBase.getCourseEndpoint(schoolId, courseId), 
                null, 
                course);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for PUT: " + msg);
    }

    public Course update(Long schoolId, Long courseId, Course course, String msg) {
      //Create the course
        ResultActions response = serviceBase.makeRequest(HttpMethod.PATCH, 
                serviceBase.getCourseEndpoint(schoolId, courseId), 
                null, 
                course);
        EntityId returnedCourseId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(returnedCourseId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedCourse(schoolId, returnedCourseId.getId(), course, HttpMethod.PATCH, msg);
    }

    public void updateNegative(Long schoolId, Long courseId, Course course, HttpStatus expectedCode, String msg) {
      //Create the course
        ResultActions response = serviceBase.makeRequest(HttpMethod.PATCH, 
                serviceBase.getCourseEndpoint(schoolId, courseId), 
                null, 
                course);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for PUT: " + msg);
    }

    public IntegrationBase getServiceBase() {
        return serviceBase;
    }
    
    /**
     * Given an ID and the value submitted to a GET/PUT endpoint, retrieve the course via GET, validate it, and 
     * return it to the caller.
     * 
     * @param courseId
     * @param submittedCourse
     * @param msg
     * @return
     */
    protected Course retrieveAndValidateCreatedCourse( Long schoolId, Long courseId, Course submittedCourse, HttpMethod method, String msg) {
        //Retrieve and validate the created course
        Course createdCourse = this.get(schoolId, courseId, msg);
        //Keep a reference to the created course for later cleanup
        Course expectedCourse = generateExpectationCourse(submittedCourse, createdCourse, method);
        Assert.assertEquals(createdCourse, expectedCourse, "Unexpected course created for case: " + msg);
        
        return createdCourse;
    }
    /**
     * Given a submitted course object and an course instance returned by the API after creation,
     * this method returns a new Course instance that represents the expected state of the submitted 
     * Course after creation.  The reason that there are differences in the submitted and expected 
     * instances is that there may be system assigned values not in the initially submitted object, for 
     * example, the id property.
     * 
     * @param submitted
     * @param created
     * @return
     */
    protected Course generateExpectationCourse(Course submitted, Course created, HttpMethod method) {
        Course returnCourse = new Course(submitted);
        if(method == HttpMethod.PATCH) {
            returnCourse.mergePropertiesIfNull(created);
        } else if(null == returnCourse.getId()) {
            returnCourse.setId(created.getId());
        }
        if (null == returnCourse.getSchool()) {
            returnCourse.setSchool(created.getSchool());
        }
        return returnCourse;
    }
}
