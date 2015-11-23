package com.scholarscore.models;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.scholarscore.util.EdPanelObjectMapper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

@Test(groups = { "unit" })
public class UiAttributesUnitTest {
    @Test
    public void uiAttributesSerializationTest() throws JsonParseException, JsonMappingException, IOException {
        School school = new School();
        school.setName("Test School");
        UiAttributes populatedAttributes = new UiAttributes();
        ObjectNode node = EdPanelObjectMapper.MAPPER.createObjectNode();
        node.put("chalupa", "grande");
        
        populatedAttributes.setSchool(school);
        populatedAttributes.setAttributes(new JsonAttributes(node));
        
        String json = null;
        try {
            json = EdPanelObjectMapper.MAPPER.writeValueAsString(populatedAttributes);
        } catch (JsonProcessingException e) {
            Assert.fail();
        }
        UiAttributes reanimated = EdPanelObjectMapper.MAPPER.readValue(json, UiAttributes.class);
        Assert.assertEquals(populatedAttributes, reanimated);
    }
}
