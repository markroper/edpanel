package com.scholarscore.models.gradeformula;

import com.scholarscore.models.assignment.StudentAssignment;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by markroper on 11/8/15.
 */
public class TermGradeFormulas extends HashSet<AssignmentGradeFormula> {
    public TermGradeFormulas() {
        super();
    }

    public TermGradeFormulas(TermGradeFormulas formulas) {
        super(formulas);
    }

    //Returns the grade, calculating each term's grade by weighting assignments and then
    //Weighting each sections grade in the final calculation according to that term's section grade weight
    public Double calculateGrade(Set<StudentAssignment> studentAssignments) {
        Double numerator = 0D;
        Double denominator = 0D;
        for(AssignmentGradeFormula formula : this) {
            Double weight = formula.getSectionGradeWeight();
            if(null == weight) {
                weight = 1D;
            }
            numerator += formula.calculateGrade(studentAssignments) * weight;
            denominator += weight;
        }
        if(denominator.equals(0D)) {
            return denominator;
        } else {
            return numerator / denominator;
        }
    }
}
