package com.scholarscore.api.persistence.mysql.querygenerator;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scholarscore.models.query.AggregateFunction;
import com.scholarscore.models.query.AggregateMeasure;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.DimensionField;
import com.scholarscore.models.query.Measure;
import com.scholarscore.models.query.Query;
import com.scholarscore.models.query.dimension.SchoolDimension;
import com.scholarscore.models.query.dimension.SectionDimension;
import com.scholarscore.models.query.dimension.StudentDimension;
import com.scholarscore.models.query.expressions.Expression;
import com.scholarscore.models.query.expressions.operands.DateOperand;
import com.scholarscore.models.query.expressions.operands.DimensionOperand;
import com.scholarscore.models.query.expressions.operands.NumericOperand;
import com.scholarscore.models.query.expressions.operators.BinaryOperator;
import com.scholarscore.models.query.expressions.operators.ComparisonOperator;

@Test(groups = { "unit" })
public class QuerySqlGeneratorUnitTest {

    @DataProvider
    public Object[][] queriesProvider() {
        Query courseGradeQuery = new Query();
        //Define aggregate measures
        List<AggregateMeasure> measures = new ArrayList<>();
        measures.add(new AggregateMeasure(Measure.COURSE_GRADE, AggregateFunction.SUM));
        courseGradeQuery.setAggregateMeasures(measures);
        //No date dimension for this query
        courseGradeQuery.addField(new DimensionField(Dimension.STUDENT, StudentDimension.AGE));
        courseGradeQuery.addField(new DimensionField(Dimension.STUDENT, StudentDimension.ETHNICITY));
        courseGradeQuery.addField(new DimensionField(Dimension.SCHOOL, SchoolDimension.ADDRESS));
        //Create expression
        Expression whereClause = new Expression();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = dateFormat.parse("01-09-2014");
            date2 = dateFormat.parse("01-09-2015");
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Expression minBound = new Expression(
                new DateOperand(date1), 
                ComparisonOperator.GREATER_THAN_OR_EQUAL, 
                new DimensionOperand(new DimensionField(Dimension.SECTION, SectionDimension.START_DATE)));
        Expression maxBound = new Expression(
                new DateOperand(date2), 
                ComparisonOperator.LESS_THAN_OR_EQUAL, 
                new DimensionOperand(new DimensionField(Dimension.SECTION, SectionDimension.START_DATE)));
        whereClause.setLeftHandSide(minBound);
        whereClause.setOperator(BinaryOperator.AND);
        whereClause.setRightHandSide(maxBound);
        courseGradeQuery.setFilter(whereClause);   
        String courseGradeQuerySql = "SELECT student.birth_date, student.federal_ethnicity, school.school_address, SUM(student_section_grade.grade) "
                + "FROM student "
                + "LEFT OUTER JOIN student_section_grade ON student.student_id = student_section_grade.student_fk "
                + "LEFT OUTER JOIN section ON section.section_id = student_section_grade.section_fk "
                + "LEFT OUTER JOIN school ON school.school_id = student.school_fk "
                + "WHERE  ( ( '2014-09-01 00:00:00.0'  >=  section.section_start_date )  "
                + "AND  ( '2015-09-01 00:00:00.0'  <=  section.section_start_date ) ) "
                + "GROUP BY student.birth_date, student.federal_ethnicity, school.school_address";
       
        Query assignmentGradesQuery  = new Query();
        ArrayList<AggregateMeasure> assginmentMeasures = new ArrayList<>();
        assginmentMeasures.add(new AggregateMeasure(Measure.ASSIGNMENT_GRADE, AggregateFunction.AVG));
        assignmentGradesQuery.setAggregateMeasures(assginmentMeasures);
        assignmentGradesQuery.addField(new DimensionField(Dimension.STUDENT, StudentDimension.NAME));
        Expression assignmentWhereClause = new Expression(
                new DimensionOperand(new DimensionField(Dimension.SECTION, SectionDimension.ID)), 
                ComparisonOperator.EQUAL, 
                new NumericOperand(4));
        assignmentGradesQuery.setFilter(assignmentWhereClause);
        String assignmentGradesQuerySql = "SELECT student.student_name, AVERAGE(student_assignment.awarded_points / assignment.available_points) "
                + "FROM student "
                + "LEFT OUTER JOIN student_assignment ON student.student_id = student_assignment.student_fk "
                + "LEFT OUTER JOIN assignment ON student_assignment.assignment_fk = assignment.assignment_id "
                + "LEFT OUTER JOIN section ON section.section_id = student_assignment.section_fk "
                + "WHERE  ( section.section_id  =  4 ) "
                + "GROUP BY student.student_name";
       
        Query homeworkCompletionQuery  = new Query();
        ArrayList<AggregateMeasure> homeworkMeasures = new ArrayList<>();
        homeworkMeasures.add(new AggregateMeasure(Measure.HW_COMPLETION, AggregateFunction.AVG));
        homeworkMeasures.add(new AggregateMeasure(Measure.ATTENDANCE, AggregateFunction.SUM));
        homeworkCompletionQuery.setAggregateMeasures(homeworkMeasures);
        homeworkCompletionQuery.addField(new DimensionField(Dimension.STUDENT, StudentDimension.ID));
        Expression termClause = new Expression(
                new DimensionOperand(new DimensionField(Dimension.TERM, SectionDimension.ID)), 
                ComparisonOperator.EQUAL, 
                new NumericOperand(1));
        Expression yearClause = new Expression(
                new DimensionOperand(new DimensionField(Dimension.YEAR, SectionDimension.ID)), 
                ComparisonOperator.EQUAL, 
                new NumericOperand(1));
        Expression sectionClause = new Expression(
                new DimensionOperand(new DimensionField(Dimension.SECTION, SectionDimension.ID)), 
                ComparisonOperator.NOT_EQUAL, 
                new NumericOperand(0));
        Expression comb1 = new Expression(termClause, BinaryOperator.AND, yearClause);
        Expression comb2 = new Expression(comb1, BinaryOperator.AND, sectionClause);
        homeworkCompletionQuery.setFilter(comb2);
        
        return new Object[][] {
                { "Course Grade query", courseGradeQuery, courseGradeQuerySql }, 
                { "Assignment Grades query", assignmentGradesQuery, assignmentGradesQuerySql }, 
//                { "Homework query", homeworkCompletionQuery, null }
        };
    }
    
    @Test(dataProvider = "queriesProvider")
    public void toSqlTest(String msg, Query q, String expectedSql) {
        SqlWithParameters sql = null;
        try {
            sql = QuerySqlGenerator.generate(q);
        } catch (SqlGenerationException e) {
            Assert.fail(msg);
        }
        Assert.assertNotNull(sql, msg);
        Assert.assertEquals(sql.getSql(), expectedSql, msg);
    }
}
