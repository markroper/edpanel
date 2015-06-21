package com.scholarscore.models.query;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashSet;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scholarscore.models.query.expressions.Expression;
import com.scholarscore.models.query.expressions.operands.DateOperand;
import com.scholarscore.models.query.expressions.operands.DimensionOperand;
import com.scholarscore.models.query.expressions.operators.BinaryOperator;
import com.scholarscore.models.query.expressions.operators.ComparisonOperator;

@Test(groups = { "unit" })
public class QuerySerializationTest {

    @DataProvider
    public Object[][] queriesToSerialize() {
        Query emptyQuery = new Query();
        
        Query fullyPopulatedQuery = new Query();
        //Define aggregate measures
        LinkedHashSet<AggregateMeasure> measures = new LinkedHashSet<>();
        measures.add(new AggregateMeasure(Measure.DEMERITS, AggregateFunction.SUM));
        measures.add(new AggregateMeasure(Measure.COURSE_GRADE, AggregateFunction.AVERAGE));
        fullyPopulatedQuery.setAggregateMeasures(measures);
        
        //define dimension
        fullyPopulatedQuery.setDimension(Dimension.STUDENT);
        
        //No date dimension for this query
        fullyPopulatedQuery.setDateDimension(null);
        
        //Create expression
        Expression whereClause = new Expression();
        @SuppressWarnings("deprecation")
        Expression minBound = new Expression(
                new DateOperand(new Date(2014, 9, 1)), 
                ComparisonOperator.GREATER_THAN_OR_EQUAL, 
                new DimensionOperand(Dimension.DATE));
        Expression maxBound = new Expression(
                new DateOperand(new Date()), 
                ComparisonOperator.LESS_THAN_OR_EQUAL, 
                new DimensionOperand(Dimension.DATE));
        whereClause.setLeftHandSide(minBound);
        whereClause.setOperator(BinaryOperator.AND);
        whereClause.setRightHandSide(maxBound);
        fullyPopulatedQuery.setFilter(whereClause);
        
        return new Object[][] {
                { "empty query", emptyQuery },
                { "fully populated query", fullyPopulatedQuery },
        };
    }
    
    @Test(dataProvider = "queriesToSerialize")
    public void testJacksonSerializationAndDeserialization(String msg, Query q) {
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
}
