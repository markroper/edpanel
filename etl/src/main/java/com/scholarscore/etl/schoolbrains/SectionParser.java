package com.scholarscore.etl.schoolbrains;

import com.scholarscore.models.Course;
import com.scholarscore.models.Section;
import com.scholarscore.models.user.Staff;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by markroper on 4/14/16.
 */
public class SectionParser extends MultiEntityCsvParser<SectionContainer> {
    private static final String TRUE = "true";

    public SectionParser(File file) {
        super(file);
    }

    @Override
    public SectionContainer parseRec(CSVRecord rec) {
        Section s = new Section();
        List<Boolean> terms = new ArrayList<>();
        s.setSourceSystemId(rec.get(SectionID));
        s.setName(rec.get(SectionName));
        Course c = new Course();
        c.setSourceSystemId(rec.get(CourseEAID));
        s.setCourse(c);
        terms.add(TRUE.equals(rec.get(IsTerm1).toLowerCase()));
        terms.add(TRUE.equals(rec.get(IsTerm2).toLowerCase()));
        terms.add(TRUE.equals(rec.get(IsTerm3).toLowerCase()));
        terms.add(TRUE.equals(rec.get(IsTerm4).toLowerCase()));
        terms.add(TRUE.equals(rec.get(IsTerm5).toLowerCase()));
        terms.add(TRUE.equals(rec.get(IsTerm6).toLowerCase()));
        int numTerms = 0;
        for(Boolean b: terms) {
            if(b) {
                numTerms++;
            }
        }
        s.setNumberOfTerms(numTerms);
        Set<Staff> teachers = new HashSet<>();
        Staff t = new Staff();
        t.setSourceSystemId(rec.get(TeacherID));
        t.setName(rec.get(Teacher));
        t.setIsTeacher(true);
        String teacher2Id = rec.get(SecondTeacherID);
        if(null != teacher2Id && !teacher2Id.trim().isEmpty()) {
            Staff t2 = new Staff();
            t2.setIsTeacher(true);
            t2.setSourceSystemId(teacher2Id);
            t2.setName(rec.get(SecondTeacher));
            teachers.add(t2);
        }
        s.setTeachers(teachers);
        return new SectionContainer(s, terms, rec.get(SchoolYearID));
    }
}
