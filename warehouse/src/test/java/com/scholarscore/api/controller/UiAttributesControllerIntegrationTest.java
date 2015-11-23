package com.scholarscore.api.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.JsonAttributes;
import com.scholarscore.models.School;
import com.scholarscore.models.UiAttributes;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test(groups = { "integration" })
public class UiAttributesControllerIntegrationTest extends IntegrationBase {
    private static ObjectMapper MAPPER = new ObjectMapper().
            setSerializationInclusion(JsonInclude.Include.NON_NULL).
            registerModule(new JavaTimeModule()).
            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    private School school;
    private UiAttributes instanceAttrs;
    
    @BeforeClass
    public void init() {
        authenticate();
        school = new School();
        school.setName(localeServiceUtil.generateName());
        school = schoolValidatingExecutor.create(school, "Create base school");
    }
    
    //Positive test cases
    @DataProvider
    public Object[][] createAttrsProvider() {
       UiAttributes populatedAttributes = new UiAttributes();
       ObjectNode node = MAPPER.createObjectNode();
       node.put("chalupa", "grande");
       
       populatedAttributes.setSchool(school);
       populatedAttributes.setAttributes(new JsonAttributes(node));
       
        return new Object[][] {
                { "Ui Attrs", populatedAttributes }
        };
    }
    
    @Test(dataProvider = "createAttrsProvider")
    public void createAttrsTest(String msg, UiAttributes attrs) {
        instanceAttrs = uiAttributesValidatingExecutor.create(school.getId(), attrs, msg);
    }
    
    @Test(dependsOnMethods = { "createAttrsTest" })
    public void replaceAttrsTest() {
        Assert.assertTrue(null != instanceAttrs);
        UiAttributes attrs = new UiAttributes();
        attrs.setSchool(school);
        attrs.setId(instanceAttrs.getId());
        UiAttributes returned = uiAttributesValidatingExecutor.replace(school.getId(), attrs, "replacement test");
    }
}
