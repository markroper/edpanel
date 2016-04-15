package com.scholarscore.etl.schoolbrains;

import com.scholarscore.etl.schoolbrains.parser.StaffParser;
import com.scholarscore.models.user.Staff;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Set;

/**
 * Created by markroper on 4/14/16.
 */
@Test(groups = {"unit"})
public class StaffParserTest {

    @Test
    public void parserTest() {
        File input = new File(getClass().getClassLoader().getResource("schoolbrains/EdPanelSections.csv").getFile());
        StaffParser parser = new StaffParser(input);
        Set<Staff> results = parser.parse();
        Assert.assertEquals(results.size(), 34, "Unexpected number of Students parsed from schoolbrains CSV generated");
    }
}
