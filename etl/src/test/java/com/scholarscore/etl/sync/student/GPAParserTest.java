package com.scholarscore.etl.sync.student;

import com.scholarscore.etl.powerschool.sync.student.gpa.GPAParser;
import org.testng.annotations.Test;

/**
 * Created by mattg on 11/24/15.
 */
@Test(groups = {"unit"})
public class GPAParserTest {

    public void testStaticFileParsing() {
        GPAParser parser = new GPAParser();
        parser.parse(GPAParserTest.class.getClassLoader().getResourceAsStream("student_gpa.csv"));
    }
}
