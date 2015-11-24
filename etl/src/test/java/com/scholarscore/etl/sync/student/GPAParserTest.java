package com.scholarscore.etl.sync.student;

import com.scholarscore.etl.powerschool.sync.student.gpa.GPAParser;
import com.scholarscore.etl.powerschool.sync.student.gpa.RawGPAValue;
import com.scholarscore.models.gpa.Gpa;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

/**
 * Created by mattg on 11/24/15.
 */
@Test(groups = {"unit"})
public class GPAParserTest {

    public void testStaticFileParsing() {
        GPAParser parser = new GPAParser();
        List<RawGPAValue> gpas = parser.parse(GPAParserTest.class.getClassLoader().getResourceAsStream("student_gpa.csv"));
        assertNotNull("Expected non-null response from parser-parse for GPA csv file");
        assertEquals(21, gpas.size(), "Expected 21 gpa entries");

        for (RawGPAValue value : gpas) {
            Gpa gpaValue =  value.emit();
            assertNotNull("Expected non-null GPA value", gpaValue);
        }
    }
}
