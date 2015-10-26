package com.scholarscore.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
    private ObjectMapper mapper = new ObjectMapper();
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
       ObjectNode node = mapper.createObjectNode();
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
