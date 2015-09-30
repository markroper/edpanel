package com.scholarscore.api.controller.service;

import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.testng.Assert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.SchoolDayList;
import com.scholarscore.models.attendance.SchoolDay;

public class SchoolDayValidatingExecutor {
    private final IntegrationBase serviceBase;
    
    public SchoolDayValidatingExecutor(IntegrationBase sb) {
        this.serviceBase = sb;
    }
    
    public List<SchoolDay> getAllInYear(Long schoolId, Long yearId, String msg, int numberOfItems) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getSchoolDayEndpoint(schoolId) + "/years/" + yearId, 
                null);
        SchoolDayList days = serviceBase.validateResponse(response, new TypeReference<SchoolDayList>(){});
        Assert.assertNotNull(days, "Unexpected null list of days returned for case: " + msg);
        Assert.assertEquals(days.size(), numberOfItems, "Unexpected number of results returned for case: " + msg);
        return days;
    }
    
    public SchoolDay get(Long schoolId, Long schoolDayId, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getSchoolDayEndpoint(schoolId,schoolDayId), 
                null);
        SchoolDay day = serviceBase.validateResponse(response, new TypeReference<SchoolDay>(){});
        Assert.assertNotNull(day, "Unexpected null day for case: " + msg);
        return day;
    }
    
    public void getNegative(Long schoolId, Long schoolDayId, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getSchoolDayEndpoint(schoolId,schoolDayId), 
                null);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(), 
                "Unexpected status code returned while retrieving day: " + msg);
    }
    
    public SchoolDay create(Long schoolId, SchoolDay day, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.POST, 
                serviceBase.getSchoolDayEndpoint(schoolId), 
                null, 
                day);
        EntityId dayId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(dayId, "unexpected null day ID returned from create call for case: " + msg);
        return retrieveAndValidateCreatedDay(schoolId, day, dayId, HttpMethod.POST, msg);
    }
    
    public void createNegative(Long schoolId, SchoolDay day, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.POST, 
                serviceBase.getSchoolDayEndpoint(schoolId), 
                null, 
                day);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(), 
                "Unexpected status code returned for case: " + msg);
    }
    
    public void delete(Long schoolId, SchoolDay day, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.DELETE, 
                serviceBase.getSchoolDayEndpoint(schoolId, day.getId()));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), HttpStatus.OK.value(),
                "Non 200 HttpStatus returned on delete for case: " + msg);
        getNegative(schoolId, day.getId(), HttpStatus.NOT_FOUND, msg);
    }
    
    public void deleteNegative(Long schoolId, SchoolDay day, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE, 
                serviceBase.getSchoolDayEndpoint(schoolId, day.getId()));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for DELETE: " + msg);
    }
    
    protected SchoolDay retrieveAndValidateCreatedDay( Long schoolId, SchoolDay submitted, EntityId id, HttpMethod method, String msg) {
        submitted.setId(id.getId());
        SchoolDay created = this.get(schoolId, id.getId(), msg);
        SchoolDay expected = submitted;
        Assert.assertEquals(created, expected, msg);
        return created;
    }

}
