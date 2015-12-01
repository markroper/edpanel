package com.scholarscore.etl.sync.student;

import com.scholarscore.etl.powerschool.sync.student.gpa.GpaParser;
import com.scholarscore.etl.powerschool.sync.student.gpa.RawGpaValue;
import com.scholarscore.models.gpa.Gpa;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

/**
 * Created by mattg on 11/24/15.
 */
@Test(groups = {"unit"})
public class GpaParserTest {

    @DataProvider
    private Object[][] fileBasedParseDataProvider() {
        return new Object[][] {
                { "Complete student GPA file", "student_gpa.csv", 115 },
                { "GPA 1 file", "gpaExtract1.csv", 56 },
                { "GPA 2 file", "gpaExtract2.csv", 59 },
        };
    }

    @Test(dataProvider = "fileBasedParseDataProvider")
    public void testStaticFileParsing(String msg, String resourceFile, int expectedNumberOfGPAs) {
        GpaParser parser = new GpaParser();
        List<RawGpaValue> gpas = parser.parse(GpaParserTest.class.getClassLoader().getResourceAsStream(resourceFile));
        assertNotNull("Expected non-null response from parser-parse for GPA csv file");
        assertEquals(expectedNumberOfGPAs, gpas.size(), "Expected " + expectedNumberOfGPAs + " gpa entries for test: " + msg);

        for (RawGpaValue value : gpas) {
            Gpa gpaValue =  value.emit();
            assertNotNull("Expected non-null GPA value", gpaValue);
        }
    }
}
