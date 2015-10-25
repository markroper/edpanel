package com.scholarscore;

import com.scholarscore.models.WeightedGradable;
import com.scholarscore.models.assignment.GradedAssignment;
import com.scholarscore.models.assignment.StudentAssignment;
import com.scholarscore.util.GradeUtil;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.HashSet;

/**
 * User: jordan
 * Date: 7/3/15
 * Time: 1:23 PM
 */

@Test(groups = { "unit" })
public class GradeUtilTest {

    @DataProvider
    public Object[][] gradablesToTest() {

        Collection<StudentAssignment> studentAssignments = new HashSet<StudentAssignment>();

        for (long i = 0; i < 5; i++) {
            GradedAssignment gradedAssignment = new GradedAssignment();
            gradedAssignment.setAvailablePoints(100L);
            StudentAssignment studentAssignment = new StudentAssignment();
            studentAssignment.setAssignment(gradedAssignment);
            studentAssignment.setAwardedPoints(50D);
            studentAssignment.setId(i);
            studentAssignments.add(studentAssignment);
        }
        
        for (long i = 5; i < 15; i++) {
            GradedAssignment gradedAssignment = new GradedAssignment();
            gradedAssignment.setAvailablePoints(100L);
            StudentAssignment studentAssignment = new StudentAssignment();
            studentAssignment.setAssignment(gradedAssignment);
            studentAssignment.setAwardedPoints(90D);
            studentAssignment.setId(i);
            studentAssignments.add(studentAssignment);
        }

        for (long i = 15; i < 20; i++) {
            GradedAssignment gradedAssignment = new GradedAssignment();
            gradedAssignment.setAvailablePoints(50L);
            StudentAssignment studentAssignment = new StudentAssignment();
            studentAssignment.setAssignment(gradedAssignment);
            studentAssignment.setAwardedPoints(50D);
            studentAssignment.setId(i);
            studentAssignments.add(studentAssignment);
        }
        
        Double expectedAverage =
                ((50.0 / 100.0) * 5.0 + (90.0 / 100.0) * 10.0 + (50.0 / 50.0) * 5)
                / (20.0);

        Collection<WeightedStudentAssignment> weightedAssignments = new HashSet<WeightedStudentAssignment>();

        for (long i = 0; i < 5; i++) {
            GradedAssignment gradedAssignment = new GradedAssignment();
            gradedAssignment.setAvailablePoints(100L);
            WeightedStudentAssignment studentAssignment = new WeightedStudentAssignment();
            studentAssignment.setAssignment(gradedAssignment);
            studentAssignment.setAwardedPoints(50D);
            studentAssignment.setId(i);
            studentAssignment.setWeight(2);
            weightedAssignments.add(studentAssignment);
        }

        for (long i = 5; i < 15; i++) {
            GradedAssignment gradedAssignment = new GradedAssignment();
            gradedAssignment.setAvailablePoints(100L);
            WeightedStudentAssignment studentAssignment = new WeightedStudentAssignment();
            studentAssignment.setAssignment(gradedAssignment);
            studentAssignment.setAwardedPoints(90D);
            studentAssignment.setId(i);
            // default weight is 1
            weightedAssignments.add(studentAssignment);
        }

        for (long i = 15; i < 20; i++) {
            GradedAssignment gradedAssignment = new GradedAssignment();
            gradedAssignment.setAvailablePoints(50L);
            WeightedStudentAssignment studentAssignment = new WeightedStudentAssignment();
            studentAssignment.setAssignment(gradedAssignment);
            studentAssignment.setAwardedPoints(50D);
            studentAssignment.setId(i);
            studentAssignment.setWeight(4);
            weightedAssignments.add(studentAssignment);
        }

        Double expectedWeightedAverage =
                        // 50 score out of 100 possible, 5 assignments, 2 weight
                        (((50.0 / 100.0) * 5.0 * 2.0)
                        // 90 score out of 100 possible, 10 assignments, 1 weight
                        + ((90.0 / 100.0) * 10.0 * 1.0)
                        // 50 score out of 50 possible, 5 assignments, 4 weight
                        + ((50.0 / 50.0) * 5.0 * 4.0))
                                // 5 assignments w/ 2 weight, 10 assignments w/ 1 weight, 5 assignments w/ 4 weight
                        / ((5.0 * 2.0) + (10.0 * 1.0) + (5.0 * 4.0));


        return new Object[][] { 
                { "AverageGrade of StudentAssignments", studentAssignments, expectedAverage},
                { "AverageGrade of Weighted StudentAssignments", weightedAssignments, expectedWeightedAverage}
        };
    }
    
    @Test(dataProvider = "gradablesToTest")
    public void testCalculateAverageGrade(String caseName, Collection<? extends WeightedGradable> gradables, Double expectedScore) {
        Assert.assertEquals(GradeUtil.calculateAverageGrade(gradables), expectedScore, "Unexpected average grade calculated: " + caseName);
    }
    
    @Test(dataProvider = "gradablesToTest")
    public void testCalculateGPA(String caseName, Collection<? extends WeightedGradable> gradables, Double expectedScore) {
        Assert.assertEquals(GradeUtil.calculateGPA(4, gradables), expectedScore *4.0 , "Unexpected average grade calculated: " + caseName);
    }
    
    private class WeightedStudentAssignment extends StudentAssignment {
    
        private int weight = 1;
        
        @Override
        public int getWeight() { 
            return this.weight;
        }
        
        public void setWeight(int weight) {
            this.weight = weight;
        }
    }
    
}
