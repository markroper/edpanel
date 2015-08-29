package com.scholarscore.api.controller.service;

import java.util.ArrayList;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.testng.Assert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.StudentSectionGrade;

public class StudentSectionGradeValidatingExecutor {
    private final IntegrationBase serviceBase;
    
    public StudentSectionGradeValidatingExecutor(IntegrationBase sb) {
        this.serviceBase = sb;
    }
    
    public StudentSectionGrade get(Long schoolId, Long schoolYearId, Long termId, Long sectionId, Long studentId, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getStudentSectionGradeEndpoint(schoolId, schoolYearId, termId, sectionId, studentId), 
                null);
        StudentSectionGrade studentSection = serviceBase.validateResponse(response, new TypeReference<StudentSectionGrade>(){});
        Assert.assertNotNull(studentSection, "Unexpected null section assignment returned for case: " + msg);
        
        return studentSection;
    }
    
    public void getAll(Long schoolId, Long schoolYearId, Long termId, Long sectionId, Long studentId, String msg, int numberOfItems) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getStudentSectionGradeEndpoint(schoolId, schoolYearId, termId, sectionId), 
                null);
        ArrayList<StudentSectionGrade> studentSectionGrades = serviceBase.validateResponse(response, new TypeReference<ArrayList<StudentSectionGrade>>(){});
        Assert.assertNotNull(studentSectionGrades, "Unexpected null term returned for case: " + msg);
        Assert.assertEquals(studentSectionGrades.size(), numberOfItems, "Unexpected number of items returned for case: " + msg);
    }
    
    public void getNegative(Long schoolId, Long schoolYearId, Long termId, Long sectionId, Long studentId, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getStudentSectionGradeEndpoint(schoolId, schoolYearId, termId, sectionId, studentId),
                null);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(), 
                "Unexpected status code returned while retrieving term: " + msg);
    }
    
    public StudentSectionGrade create(Long schoolId, Long schoolYearId, Long termId, Long sectionId, Long studentId, 
            StudentSectionGrade studentSectionGrade, String msg) {
        //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getStudentSectionGradeEndpoint(schoolId, schoolYearId, termId, sectionId, studentId), null, studentSectionGrade);
        EntityId studentSectionGradeId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        return retrieveAndValidateCreatedStudentSectionGrade(
                schoolId, schoolYearId, termId, sectionId, studentId, studentSectionGradeId, studentSectionGrade, HttpMethod.POST, msg);
    }
    
    public void createNegative(Long schoolId, Long schoolYearId, Long termId, Long sectionId, Long studentId,
            StudentSectionGrade studentSectionGrade, HttpStatus expectedCode, String msg) {
        //Attempt to create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getStudentSectionGradeEndpoint(
                schoolId, schoolYearId, termId, sectionId, studentId), null, studentSectionGrade);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(), 
                "Unexpected status code returned for case: " + msg);
    }
    
    public void delete(Long schoolId, Long schoolYearId, Long termId, Long sectionId, Long studentId, String msg) {
        //Delete the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE, 
                serviceBase.getStudentSectionGradeEndpoint(schoolId, schoolYearId, termId, sectionId, studentId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), HttpStatus.OK.value(),
                "Non 200 HttpStatus returned on delete for case: " + msg);
        getNegative(schoolId, schoolYearId, termId, sectionId, studentId, HttpStatus.NOT_FOUND, msg);
    }
    
    public void deleteNegative(Long schoolId, Long schoolYearId, Long termId, Long sectionId, Long studentId, 
            HttpStatus expectedCode, String msg) {
        //Attempt to delete the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE, 
                serviceBase.getStudentSectionGradeEndpoint(schoolId, schoolYearId, termId, sectionId, studentId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for DELETE: " + msg);
    }

    public StudentSectionGrade replace(Long schoolId, Long schoolYearId, Long termId, Long sectionId, Long studentId,
            StudentSectionGrade studentSectionGrade, String msg) {
        //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT, 
                serviceBase.getStudentSectionGradeEndpoint(schoolId, schoolYearId, termId, sectionId, studentId), 
                null, 
                studentSectionGrade);
        EntityId studentSectionGradeId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        return retrieveAndValidateCreatedStudentSectionGrade(schoolId, schoolYearId, termId, sectionId, studentId,
                studentSectionGradeId, studentSectionGrade, HttpMethod.PUT, msg);
    }

    public void replaceNegative(Long schoolId, Long schoolYearId, Long termId, Long sectionId, Long studentId, 
            StudentSectionGrade studentSectionGrade, HttpStatus expectedCode, String msg) {
        //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT, 
                serviceBase.getStudentSectionGradeEndpoint(schoolId, schoolYearId, termId, sectionId, studentId), 
                null, 
                studentSectionGrade);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for PUT: " + msg);
    }

    public StudentSectionGrade update(Long schoolId, Long schoolYearId, Long termId, Long sectionId, Long studentId,
            StudentSectionGrade studentSectionGrade, String msg) {
      //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.PATCH, 
                serviceBase.getStudentSectionGradeEndpoint(schoolId, schoolYearId, termId, sectionId, studentId), 
                null, 
                studentSectionGrade);
        EntityId studentSectionGradeId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        return retrieveAndValidateCreatedStudentSectionGrade(schoolId, schoolYearId, termId, sectionId, studentId, 
                studentSectionGradeId, studentSectionGrade, HttpMethod.PATCH, msg);
    }

    public void updateNegative(Long schoolId, Long schoolYearId, Long termId, Long sectionId, Long studentId,
            StudentSectionGrade studentSectionGrade, HttpStatus expectedCode, String msg) {
      //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.PATCH, 
                serviceBase.getStudentSectionGradeEndpoint(schoolId, schoolYearId, termId, sectionId, studentId), 
                null, 
                studentSectionGrade);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for PUT: " + msg);
    }

    public IntegrationBase getServiceBase() {
        return serviceBase;
    }
    
    /**
     * Given an ID and the value submitted to a GET/PUT endpoint, retrieve the term via GET, validate it, and 
     * return it to the caller.
     * 
     * @param termId
     * @param submittedStudentSectionGrade
     * @param msg
     * @return
     */
    protected StudentSectionGrade retrieveAndValidateCreatedStudentSectionGrade( Long schoolId, Long schoolYearId, Long termId,
            Long sectionId, Long studentId, EntityId id, StudentSectionGrade submittedStudentSectionGrade, HttpMethod method, String msg) {
        
        //Retrieve and validate the created term
        StudentSectionGrade createdStudentSectionGrade = this.get(schoolId, schoolYearId, termId, sectionId, studentId, msg);
        StudentSectionGrade expectedStudentSectionGrade = generateExpectationStudentSectionGrade(submittedStudentSectionGrade, createdStudentSectionGrade, method);
        Assert.assertEquals(createdStudentSectionGrade, expectedStudentSectionGrade, "Unexpected term created for case: " + msg);
        
        return createdStudentSectionGrade;
    }
    /**
     * Given a submitted section assignment object and a section assignment instance returned by the API after creation,
     * this method returns a new StudentSectionGrade instance that represents the expected state of the submitted 
     * StudentSectionGrade after creation.  The reason that there are differences in the submitted and expected 
     * instances is that there may be system assigned values not in the initially submitted object, for 
     * example, the id property.
     * 
     * @param submitted
     * @param created
     * @return
     */
    protected StudentSectionGrade generateExpectationStudentSectionGrade(StudentSectionGrade submitted, StudentSectionGrade created, HttpMethod method) {
        StudentSectionGrade returnStudentSectionGrade = new StudentSectionGrade(submitted);
        if(method == HttpMethod.PATCH) {
            returnStudentSectionGrade.mergePropertiesIfNull(created);
        }
        returnStudentSectionGrade.setId(created.getId());
        return returnStudentSectionGrade;
    }
}
