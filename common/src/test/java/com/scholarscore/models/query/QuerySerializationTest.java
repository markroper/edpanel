package com.scholarscore.models.query;

import java.io.IOException;
import java.util.ArrayList;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scholarscore.models.query.dimension.SectionDimension;
import com.scholarscore.models.query.dimension.StudentDimension;
import com.scholarscore.models.query.expressions.Expression;
import com.scholarscore.models.query.expressions.operands.DimensionOperand;
import com.scholarscore.models.query.expressions.operands.NumericOperand;
import com.scholarscore.models.query.expressions.operators.ComparisonOperator;

@Test(groups = { "unit" })
public class QuerySerializationTest {

    @DataProvider
    public Object[][] queriesToSerialize() {
        Query emptyQuery = new Query();
        
        Query assignmentGradesQuery  = new Query();
        ArrayList<AggregateMeasure> assginmentMeasures = new ArrayList<>();
        assginmentMeasures.add(new AggregateMeasure(Measure.ASSIGNMENT_GRADE, AggregateFunction.AVERAGE));
        assignmentGradesQuery.setAggregateMeasures(assginmentMeasures);
        assignmentGradesQuery.addField(new DimensionField(Dimension.STUDENT, StudentDimension.NAME));
        Expression assignmentWhereClause = new Expression(
                new DimensionOperand(new DimensionField(Dimension.SECTION, SectionDimension.ID)), 
                ComparisonOperator.EQUAL, 
                new NumericOperand(4));
        assignmentGradesQuery.setFilter(assignmentWhereClause);
        
        return new Object[][] {
                { "empty query", emptyQuery, true },
                { "fully populated query", assignmentGradesQuery, true },
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
