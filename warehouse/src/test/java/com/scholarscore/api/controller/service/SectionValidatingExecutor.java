package com.scholarscore.api.controller.service;

import java.util.ArrayList;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.testng.Assert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.Section;

public class SectionValidatingExecutor {

    private final IntegrationBase serviceBase;
    
    public SectionValidatingExecutor(IntegrationBase sb) {
        this.serviceBase = sb;
    }
    
    public Section get(Long schoolId, Long schoolYearId, Long termId, Long id, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getSectionEndpoint(schoolId, schoolYearId, termId, id),
                null);
        Section section = serviceBase.validateResponse(response, new TypeReference<Section>(){});
        Assert.assertNotNull(section, "Unexpected null section returned for case: " + msg);
        
        return section;
    }
    
    public void getAll(Long schoolId, Long schoolYearId, Long termId, String msg, int numberOfItems) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getSectionEndpoint(schoolId, schoolYearId, termId), 
                null);
        ArrayList<Section> terms = serviceBase.validateResponse(response, new TypeReference<ArrayList<Section>>(){});
        Assert.assertNotNull(terms, "Unexpected null section returned for case: " + msg);
        Assert.assertTrue(terms.size() >= numberOfItems, "Unexpected number of items returned for case: " + msg);
    }
    
    public void getNegative(Long schoolId, Long schoolYearId, Long termId, Long id, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getSectionEndpoint(schoolId, schoolYearId, termId, id),
                null);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(), 
                "Unexpected status code returned while retrieving section: " + msg);
    }
    
    public Section create(Long schoolId, Long schoolYearId, Long termId, Section section, String msg) {
        //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getSectionEndpoint(schoolId, schoolYearId, termId), null, section);
        EntityId sectionId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(sectionId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedSection(schoolId, schoolYearId, termId, sectionId, section, HttpMethod.POST, msg);
    }
    
    public void createNegative(Long schoolId, Long schoolYearId, Long termId, Section section, HttpStatus expectedCode, String msg) {
        //Attempt to create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getSectionEndpoint(schoolId, schoolYearId, termId), null, section);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(), 
                "Unexpected status code returned for case: " + msg);
    }
    
    public void delete(Long schoolId, Long schoolYearId, Long termId, Long sectionId, String msg) {
        //Delete the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE, 
                serviceBase.getSectionEndpoint(schoolId, schoolYearId, termId, sectionId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), HttpStatus.OK.value(),
                "Non 200 HttpStatus returned on delete for case: " + msg);
        getNegative(schoolId, schoolYearId, termId, sectionId, HttpStatus.NOT_FOUND, msg);
    }
    
    public void deleteNegative(Long schoolId, Long schoolYearId, Long termId, Long sectionId, HttpStatus expectedCode, String msg) {
        //Attempt to delete the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE, 
                serviceBase.getSectionEndpoint(schoolId, schoolYearId, termId, sectionId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for DELETE: " + msg);
    }

    public Section replace(Long schoolId, Long schoolYearId, Long termId, Long id, Section section, String msg) {
        //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT, 
                serviceBase.getSectionEndpoint(schoolId, schoolYearId, termId, id), 
                null, 
                section);
        EntityId sectionId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(sectionId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedSection(schoolId, schoolYearId, termId, sectionId, section, HttpMethod.PUT, msg);
    }

    public void replaceNegative(Long schoolId, Long schoolYearId, Long termId, Long id, Section section, HttpStatus expectedCode, String msg) {
        //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT, 
                serviceBase.getSectionEndpoint(schoolId, schoolYearId, termId, id), 
                null, 
                section);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for PUT: " + msg);
    }

    public Section update(Long schoolId, Long schoolYearId, Long termId, Long id, Section section, String msg) {
      //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.PATCH, 
                serviceBase.getSectionEndpoint(schoolId, schoolYearId, termId, id), 
                null, 
                section);
        EntityId sectionId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(sectionId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedSection(schoolId, schoolYearId, termId, sectionId, section, HttpMethod.PATCH, msg);
    }

    public void updateNegative(Long schoolId, Long schoolYearId, Long termId, Long id, Section section, HttpStatus expectedCode, String msg) {
      //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.PATCH, 
                serviceBase.getSectionEndpoint(schoolId, schoolYearId, termId, id), 
                null, 
                section);
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
     * @param submittedSection
     * @param msg
     * @return
     */
    protected Section retrieveAndValidateCreatedSection( Long schoolId, Long schoolYearId, Long termId,
            EntityId sectionId, Section submittedSection, HttpMethod method, String msg) {
        
        //Retrieve and validate the created term
        Section createdSection = this.get(schoolId, schoolYearId, termId, sectionId.getId(), msg);
        Section expectedSection = generateExpectationSection(submittedSection, createdSection, method);
        Assert.assertEquals(createdSection, expectedSection, "Unexpected term created for case: " + msg);
        
        return createdSection;
    }
    /**
     * Given a submitted Section object and a Section instance returned by the API after creation,
     * this method returns a new Section instance that represents the expected state of the submitted 
     * Section after creation.  The reason that there are differences in the submitted and expected 
     * instances is that there may be system assigned values not in the initially submitted object, for 
     * example, the id property.
     * 
     * @param submitted
     * @param created
     * @return
     */
    protected Section generateExpectationSection(Section submitted, Section created, HttpMethod method) {
        Section returnSection = new Section(submitted);
        if(method == HttpMethod.PATCH) {
            returnSection.mergePropertiesIfNull(created);
        } else if(null != returnSection && null == returnSection.getId()) {
            returnSection.setId(created.getId());
        }
        return returnSection;
    }
}
