package com.scholarscore.models;

import com.scholarscore.models.assignment.AssignmentType;
import com.scholarscore.models.assignment.AttendanceAssignment;
import com.scholarscore.models.assignment.GradedAssignment;
import com.scholarscore.models.assignment.StudentAssignment;
import com.scholarscore.models.gradeformula.GradeFormula;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Test(groups = { "unit" })
public class GradeFormulaTest {

    @DataProvider
    public Object[][] formulasToTest() {
        Set<StudentAssignment> studentAssignments = new HashSet<>();

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(15l);
        for(long i = 1; i <= 10; i++) {
            GradedAssignment sect = new GradedAssignment();
            sect.setAvailablePoints(10L);
            sect.setType(AssignmentType.TEST);
            sect.setUserDefinedType(AssignmentType.TEST.name());
            sect.setDueDate(startDate);
            StudentAssignment stu = new StudentAssignment();
            stu.setAwardedPoints(7D);
            stu.setAssignment(sect);
            stu.setId(i);
            studentAssignments.add(stu);
        }
        for(long i = 11; i <= 25; i++) {
            AttendanceAssignment sect = new AttendanceAssignment();
            sect.setUserDefinedType(AssignmentType.ATTENDANCE.name());
            sect.setAvailablePoints(5L);
            sect.setDueDate(startDate);
            StudentAssignment stu = new StudentAssignment();
            stu.setAssignment(sect);
            stu.setAwardedPoints(5D);
            stu.setId(i);
            studentAssignments.add(stu);
        }
  
        //THE FORMULAS:
        GradeFormula emptyFormula = new GradeFormula();
        emptyFormula.setStartDate(startDate);
        emptyFormula.setEndDate(endDate);
        //The no formula just takes all the awarded points and divides them by all available points;
        Double noFormulaGrade = ((15D*5D) + (7D*10D)) / (75 + 100);

        GradeFormula validFormula = new GradeFormula();
        validFormula.setStartDate(startDate);
        validFormula.setEndDate(endDate);
        Map<String, Double> assignmentTypeWeights = new HashMap<>();
        assignmentTypeWeights.put(AssignmentType.ATTENDANCE.name(), 15D);
        assignmentTypeWeights.put(AssignmentType.TEST.name(), 85D);
        validFormula.setAssignmentTypeWeights(assignmentTypeWeights);
        //The formula is weighted to allow attendance to be 15% of the grade and graded work 85%:
        Double weightedFormulaGrade = ((.7*85)+15) / 100D;

        GradeFormula invalidFormula = new GradeFormula();
        invalidFormula.setStartDate(startDate);
        invalidFormula.setEndDate(endDate);
        Map<String, Double> invalidAssignmentWeights = new HashMap<>();
        invalidAssignmentWeights.put(AssignmentType.ATTENDANCE.name(), 15D);
        invalidAssignmentWeights.put(AssignmentType.TEST.name(), 90D);
        invalidFormula.setAssignmentTypeWeights(invalidAssignmentWeights);
        
        return new Object[][] {
                { "Null formula", emptyFormula, studentAssignments, noFormulaGrade },
                { "Weighted formula", validFormula, studentAssignments, weightedFormulaGrade },
//                { "Invalid formula", invalidFormula, studentAssignments, null }
        };
    }
    
    @Test(dataProvider = "formulasToTest")
    public void testFormulas(String msg, GradeFormula formula, Set<StudentAssignment> studAssignments, Double expectedGrade) {
        Assert.assertEquals(formula.calculateGrade(studAssignments), expectedGrade, 
                "Unexpected grade calculated for case: " + msg);
    }
}
