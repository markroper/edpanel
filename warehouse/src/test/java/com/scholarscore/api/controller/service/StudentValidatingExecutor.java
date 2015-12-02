package com.scholarscore.api.controller.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.user.Student;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.testng.Assert;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StudentValidatingExecutor {
    private final IntegrationBase serviceBase;
    
    public StudentValidatingExecutor(IntegrationBase sb) {
        this.serviceBase = sb;
    }
    
    public Student get(Long studentId, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getStudentEndpoint(studentId),
                null);
        Student student = serviceBase.validateResponse(response, new TypeReference<Student>(){});
        Assert.assertNotNull(student, "Unexpected null student returned for case: " + msg);
        
        return student;
    }
    
    public void getAll(String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getStudentEndpoint(), 
                null);
        ArrayList<Student> students = serviceBase.validateResponse(response, new TypeReference<ArrayList<Student>>(){});
        Assert.assertNotNull(students, "Unexpected null student returned for case: " + msg);
    }
    
    public void getNegative(Long studentId, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getStudentEndpoint(studentId),
                null);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(), 
                "Unexpected status code returned while retrieving student: " + msg);
    }
    
    
    public Student create(Student student, String msg) {
        //Create the student
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getStudentEndpoint(), null, student);
        EntityId studentId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(studentId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedStudent(studentId.getId(), student, HttpMethod.POST, msg);
    }
    
    public void createNegative(Student student, HttpStatus expectedCode, String msg) {
        //Attempt to create the student
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getStudentEndpoint(), null, student);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(), 
                "Unexpected status code returned for case: " + msg);
    }
    
    public void delete(Long studentId, String msg) {
        //Delete the student
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE, 
                serviceBase.getStudentEndpoint(studentId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), HttpStatus.OK.value(),
                "Non 200 HttpStatus returned on delete for case: " + msg);
        getNegative(studentId, HttpStatus.NOT_FOUND, msg);
    }
    
    public void deleteNegative(Long studentId, HttpStatus expectedCode, String msg) {
        //Attempt to delete the student
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE, 
                serviceBase.getStudentEndpoint(studentId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for DELETE: " + msg);
    }

    public Student replace(Long studentId, Student student, String msg) {
        //Create the student
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT, 
                serviceBase.getStudentEndpoint(studentId), 
                null, 
                student);
        EntityId returnedStudentId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(returnedStudentId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedStudent(studentId, student, HttpMethod.PUT, msg);
    }

    public void replaceNegative(Long studentId, Student student, HttpStatus expectedCode, String msg) {
        //Create the student
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT, 
                serviceBase.getStudentEndpoint(studentId), 
                null, 
                student);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for PUT: " + msg);
    }

    public Student update(Long studentId, Student student, String msg) {
      //Create the student
        ResultActions response = serviceBase.makeRequest(HttpMethod.PATCH, 
                serviceBase.getStudentEndpoint(studentId), 
                null, 
                student);
        EntityId returnedStudentId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(returnedStudentId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedStudent(studentId, student, HttpMethod.PATCH, msg);
    }

    public void updateNegative(Long studentId, Long courseId, Long id, Student student, HttpStatus expectedCode, String msg) {
      //Create the student
        ResultActions response = serviceBase.makeRequest(HttpMethod.PATCH, 
                serviceBase.getStudentEndpoint(studentId), 
                null, 
                student);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for PUT: " + msg);
    }

    public IntegrationBase getServiceBase() {
        return serviceBase;
    }

    public String getGpa(Long studentId, Integer gpaScale) {
        ResultActions response = serviceBase.makeRequest(HttpMethod.GET,
                serviceBase.getStudentGpaEndpoint(studentId, gpaScale));

        MockHttpServletResponse resp = response.andReturn().getResponse();
        Assert.assertEquals(resp.getStatus(), HttpStatus.OK.value(),
                "Unexpected Http status code returned when fetching GPA");
        try {
            return resp.getContentAsString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public String getPrepScore(Long[] studentIds, String startDate, String endDate) { 
        Map<String, String> params = new HashMap<>();
        
        StringBuilder studentIdBuilder = new StringBuilder();
        boolean first = true;
        for (Long studentId : studentIds) {
            if (!first) { studentIdBuilder.append(", "); }
            else { first = false; } 
            studentIdBuilder.append(studentId);
        }
        
        if (studentIds != null) { params.put("studentId", studentIdBuilder.toString()); }
        if (startDate != null) { params.put("startDate", startDate); }
        if (endDate != null) { params.put("endDate", endDate); }
        ResultActions response = serviceBase.makeRequest(HttpMethod.GET,
                serviceBase.getStudentEndpoint() + "/prepscores", params, null);

        MockHttpServletResponse resp = response.andReturn().getResponse();
        Assert.assertEquals(resp.getStatus(), HttpStatus.OK.value(),
                "Unexpected Http status code returned when fetching PrepScore");
        try {
            return resp.getContentAsString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Given an ID and the value submitted to a GET/PUT endpoint, retrieve the assignment via GET, validate it, and 
     * return it to the caller.
     * 
     * @param assignmentId
     * @param submittedStudent
     * @param msg
     * @return
     */
    protected Student retrieveAndValidateCreatedStudent( Long studentId, Student submittedStudent, HttpMethod method, String msg) {
        //Retrieve and validate the created assignment
        Student createdStudent = this.get(studentId, msg);
        //Keep a reference to the created assignment for later cleanup
        serviceBase.studentsCreated.add(createdStudent);
        Student expectedStudent = generateExpectationStudent(submittedStudent, createdStudent, method);
        createdStudent.setCurrentSchoolId(expectedStudent.getCurrentSchoolId());
        Assert.assertEquals(createdStudent, expectedStudent, "Unexpected assignment created for case: " + msg);
        
        return createdStudent;
    }
    /**
     * Given a submitted assignment object and an assignment instance returned by the API after creation,
     * this method returns a new Student instance that represents the expected state of the submitted 
     * Student after creation.  The reason that there are differences in the submitted and expected 
     * instances is that there may be system assigned values not in the initially submitted object, for 
     * example, the id property.
     * 
     * @param submitted
     * @param created
     * @return
     */
    protected Student generateExpectationStudent(Student submitted, Student created, HttpMethod method) {
        Student returnStudent = new Student(submitted);
        if(method == HttpMethod.PATCH) {
            returnStudent.mergePropertiesIfNull(created);
        } else if(null == returnStudent.getId()) {
            returnStudent.setId(created.getId());
        }
        returnStudent.setPassword(null);
        if(null == returnStudent.getUsername()) {
            returnStudent.setUsername(created.getUsername());
        }
        returnStudent.setEnabled(created.getEnabled());
        returnStudent.setUserId(created.getUserId());
        returnStudent.setId(created.getId());
        return returnStudent;
    }
}
