package com.scholarscore.etl.powerschool.api.model;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Testing the regex expressions used to conver the PsSchool Cycle string into the useful map
 * Created by cwallace on 12/31/15.
 */
@Test(groups = { "unit" })
public class PsSectionUnitTest {

    @DataProvider(name = "expressionDataProvider")
    public Object[][] expressionDataMethod() {

        //Test case one
        String inputOne = "2(A-D) 3-4(E) 6(C-D) 7(B)";
        Map<String, ArrayList<Long>> expectedOne = new HashMap<>();
        ArrayList<Long> oneListA = new ArrayList<>();
        ArrayList<Long> oneListB = new ArrayList<>();
        ArrayList<Long> oneListC = new ArrayList<>();
        ArrayList<Long> oneListD = new ArrayList<>();
        ArrayList<Long> oneListE = new ArrayList<>();
        oneListA.add(2L);
        oneListB.add(2L);
        oneListB.add(7L);
        oneListC.add(2L);
        oneListC.add(6L);
        oneListD.add(2L);
        oneListD.add(6L);
        oneListE.add(3L);
        oneListE.add(4L);
        expectedOne.put("A",oneListA);
        expectedOne.put("B",oneListB);
        expectedOne.put("C",oneListC);
        expectedOne.put("D",oneListD);
        expectedOne.put("E",oneListE);

        //Test case two
        String inputTwo = "2(A-C,E) 5-7(D)";
        Map<String, ArrayList<Long>> expectedTwo = new HashMap<>();
        ArrayList<Long> twoListA = new ArrayList<>();
        ArrayList<Long> twoListB = new ArrayList<>();
        ArrayList<Long> twoListC = new ArrayList<>();
        ArrayList<Long> twoListD = new ArrayList<>();
        ArrayList<Long> twoListE = new ArrayList<>();
        twoListA.add(2L);
        twoListB.add(2L);
        twoListC.add(2L);
        twoListD.add(5L);
        twoListD.add(6L);
        twoListD.add(7L);
        twoListE.add(2L);
        expectedTwo.put("A",twoListA);
        expectedTwo.put("B",twoListB);
        expectedTwo.put("C",twoListC);
        expectedTwo.put("D",twoListD);
        expectedTwo.put("E",twoListE);

        //Test case three
        String inputThr = "2(A,C-E,G) 5-7(B,D,F)";
        Map<String, ArrayList<Long>> expectedThr = new HashMap<>();
        ArrayList<Long> thrListA = new ArrayList<>();
        ArrayList<Long> thrListB = new ArrayList<>();
        ArrayList<Long> thrListC = new ArrayList<>();
        ArrayList<Long> thrListD = new ArrayList<>();
        ArrayList<Long> thrListE = new ArrayList<>();
        ArrayList<Long> thrListF = new ArrayList<>();
        ArrayList<Long> thrListG = new ArrayList<>();
        thrListA.add(2L);
        thrListB.add(5L);
        thrListB.add(6L);
        thrListB.add(7L);
        thrListC.add(2L);
        thrListD.add(2L);
        thrListD.add(5L);
        thrListD.add(6L);
        thrListD.add(7L);
        thrListE.add(2L);
        thrListF.add(5L);
        thrListF.add(6L);
        thrListF.add(7L);
        thrListG.add(2L);
        expectedThr.put("A",thrListA);
        expectedThr.put("B",thrListB);
        expectedThr.put("C",thrListC);
        expectedThr.put("D",thrListD);
        expectedThr.put("E",thrListE);
        expectedThr.put("F",thrListF);
        expectedThr.put("G",thrListG);

        //Test case four
        String inputFou = "2,5(A)";
        Map<String, ArrayList<Long>> expectedFou = new HashMap<>();
        ArrayList<Long> fouListA = new ArrayList<>();
        fouListA.add(2L);
        fouListA.add(5L);

        expectedFou.put("A",fouListA);


        return new Object[][] {
                {inputOne, expectedOne, "Failed on the first test case"},
                {inputTwo, expectedTwo, "Failed on the second test case"},
                {inputThr, expectedThr, "Failed on the third test case"},
                {inputFou, expectedFou, "Failed on the fourth test case"}
        };
    }

    @Test(dataProvider = "expressionDataProvider")
    public void expressionUnitTest(String input, Map<String, ArrayList<Long>> expected, String message) {
        Map<String, ArrayList<Long>> results = PsSection.evaluateExpression(input);
        Assert.assertEquals(results, expected, message);
    }
}
