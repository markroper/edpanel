package com.scholarscore.etl.schoolbrains;

import com.scholarscore.etl.schoolbrains.parser.StudentParser;
import com.scholarscore.models.user.Student;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Set;

/**
 * Created by markroper on 4/14/16.
 */
@Test(groups = {"unit"})
public class StudentParserTest {

    @Test
    public void parserTest() {
        File input = new File(getClass().getClassLoader().getResource("schoolbrains/EdPanelStudents.csv").getFile());
        StudentParser parser = new StudentParser(input);
        Set<Student> results = parser.parse();
        Assert.assertEquals(results.size(), 515, "Unexpected number of Students parsed from schoolbrains CSV generated");
    }
}
