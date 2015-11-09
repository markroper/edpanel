package com.scholarscore.models;

import com.scholarscore.models.assignment.AssignmentType;
import com.scholarscore.models.gradeformula.AssignmentGradeFormula;
import org.apache.commons.lang3.RandomUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * GradeFormulaBuilderUnitTest tests that we can build equivalent AssignmentGradeFormula objects using setters and builders
 * Created by cschneider on 10/11/15.
 */
@Test(groups = { "unit" })
public class GradeFormulaBuilderUnitTest extends AbstractBuilderUnitTest<AssignmentGradeFormula>{

    @DataProvider
    @Override
    public Object[][] builderProvider() {
        AssignmentGradeFormula emptyGradeFormula = new AssignmentGradeFormula();
        AssignmentGradeFormula emptyGradeFormulaByBuilder = new AssignmentGradeFormula.GradeFormulaBuilder().build();

        Map<AssignmentType, Integer> assignmentTypeWeights = new HashMap<>();

        for(AssignmentType type : AssignmentType.values()){
            assignmentTypeWeights.put(type, RandomUtils.nextInt(0, Integer.MAX_VALUE));
        }

        AssignmentGradeFormula fullGradeFormula = new AssignmentGradeFormula();

        fullGradeFormula.setAssignmentTypeWeights(assignmentTypeWeights);

        AssignmentGradeFormula fullGradeFormulaBuilder = new AssignmentGradeFormula.GradeFormulaBuilder().
                withAssignmentTypeWeights(assignmentTypeWeights).
                build();

        AssignmentGradeFormula.GradeFormulaBuilder fullGradeFormulaOneByOneBuilder = new AssignmentGradeFormula.GradeFormulaBuilder();

        for(Map.Entry<AssignmentType, Integer> weight : assignmentTypeWeights.entrySet()){
            fullGradeFormulaOneByOneBuilder.withAssignmentTypeWeight(weight.getKey(), weight.getValue());
        }

        AssignmentGradeFormula oneByOneBuilder = fullGradeFormulaOneByOneBuilder.build();

        return new Object[][]{
                {"Empty grade formula", emptyGradeFormulaByBuilder, emptyGradeFormula},
                {"Full grade formula", fullGradeFormulaBuilder, fullGradeFormula},
                {"Full grade formula built one entry at a time", oneByOneBuilder, fullGradeFormula}

        };
    }
}
