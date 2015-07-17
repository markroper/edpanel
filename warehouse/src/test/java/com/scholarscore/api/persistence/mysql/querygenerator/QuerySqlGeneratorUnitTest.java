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

import com.scholarscore.models.School;
import com.scholarscore.models.Section;
import com.scholarscore.models.Student;
import com.scholarscore.models.query.AggregateFunction;
import com.scholarscore.models.query.AggregateMeasure;
import com.scholarscore.models.query.Measure;
import com.scholarscore.models.query.Query;
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
        courseGradeQuery.addField(Student.STUDENT_AGE);
        courseGradeQuery.addField(Student.STUDENT_ETHNICITY);
        courseGradeQuery.addField(School.ADDRESS);
        
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
                new DimensionOperand(Section.START_DATE));
        Expression maxBound = new Expression(
                new DateOperand(date2), 
                ComparisonOperator.LESS_THAN_OR_EQUAL, 
                new DimensionOperand(Section.START_DATE));
        whereClause.setLeftHandSide(minBound);
        whereClause.setOperator(BinaryOperator.AND);
        whereClause.setRightHandSide(maxBound);
        courseGradeQuery.setFilter(whereClause);
        
        String courseGradeQuerySql = "SELECT student.birth_date, student.federal_ethnicity, school.school_address, SUM(student_section_grade.grade) "
                + "FROM student LEFT OUTER JOIN student_section_grade ON student.student_id = student_section_grade.student_fk "
                + "LEFT OUTER JOIN section ON section.section_id = student.section_fk "
                + "LEFT OUTER JOIN school ON school.school_id = student.school_fk "
                + "WHERE  ( ( '2014-09-01 00:00:00.0'  >=  section.section_start_date )  "
                + "AND  ( '2015-09-01 00:00:00.0'  <=  section.section_start_date ) ) "
                + "GROUP BY student.birth_date, student.federal_ethnicity, school.school_address";
        
        Query assignmentGradesQuery  = new Query();
        ArrayList<AggregateMeasure> assginmentMeasures = new ArrayList<>();
        assginmentMeasures.add(new AggregateMeasure(Measure.ASSIGNMENT_GRADE, AggregateFunction.AVERAGE));
        assignmentGradesQuery.setAggregateMeasures(assginmentMeasures);
        assignmentGradesQuery.addField(Student.STUDENT_NAME);
        Expression assignmentWhereClause = new Expression(
                new DimensionOperand(Section.ID), 
                ComparisonOperator.EQUAL, 
                new NumericOperand(4));
        assignmentGradesQuery.setFilter(assignmentWhereClause);
        String assignmentGradesQuerySql = "SELECT student.student_name, AVERAGE(student_assignment.awarded_points / assignment.available_points) "
                + "FROM student LEFT OUTER JOIN student_assignment ON student.student_id = student_assignment.student_fk "
                + "LEFT OUTER JOIN assignment ON student_assignment.assignment_fk = assignment.assignment_id "
                + "LEFT OUTER JOIN section ON section.section_id = student_assignment.section_fk "
                + "WHERE  ( section.course_id  =  4 ) "
                + "GROUP BY student.student_name";
        
        return new Object[][] {
                { "Course Grade query", courseGradeQuery, courseGradeQuerySql }, 
                { "Assignment Grades query", assignmentGradesQuery, assignmentGradesQuerySql }, 
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
