package com.scholarscore.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test(groups = { "unit" })
public class GradeFormulaTest {

    @DataProvider
    public Object[][] formulasToTest() {
        GradedAssignment graded = new GradedAssignment();
        AttendanceAssignment attend = new AttendanceAssignment();
        
        Set<StudentAssignment> studentAssignments = new HashSet<>();
        for(long i = 1; i <= 10; i++) {
            SectionAssignment sect = new SectionAssignment();
            sect.setAvailablePoints(10L);
            sect.setAssignment(graded);
            StudentAssignment stu = new StudentAssignment();
            stu.setAwardedPoints(7L);
            stu.setAssignment(sect);
            stu.setId(i);
            studentAssignments.add(stu);
        }
        for(long i = 11; i <= 25; i++) {
            SectionAssignment sect = new SectionAssignment();
            sect.setAvailablePoints(5L);
            sect.setAssignment(attend);
            StudentAssignment stu = new StudentAssignment();
            stu.setAssignment(sect);
            stu.setAwardedPoints(5L);
            stu.setId(i);
            studentAssignments.add(stu);
        }
  
        //THE FORMULAS:
        GradeFormula emptyFormula = new GradeFormula();
        //The no formula just takes all the awarded points and divides them by all available points;
        Double noFormulaGrade = 145.0/175.0;
        
        GradeFormula validFormula = new GradeFormula();
        Map<AssignmentType, Integer> assignmentTypeWeights = new HashMap<>();
        assignmentTypeWeights.put(AssignmentType.ATTENDANCE, 15);
        assignmentTypeWeights.put(AssignmentType.GRADED, 85);
        validFormula.setAssignmentTypeWeights(assignmentTypeWeights);
        //The formula is weighted to allow attendance to be 15% of the grade and graded work 85%:
        Double weightedFormulaGrade = (.7*85)+15;
        
        GradeFormula invalidFormula = new GradeFormula();
        Map<AssignmentType, Integer> invalidAssignmentWeights = new HashMap<>();
        invalidAssignmentWeights.put(AssignmentType.ATTENDANCE, 15);
        invalidAssignmentWeights.put(AssignmentType.GRADED, 90);
        invalidFormula.setAssignmentTypeWeights(invalidAssignmentWeights);
       
        
        return new Object[][] {
                { "Null formula", emptyFormula, studentAssignments, noFormulaGrade },
                { "Weighted formula", validFormula, studentAssignments, weightedFormulaGrade },
                { "Invalid formula", invalidFormula, studentAssignments, null }
        };
    }
    
    @Test(dataProvider = "formulasToTest")
    public void testFormulas(String msg, GradeFormula formula, Set<StudentAssignment> studAssignments, Double expectedGrade) {
        Assert.assertEquals(formula.calculateGrade(studAssignments), expectedGrade, 
                "Unexpected grade calculated for case: " + msg);
    }
}
