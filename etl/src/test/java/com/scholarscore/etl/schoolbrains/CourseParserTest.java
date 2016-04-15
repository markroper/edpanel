package com.scholarscore.etl.schoolbrains;

import com.scholarscore.models.Course;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Set;

/**
 * Created by markroper on 4/14/16.
 */
@Test(groups = {"unit"})
public class CourseParserTest {

    @Test
    public void parserTest() {
        File input = new File(getClass().getClassLoader().getResource("schoolbrains/EdPanelSections.csv").getFile());
        CourseParser parser = new CourseParser(input);
        Set<Course> results = parser.parse();
        Assert.assertEquals(results.size(), 90, "Unexpected number of Students parsed from schoolbrains enrollment CSV");
    }
}
