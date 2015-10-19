package com.scholarscore.models;

import org.apache.commons.lang3.RandomUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * GradeFormulaBuilderUnitTest tests that we can build equivalent GradeFormula objects using setters and builders
 * Created by cschneider on 10/11/15.
 */
@Test
public class GradeFormulaBuilderUnitTest extends AbstractBuilderUnitTest<GradeFormula>{

    @DataProvider
    @Override
    public Object[][] builderProvider() {
        GradeFormula emptyGradeFormula = new GradeFormula();
        GradeFormula emptyGradeFormulaByBuilder = new GradeFormula.GradeFormulaBuilder().build();

        Map<AssignmentType, Integer> assignmentTypeWeights = new HashMap<>();

        for(AssignmentType type : AssignmentType.values()){
            assignmentTypeWeights.put(type, RandomUtils.nextInt(0, Integer.MAX_VALUE));
        }

        GradeFormula fullGradeFormula = new GradeFormula();

        fullGradeFormula.setAssignmentTypeWeights(assignmentTypeWeights);

        GradeFormula fullGradeFormulaBuilder = new GradeFormula.GradeFormulaBuilder().
                withAssignmentTypeWeights(assignmentTypeWeights).
                build();

        GradeFormula.GradeFormulaBuilder fullGradeFormulaOneByOneBuilder = new GradeFormula.GradeFormulaBuilder();

        for(Map.Entry<AssignmentType, Integer> weight : assignmentTypeWeights.entrySet()){
            fullGradeFormulaOneByOneBuilder.withAssignmentTypeWeight(weight.getKey(), weight.getValue());
        }

        GradeFormula oneByOneBuilder = fullGradeFormulaOneByOneBuilder.build();

        return new Object[][]{
                {"Empty grade formula", emptyGradeFormulaByBuilder, emptyGradeFormula},
                {"Full grade formula", fullGradeFormulaBuilder, fullGradeFormula},
                {"Full grade formula built one entry at a time", oneByOneBuilder, fullGradeFormula}

        };
    }
}
