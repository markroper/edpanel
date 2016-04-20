package com.scholarscore.etl.schoolbrains;

import com.scholarscore.etl.schoolbrains.parser.SectionParser;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Set;

/**
 * Created by markroper on 4/14/16.
 */
@Test(groups = {"unit"})
public class SectionParserTest {

    @Test
    public void parserTest() {
        File input = new File(getClass().getClassLoader().getResource("schoolbrains/EdPanelSections.csv").getFile());
        SectionParser parser = new SectionParser(input);
        Set<SectionContainer> results = parser.parse();
        Assert.assertEquals(results.size(), 400, "Unexpected number of Students parsed from schoolbrains CSV generated");
    }
}
