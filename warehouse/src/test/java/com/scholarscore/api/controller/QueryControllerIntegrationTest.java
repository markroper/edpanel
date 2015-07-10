package com.scholarscore.api.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.School;
import com.scholarscore.models.Student;
import com.scholarscore.models.query.AggregateFunction;
import com.scholarscore.models.query.AggregateMeasure;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.Measure;
import com.scholarscore.models.query.Query;
import com.scholarscore.models.query.expressions.Expression;
import com.scholarscore.models.query.expressions.operands.DateOperand;
import com.scholarscore.models.query.expressions.operands.DimensionOperand;
import com.scholarscore.models.query.expressions.operators.BinaryOperator;
import com.scholarscore.models.query.expressions.operators.ComparisonOperator;

@Test(groups = { "integration" })
public class QueryControllerIntegrationTest extends IntegrationBase {
    private int numberOfItemsCreated = 0;
    private School school;
    
    @BeforeClass
    public void init() {
        authenticate();
        numberOfItemsCreated = 0;
        school = new School();
        school.setName(localeServiceUtil.generateName());
        school = schoolValidatingExecutor.create(school, "Create base school");
    }
    
    //Positive test cases
    @DataProvider
    public Object[][] createQueryProvider() {
        Query emptyQuery = new Query();
        
        Query fullyPopulatedQuery = new Query();
        //Define aggregate measures
        List<AggregateMeasure> measures = new ArrayList<>();
        measures.add(new AggregateMeasure(Measure.DEMERITS, AggregateFunction.SUM));
        measures.add(new AggregateMeasure(Measure.COURSE_GRADE, AggregateFunction.AVERAGE));
        fullyPopulatedQuery.setAggregateMeasures(measures);
        
        //No date dimension for this query
        fullyPopulatedQuery.addField(Student.STUDENT_AGE);
        fullyPopulatedQuery.addField(Student.STUDENT_ETHNICITY);
        fullyPopulatedQuery.addField(School.ADDRESS);
        
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
                { "Empty query", emptyQuery },
                { "Populated query", fullyPopulatedQuery }
        };
    }
    
    @Test(dataProvider = "createQueryProvider")
    public void createQueryTest(String msg, Query query) {
        queryValidatingExecutor.create(school.getId(), query, msg);
        numberOfItemsCreated++;
    }
    
    @Test(dataProvider = "createQueryProvider")
    public void deleteQueryTest(String msg, Query query) {
        Query createdQuery = queryValidatingExecutor.create(school.getId(), query, msg);
        queryValidatingExecutor.delete(school.getId(), createdQuery.getId(), msg);
    }
    
//    @Test(dataProvider = "createQueryProvider")
//    public void replaceQueryTest(String msg, Query query) {
//        Query createdQuery = queryValidatingExecutor.create(school.getId(), query, msg);
//        queryValidatingExecutor.replace(school.getId(), createdQuery.getId(), new Query(), msg);
//        numberOfItemsCreated++;
//    }
//    
//    @Test(dataProvider = "createQueryProvider")
//    public void updateQueryTest(String msg, Query query) {
//        Query createdQuery = queryValidatingExecutor.create(school.getId(), query, msg);
//        Query updatedQuery = new Query();
//        updatedQuery.setName(localeServiceUtil.generateName());
//        //PATCH the existing record with a new name.
//        queryValidatingExecutor.update(school.getId(), createdQuery.getId(), updatedQuery, msg);
//        numberOfItemsCreated++;
//    }
    
    @Test
    public void getAllItems() {
        queryValidatingExecutor.getAll(school.getId(), "Get all records created so far", numberOfItemsCreated++);
    }
    
    //Negative test cases
    @DataProvider
    public Object[][] createQueryNegativeProvider() {
        Query gradedQueryNameTooLong = new Query();
        gradedQueryNameTooLong.setName(localeServiceUtil.generateName(257));
        
        return new Object[][] {
                { "Query with name exceeding 256 char limit", gradedQueryNameTooLong, HttpStatus.BAD_REQUEST }
        };
    }
    
    @Test(dataProvider = "createQueryNegativeProvider")
    public void createQueryNegativeTest(String msg, Query query, HttpStatus expectedStatus) {
        queryValidatingExecutor.createNegative(school.getId(), query, expectedStatus, msg);
    }
    
//    @Test(dataProvider = "createQueryNegativeProvider")
//    public void replaceQueryNegativeTest(String msg, Query query, HttpStatus expectedStatus) {
//        Query created = queryValidatingExecutor.create(school.getId(), new Query(), msg);
//        queryValidatingExecutor.replaceNegative(school.getId(), created.getId(), query, expectedStatus, msg);
//    }

}