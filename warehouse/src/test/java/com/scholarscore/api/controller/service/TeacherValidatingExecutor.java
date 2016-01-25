package com.scholarscore.api.controller.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.user.Staff;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.testng.Assert;

import java.util.ArrayList;

public class TeacherValidatingExecutor {
    private final IntegrationBase serviceBase;
    
    public TeacherValidatingExecutor(IntegrationBase sb) {
        this.serviceBase = sb;
    }
    
    public Staff get(Long teacherId, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getTeacherEndpoint(teacherId),
                null);
        Staff teacher = serviceBase.validateResponse(response, new TypeReference<Staff>(){});
        Assert.assertNotNull(teacher, "Unexpected null teacher returned for case: " + msg);
        
        return teacher;
    }
    
    public void getAll(String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getTeacherEndpoint(), 
                null);
        ArrayList<Staff> teachers = serviceBase.validateResponse(response, new TypeReference<ArrayList<Staff>>(){});
        Assert.assertNotNull(teachers, "Unexpected null teacher returned for case: " + msg);
    }
    
    public void getNegative(Long teacherId, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getTeacherEndpoint(teacherId),
                null);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(), 
                "Unexpected status code returned while retrieving teacher: " + msg);
    }
    
    
    public Staff create(Staff teacher, String msg) {
        //Create the teacher
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getTeacherEndpoint(), null, teacher);
        EntityId teacherId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(teacherId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedTeacher(teacherId.getId(), teacher, HttpMethod.POST, msg);
    }
    
    public void createNegative(Staff teacher, HttpStatus expectedCode, String msg) {
        //Attempt to create the teacher
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getTeacherEndpoint(), null, teacher);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(), 
                "Unexpected status code returned for case: " + msg);
    }
    
    public void delete(Long teacherId, String msg) {
        //Delete the teacher
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE, 
                serviceBase.getTeacherEndpoint(teacherId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), HttpStatus.OK.value(),
                "Non 200 HttpStatus returned on delete for case: " + msg);
        getNegative(teacherId, HttpStatus.NOT_FOUND, msg);
    }
    
    public void deleteNegative(Long teacherId, HttpStatus expectedCode, String msg) {
        //Attempt to delete the teacher
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE, 
                serviceBase.getTeacherEndpoint(teacherId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for DELETE: " + msg);
    }

    public Staff replace(Long teacherId, Staff teacher, String msg) {
        //Create the teacher
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT, 
                serviceBase.getTeacherEndpoint(teacherId), 
                null, 
                teacher);
        EntityId returnedTeacherId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(returnedTeacherId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedTeacher(teacherId, teacher, HttpMethod.PUT, msg);
    }

    public void replaceNegative(Long teacherId, Staff teacher, HttpStatus expectedCode, String msg) {
        //Create the teacher
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT, 
                serviceBase.getTeacherEndpoint(teacherId), 
                null, 
                teacher);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for PUT: " + msg);
    }

    public Staff update(Long teacherId, Staff teacher, String msg) {
      //Create the teacher
        ResultActions response = serviceBase.makeRequest(HttpMethod.PATCH, 
                serviceBase.getTeacherEndpoint(teacherId), 
                null, 
                teacher);
        EntityId returnedTeacherId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(returnedTeacherId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedTeacher(teacherId, teacher, HttpMethod.PATCH, msg);
    }

    public void updateNegative(Long teacherId, Long courseId, Long id, Staff teacher, HttpStatus expectedCode, String msg) {
      //Create the teacher
        ResultActions response = serviceBase.makeRequest(HttpMethod.PATCH, 
                serviceBase.getTeacherEndpoint(teacherId), 
                null, 
                teacher);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for PUT: " + msg);
    }

    public IntegrationBase getServiceBase() {
        return serviceBase;
    }
    
    /**
     * Given an ID and the value submitted to a GET/PUT endpoint, retrieve the assignment via GET, validate it, and 
     * return it to the caller.
     * 
     * @param teacherId
     * @param submittedTeacher
     * @param msg
     * @return
     */
    protected Staff retrieveAndValidateCreatedTeacher( Long teacherId, Staff submittedTeacher, HttpMethod method, String msg) {
        //Retrieve and validate the created assignment
        Staff createdTeacher = this.get(teacherId, msg);
        //Keep a reference to the created assignment for later cleanup
        serviceBase.teachersCreated.add(createdTeacher);
        Staff expectedTeacher = generateExpectationTeacher(submittedTeacher, createdTeacher, method);
        Assert.assertEquals(createdTeacher, expectedTeacher, "Unexpected assignment created for case: " + msg);
        
        return createdTeacher;
    }
    /**
     * Given a submitted assignment object and an assignment instance returned by the API after creation,
     * this method returns a new Staff instance that represents the expected state of the submitted
     * Staff after creation.  The reason that there are differences in the submitted and expected
     * instances is that there may be system assigned values not in the initially submitted object, for 
     * example, the id property.
     * 
     * @param submitted
     * @param created
     * @return
     */
    protected Staff generateExpectationTeacher(Staff submitted, Staff created, HttpMethod method) {
        Staff returnTeacher = new Staff(submitted);
        if(method == HttpMethod.PATCH) {
            returnTeacher.mergePropertiesIfNull(created);
        } else if(null != returnTeacher && null == returnTeacher.getId()) {
            returnTeacher.setId(created.getId());
        }
        returnTeacher.setEnabled(created.getEnabled());
        returnTeacher.setPassword(null);
        if(null == returnTeacher.getUsername()) {
            returnTeacher.setUsername(created.getUsername());
        }
        returnTeacher.setUserId(created.getUserId());
        returnTeacher.setId(created.getId());
        return returnTeacher;
    }
}
