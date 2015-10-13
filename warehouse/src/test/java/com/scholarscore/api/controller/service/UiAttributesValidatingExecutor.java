package com.scholarscore.api.controller.service;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.testng.Assert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.NullNode;
import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.UiAttributes;

public class UiAttributesValidatingExecutor {
    private final IntegrationBase serviceBase;
    
    public UiAttributesValidatingExecutor(IntegrationBase sb) {
        this.serviceBase = sb;
    }
    
    public UiAttributes get(Long schoolId, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getUiAttributesEndpoint(schoolId),
                null);
        UiAttributes attrs = serviceBase.validateResponse(response, new TypeReference<UiAttributes>(){});
        Assert.assertNotNull(attrs, "Unexpected null schoolYear returned for case: " + msg);
        return attrs;
    }
    
    public void getNegative(Long schoolId, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getUiAttributesEndpoint(schoolId),
                null);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(), 
                "Unexpected status code returned while retrieving schoolYear: " + msg);
    }
    
    public UiAttributes create(Long schoolId, UiAttributes attrs, String msg) {
        //Create the schoolYear
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getUiAttributesEndpoint(schoolId), null, attrs);
        EntityId attrsId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(attrsId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedUiAttributes(schoolId, attrs, HttpMethod.POST, msg);
    }
    
    public void createNegative(Long schoolId, UiAttributes attrs, HttpStatus expectedCode, String msg) {
        //Attempt to create the schoolYear
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getUiAttributesEndpoint(schoolId), null, attrs);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(), 
                "Unexpected status code returned for case: " + msg);
    }
    
    public UiAttributes replace(Long schoolId, UiAttributes attrs, String msg) {
        //Create the schoolYear
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT, 
                serviceBase.getUiAttributesEndpoint(schoolId), 
                null, 
                attrs);
        EntityId attrsId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(attrsId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedUiAttributes(schoolId, attrs, HttpMethod.PUT, msg);
    }

    public void replaceNegative(Long schoolId, UiAttributes attrs, HttpStatus expectedCode, String msg) {
        //Create the schoolYear
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT, 
                serviceBase.getUiAttributesEndpoint(schoolId), 
                null, 
                attrs);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for PUT: " + msg);
    }
    
    protected UiAttributes retrieveAndValidateCreatedUiAttributes( Long schoolId, UiAttributes submittedAttrs, HttpMethod method, String msg) {
        //Retrieve and validate the created schoolYear
        UiAttributes createdAttrs = this.get(schoolId, msg);
        UiAttributes expectedAttrs = generateExpectationUiAttributes(submittedAttrs, createdAttrs, method);
        Assert.assertEquals(createdAttrs, expectedAttrs, "Unexpected attrs created for case: " + msg);
        return createdAttrs;
    }
    
    protected UiAttributes generateExpectationUiAttributes(UiAttributes submitted, UiAttributes created, HttpMethod method) {
        UiAttributes returnAttrs = new UiAttributes(submitted);
        if(null != submitted.getSchool() && 
                submitted.getSchool().getId().equals(created.getSchool().getId())) {
            returnAttrs.setSchool(created.getSchool());
        }
        if(null == returnAttrs.getAttributes() 
                && null != created.getAttributes() 
                && created.getAttributes().getJsonNode() instanceof NullNode) {
            returnAttrs.setAttributes(created.getAttributes());
        }
        returnAttrs.setId(created.getId());
        return returnAttrs;
    }
}
