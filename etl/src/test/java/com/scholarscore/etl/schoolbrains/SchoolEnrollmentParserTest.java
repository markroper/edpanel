package com.scholarscore.etl.schoolbrains;

import com.scholarscore.models.user.Student;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

/**
 * Created by markroper on 4/14/16.
 */
@Test(groups = {"unit"})
public class SchoolEnrollmentParserTest {

    @Test
    public void parserTest() {
        File input = new File(getClass().getClassLoader().getResource("schoolbrains/EdPanelEnrollment.csv").getFile());
        SchoolEnrollmentParser parser = new SchoolEnrollmentParser(input);
        List<Student> results = parser.parse();
        Assert.assertEquals(results.size(), 1723, "Unexpected number of Students parsed from schoolbrains enrollment CSV");
    }
}