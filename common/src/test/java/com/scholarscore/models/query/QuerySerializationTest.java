package com.scholarscore.models.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scholarscore.models.School;
import com.scholarscore.models.Student;
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
        List<AggregateMeasure> measures = new ArrayList<>();
        measures.add(new AggregateMeasure(Measure.DEMERITS, AggregateFunction.SUM));
        measures.add(new AggregateMeasure(Measure.MERITS, AggregateFunction.AVERAGE));
        fullyPopulatedQuery.setAggregateMeasures(measures);
        
        //No date dimension for this query
        fullyPopulatedQuery.addField(Student.STUDENT_AGE);
        fullyPopulatedQuery.addField(Student.STUDENT_ETHNICITY);
        fullyPopulatedQuery.addField(School.ADDRESS);
        
        //Create expression
//        Expression whereClause = new Expression();
//        @SuppressWarnings("deprecation")
//        Expression minBound = new Expression(
//                new DateOperand(new Date(2014, 9, 1)), 
//                ComparisonOperator.GREATER_THAN_OR_EQUAL, 
//                new DimensionOperand(Dimension.DATE));
//        Expression maxBound = new Expression(
//                new DateOperand(new Date()), 
//                ComparisonOperator.LESS_THAN_OR_EQUAL, 
//                new DimensionOperand(Dimension.DATE));
//        whereClause.setLeftHandSide(minBound);
//        whereClause.setOperator(BinaryOperator.AND);
//        whereClause.setRightHandSide(maxBound);
//        fullyPopulatedQuery.setFilter(whereClause);
        
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
