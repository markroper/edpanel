package com.scholarscore.models;

import com.scholarscore.models.assignment.AssignmentType;
import com.scholarscore.models.assignment.AttendanceAssignment;
import com.scholarscore.models.assignment.GradedAssignment;
import com.scholarscore.models.assignment.StudentAssignment;
import com.scholarscore.models.gradeformula.GradeFormula;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Test(groups = { "unit" })
public class GradeFormulaTest {

    @DataProvider
    public Object[][] formulasToTest() {
        Set<StudentAssignment> studentAssignments = new HashSet<>();

        for(long i = 1; i <= 10; i++) {
            GradedAssignment sect = new GradedAssignment();
            sect.setAvailablePoints(10L);
            sect.setType(AssignmentType.TEST);
            StudentAssignment stu = new StudentAssignment();
            stu.setAwardedPoints(7D);
            stu.setAssignment(sect);
            stu.setId(i);
            studentAssignments.add(stu);
        }
        for(long i = 11; i <= 25; i++) {
            AttendanceAssignment sect = new AttendanceAssignment();
            sect.setAvailablePoints(5L);
            StudentAssignment stu = new StudentAssignment();
            stu.setAssignment(sect);
            stu.setAwardedPoints(5D);
            stu.setId(i);
            studentAssignments.add(stu);
        }
  
        //THE FORMULAS:
        GradeFormula emptyFormula = new GradeFormula();
        //The no formula just takes all the awarded points and divides them by all available points;
        Double noFormulaGrade = ((70.0*10.0) + (100.0*15.0)) / (25.0 * 100);

        GradeFormula validFormula = new GradeFormula();
        Map<AssignmentType, Double> assignmentTypeWeights = new HashMap<>();
        assignmentTypeWeights.put(AssignmentType.ATTENDANCE, 15D);
        assignmentTypeWeights.put(AssignmentType.TEST, 85D);
        validFormula.setAssignmentTypeWeights(assignmentTypeWeights);
        //The formula is weighted to allow attendance to be 15% of the grade and graded work 85%:
        Double weightedFormulaGrade = (.7*85)+15;

        GradeFormula invalidFormula = new GradeFormula();
        Map<AssignmentType, Double> invalidAssignmentWeights = new HashMap<>();
        invalidAssignmentWeights.put(AssignmentType.ATTENDANCE, 15D);
        invalidAssignmentWeights.put(AssignmentType.TEST, 90D);
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
