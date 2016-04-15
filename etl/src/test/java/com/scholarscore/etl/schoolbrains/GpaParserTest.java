package com.scholarscore.etl.schoolbrains;

import com.scholarscore.models.gpa.Gpa;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Set;

/**
 * Created by markroper on 4/14/16.
 */
@Test(groups = {"unit"})
public class GpaParserTest {

    @Test
    public void parserTest() {
        File input = new File(getClass().getClassLoader().getResource("schoolbrains/EdPanelStudents.csv").getFile());
        GpaParser parser = new GpaParser(input);
        Set<Gpa> results = parser.parse();
        Assert.assertEquals(results.size(), 515, "Unexpected number of Students parsed from schoolbrains CSV generated");
    }
}
