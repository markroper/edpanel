package com.scholarscore.models.query;

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Test(groups = { "unit" })
public class QueryComponentSerializationTest {
    public void testJacksonSerializationAndDeserialization() {
        QueryComponents qc = new QueryComponents();
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        String expectedJson = "{\"avalailableDimensions\":"
                + "["
                + "{\"name\":\"Course\",\"fields\":[\"ID\",\"Name\"],\"type\":\"COURSE\",\"parentDimensions\":[\"SCHOOL\",\"SUBJECT_AREA\"]},"
                + "{\"name\":\"Grade Level\",\"fields\":[\"ID\",\"Name\"],\"type\":\"GRADE_LEVEL\",\"parentDimensions\":[\"SCHOOL\"]},"
                + "{\"name\":\"School\",\"fields\":[\"ID\",\"Name\",\"Address\"],\"type\":\"SCHOOL\",\"parentDimensions\":null},"
                + "{\"name\":\"Year\",\"fields\":[\"ID\",\"Name\",\"Start Date\",\"End Date\"],\"type\":\"YEAR\",\"parentDimensions\":[\"SCHOOL\"]},"
                + "{\"name\":\"Section\",\"fields\":[\"ID\",\"Name\",\"Start Date\",\"End Date\",\"Teacher\",\"Grade Formula\",\"Room\"],\"type\":\"YEAR\",\"parentDimensions\":[\"COURSE\",\"TERM\"]},"
                + "{\"name\":\"Student\",\"fields\":[\"ID\",\"Name\",\"Gender\",\"Age\",\"Ethnicity\",\"Race\",\"City of Residence\",\"Home Address\",\"Mailing Address\",\"Proj. Graduation Year\"],\"type\":\"STUDENT\",\"parentDimensions\":[\"SCHOOL\",\"GRADE_LEVEL\"]},"
                + "{\"name\":\"Subject\",\"fields\":[\"ID\",\"Name\"],\"type\":\"SUBJECT_AREA\",\"parentDimensions\":[\"SCHOOL\"]},"
                + "{\"name\":\"Teacher\",\"fields\":[\"ID\",\"Name\",\"Email\"],\"type\":\"TEACHER\",\"parentDimensions\":[\"SCHOOL\"]},"
                + "{\"name\":\"Term\",\"fields\":[\"ID\",\"Name\",\"Start Date\",\"End Date\"],\"type\":\"TERM\",\"parentDimensions\":[\"YEAR\"]}"
                + "],"
                + "\"availableMeasures\":["
                + "{\"compatibleMeasures\":[],\"compatibleDimensions\":[\"STUDENT\",\"SECTION\"],\"name\":\"Assignment Grade\",\"measure\":\"ASSIGNMENT_GRADE\"},"
                + "{\"compatibleMeasures\":[],\"compatibleDimensions\":[\"STUDENT\",\"SECTION\"],\"name\":\"Course Grade\",\"measure\":\"COURSE_GRADE\"},"
                + "{\"compatibleMeasures\":[],\"compatibleDimensions\":[\"TERM\",\"YEAR\",\"STUDENT\",\"SCHOOL\",\"GRADE_LEVEL\"],\"name\":\"GPA\",\"measure\":\"GPA\"},"
                + "{\"compatibleMeasures\":[],\"compatibleDimensions\":[\"TERM\",\"STUDENT\",\"SCHOOL\",\"SECTION\",\"YEAR\",\"GRADE_LEVEL\",\"TEACHER\"],\"name\":\"Homework Completion\",\"measure\":\"HW_COMPLETION\"}]}";
        try {
            json = mapper.writeValueAsString(qc);
        } catch (JsonProcessingException e) {
            Assert.fail("Failed to produce a JSON string for QueryComponents object");
        }
        Assert.assertNotNull(json);

        try {
            JsonNode actualObj = mapper.readTree(json);
            JsonNode expectedObj = mapper.readTree(expectedJson);
            Assert.assertEquals(actualObj, expectedObj);
        } catch (IOException e) {
            Assert.fail("Unable to parse JSON strings");
        }
    }
}
