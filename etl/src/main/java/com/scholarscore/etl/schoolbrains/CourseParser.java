package com.scholarscore.etl.schoolbrains;

import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import org.apache.commons.csv.CSVRecord;

import java.io.File;

/**
 * Created by markroper on 4/14/16.
 */
public class CourseParser extends MultiEntityCsvParser<Course> {
    public CourseParser(File file) {
        super(file);
    }

    @Override
    public Course parseRec(CSVRecord rec) {
        Course c = new Course();
        c.setName(rec.get(CourseName));
        c.setSourceSystemId(rec.get(CourseEAID));
        c.setNumber(rec.get(CourseCode));
        School s = new School();
        s.setSourceSystemId(rec.get(SchoolID));
        c.setSchool(s);
        return c;
    }
}
