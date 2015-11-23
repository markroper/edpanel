package com.scholarscore.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

@Test(groups = { "unit" })
public class UiAttributesUnitTest {
    ObjectMapper mapper = new ObjectMapper().
        setSerializationInclusion(JsonInclude.Include.NON_NULL).
        registerModule(new JavaTimeModule()).
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    
    @Test
    public void uiAttributesSerializationTest() throws JsonParseException, JsonMappingException, IOException {
        School school = new School();
        school.setName("Test School");
        UiAttributes populatedAttributes = new UiAttributes();
        ObjectNode node = mapper.createObjectNode();
        node.put("chalupa", "grande");
        
        populatedAttributes.setSchool(school);
        populatedAttributes.setAttributes(new JsonAttributes(node));
        
        String json = null;
        try {
            json = mapper.writeValueAsString(populatedAttributes);
        } catch (JsonProcessingException e) {
            Assert.fail();
        }
        UiAttributes reanimated = mapper.readValue(json, UiAttributes.class);
        Assert.assertEquals(populatedAttributes, reanimated);
    }
}
