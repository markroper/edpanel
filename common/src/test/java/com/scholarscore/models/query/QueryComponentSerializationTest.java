package com.scholarscore.models.query;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Test(groups = { "unit" })
public class QueryComponentSerializationTest {
    public void testJacksonSerializationAndDeserialization() {
        QueryComponents qc = new QueryComponents();
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        String expectedJson = "{\"avalailableDimensions\":[{\"fields\":[\"ID\",\"Name\"],\"type\":\"COURSE\",\"parentDimensions\":[\"SCHOOL\",\"SUBJECT_AREA\"]},"
                + "{\"fields\":[\"ID\",\"Name\"],\"type\":\"GRADE_LEVEL\",\"parentDimensions\":[\"SCHOOL\"]},"
                + "{\"fields\":[\"ID\",\"Name\",\"Address\"],\"type\":\"SCHOOL\",\"parentDimensions\":null},"
                + "{\"fields\":[\"ID\",\"Name\",\"Start Date\",\"End Date\"],\"type\":\"YEAR\",\"parentDimensions\":[\"SCHOOL\"]},"
                + "{\"fields\":[\"ID\",\"Name\",\"Start Date\",\"End Date\",\"Teacher\",\"Grade Formula\",\"Room\"],\"type\":\"YEAR\",\"parentDimensions\":[\"COURSE\",\"TERM\"]},{\"fields\":[\"ID\",\"Name\",\"Gender\",\"Age\",\"Ethnicity\",\"Race\",\"City of Residence\",\"Home Address\",\"Mailing Address\",\"Proj. Graduation Year\"],\"type\":\"STUDENT\",\"parentDimensions\":[\"SCHOOL\",\"GRADE_LEVEL\"]},"
                + "{\"fields\":[\"ID\",\"Name\"],\"type\":\"SUBJECT_AREA\",\"parentDimensions\":[\"SCHOOL\"]},"
                + "{\"fields\":[\"ID\",\"Name\",\"Email\"],\"type\":\"TEACHER\",\"parentDimensions\":[\"SCHOOL\"]},"
                + "{\"fields\":[\"ID\",\"Name\",\"Start Date\",\"End Date\"],\"type\":\"TERM\",\"parentDimensions\":[\"YEAR\"]}],"
                + "\"availableMeasures\":[{\"compatibleMeasures\":[],\"compatibleDimensions\":[\"STUDENT\",\"SECTION\"],\"measure\":\"ASSIGNMENT_GRADE\"},"
                + "{\"compatibleMeasures\":[],\"compatibleDimensions\":[\"STUDENT\",\"SECTION\"],\"measure\":\"COURSE_GRADE\"},"
                + "{\"compatibleMeasures\":[],\"compatibleDimensions\":[\"TERM\",\"YEAR\",\"STUDENT\",\"SCHOOL\",\"GRADE_LEVEL\"],\"measure\":\"GPA\"},"
                + "{\"compatibleMeasures\":[],\"compatibleDimensions\":[\"TERM\",\"STUDENT\",\"SCHOOL\",\"SECTION\",\"YEAR\",\"GRADE_LEVEL\",\"TEACHER\"],\"measure\":\"HW_COMPLETION\"}]}";
        try {
            json = mapper.writeValueAsString(qc);
        } catch (JsonProcessingException e) {
            Assert.fail("Failed to produce a JSON string for QueryComponents object");
        }
        Assert.assertNotNull(json);
        Assert.assertEquals(json, expectedJson);
    }
}
