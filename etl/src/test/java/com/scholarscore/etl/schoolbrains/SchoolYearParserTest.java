package com.scholarscore.etl.schoolbrains;

import com.scholarscore.etl.schoolbrains.parser.SchoolYearParser;
import com.scholarscore.models.SchoolYear;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Set;

/**
 * Created by markroper on 4/14/16.
 */
@Test(groups = {"unit"})
public class SchoolYearParserTest {

    @Test
    public void parserTest() {
        File input = new File(getClass().getClassLoader().getResource("schoolbrains/EdPanelSections.csv").getFile());
        SchoolYearParser parser = new SchoolYearParser(input);
        Set<SchoolYear> results = parser.parse();
        Assert.assertEquals(results.size(), 1, "Unexpected number of Students parsed from schoolbrains enrollment CSV");
    }
}
