package com.scholarscore.etl.schoolbrains;

import com.scholarscore.etl.schoolbrains.parser.SchoolParser;
import com.scholarscore.models.School;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Set;

/**
 * User: jordan
 * Date: 4/20/16
 * Time: 12:30 PM
 */
@Test(groups = {"unit"})
public class SchoolParserTest {

    @Test
    public void parserTest() {
        File input = new File(getClass().getClassLoader().getResource("schoolbrains/EdPanelSchools.csv").getFile());
        SchoolParser parser = new SchoolParser();
        Set<School> results = parser.parse(input);
        Assert.assertEquals(results.size(), 198, "Unexpected number of Schools parsed from EdPanelSchools.csv");
    }

}
