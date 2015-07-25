package com.scholarscore.models.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scholarscore.models.query.dimension.StudentDimension;

@Test(groups = { "unit" })
public class QuerySerializationTest {

    @DataProvider
    public Object[][] queriesToSerialize() {
        Query emptyQuery = new Query();
        
        Query fullyPopulatedQuery = new Query();
        //Define aggregate measures
        List<AggregateMeasure> measures = new ArrayList<>();
        measures.add(new AggregateMeasure(Measure.DEMERITS, AggregateFunction.SUM));
        measures.add(new AggregateMeasure(Measure.MERITS, AggregateFunction.AVERAGE));
        fullyPopulatedQuery.setAggregateMeasures(measures);
        
        //No date dimension for this query
        fullyPopulatedQuery.addField(new DimensionField(Dimension.STUDENT, StudentDimension.AGE));
        fullyPopulatedQuery.addField(new DimensionField(Dimension.STUDENT, StudentDimension.ETHNICITY));
        fullyPopulatedQuery.addField(new DimensionField(Dimension.STUDENT, StudentDimension.HOME_ADDRESS));
        
        return new Object[][] {
                { "empty query", emptyQuery, true },
                { "fully populated query", fullyPopulatedQuery, true },
        };
    }
    
    @Test(dataProvider = "queriesToSerialize")
    public void testJacksonSerializationAndDeserialization(String msg, Query q, boolean isValid) {
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writeValueAsString(q);
        } catch (JsonProcessingException e) {
            Assert.fail("Failed to produce a JSON string for Query object: " + msg);
        }
        Query reanimatedQuery = null;
        try {
            reanimatedQuery = mapper.readValue(json, Query.class);
        } catch (IOException e) {
            Assert.fail("Failed to rehydrate a Query instance from JSON for case: " + msg);
        }
        Assert.assertNotNull(reanimatedQuery);
        try {
            Assert.assertEquals(mapper.writeValueAsString(reanimatedQuery), json, "Unexpedctedly unequal Query instances for case: " + msg);
        } catch (JsonProcessingException e) {
            Assert.fail("Failed to produce JSON from the reanimated Query object" + e.getMessage());
        }
    }
    
    @Test(dataProvider = "queriesToSerialize")
    public void testIsValid(String msg, Query q, boolean isValid) {
        Assert.assertEquals(q.isValid(), isValid, "Unexpected valid state of query: " + msg);
    }
}
