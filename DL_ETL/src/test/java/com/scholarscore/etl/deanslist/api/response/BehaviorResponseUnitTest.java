package com.scholarscore.etl.deanslist.api.response;

import com.scholarscore.etl.deanslist.api.response.BehaviorResponse;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * User: jordan
 * Date: 9/17/15
 * Time: 10:56 AM
 */
@Test(groups = { "unit" })
public class BehaviorResponseUnitTest {
    
    public void testBehaviorResponse(String msg, BehaviorResponse behaviorResponse) {
        
    }
    
    @DataProvider
    public Object[] validBehaviorResponseProvider() {
        BehaviorResponse validResponse = new BehaviorResponse();
        validResponse.data;
        
        return new Object[] { 
        
        };
    }
    
    public Object[] invalidBehaviorResponseProvider() {

    }
    
    /* @DataProvider
    public Object[][] createQueryProvider() {
        Query emptyQuery = new Query();
        
        Query assignmentGradesQuery  = new Query();
        ArrayList<AggregateMeasure> assignmentMeasures = new ArrayList<>();
        assignmentMeasures.add(new AggregateMeasure(Measure.ASSIGNMENT_GRADE, AggregateFunction.AVG));
        assignmentGradesQuery.setAggregateMeasures(assignmentMeasures);
        assignmentGradesQuery.addField(new DimensionField(Dimension.STUDENT, StudentDimension.NAME));
        Expression assignmentWhereClause = new Expression(
                new DimensionOperand(new DimensionField(Dimension.SECTION, SectionDimension.ID)), 
                ComparisonOperator.EQUAL, 
                new NumericOperand(4));
        assignmentGradesQuery.setFilter(assignmentWhereClause);
       
        return new Object[][] {
                { "Empty query", emptyQuery },
                { "Populated query", assignmentGradesQuery }
        };
    }
    
    @Test(dataProvider = "createQueryProvider")
    public void createQueryTest(String msg, Query query) {
        queryValidatingExecutor.create(school.getId(), query, msg);
        numberOfItemsCreated++;
    } 
    * * */
}
