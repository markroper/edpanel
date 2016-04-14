package com.scholarscore.etl.schoolbrains;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by markroper on 4/14/16.
 */
@Test(groups = {"unit"})
public class SectionEnrollmentParserTest {

    @Test
    public void parserTest() {
        File input = new File(getClass().getClassLoader().getResource("schoolbrains/EdPanelSectionRoster.csv").getFile());
        SectionEnrollmentParser parser = new SectionEnrollmentParser(input);
        Map<String, List<String>> results = parser.parse();
        Assert.assertEquals(results.size(), 8, "Unexpected number of Students parsed from schoolbrains CSV generated");
    }
}
