package com.scholarscore.etl.schoolbrains;

import com.scholarscore.models.user.EnrollStatus;
import com.scholarscore.models.user.Student;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Useful in parsing some information about student enrollment that doesn't come out in the EdPanelStudent.csv
 * extract.  Specifically, the field district entry date and is_active flag.
 *
 * Created by markroper on 4/14/16.
 */
public class StudentEnrollmentParser extends BaseParser<Student> {
    private static final int STUDENT_ID = 0;
    private static final int GRADE_ID = 1;
    private static final int SCHOOL_ID = 2;
    private static final int SCHOOL_YEAR_ID = 3;
    private static final int SASID = 4;
    private static final int LASID = 5;
    private static final int IS_ACTIVE = 6;
    private static final int DISTRICT_ENTRY_DATE = 7;
    private static final int CURRENT_SCHOOL = 8;
    private static final int SCHOOL_YEAR = 9;
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("M/d/yyyy");
    public StudentEnrollmentParser(File file) {
        super(file);
    }

    @Override
    public Student parseRec(CSVRecord rec) {
        Student s = new Student();
        s.setSourceSystemId(rec.get(STUDENT_ID));
        s.setSourceSystemUserId(rec.get(LASID));
        s.setStateStudentId(rec.get(SASID));
        s.setCurrentGradeLevel(parseLongOrReturnNull(rec.get(GRADE_ID)));
        String ded = rec.get(DISTRICT_ENTRY_DATE);
        String[] deds = ded.split("\\s+");
        if(null != deds && deds.length > 0) {
            ded = deds[0];
        }
        s.setDistrictEntryDate(LocalDate.parse(ded, dtf));
        s.setCurrentSchoolId(parseLongOrReturnNull(rec.get(SCHOOL_ID)));
        String active = rec.get(IS_ACTIVE);
        if("true".equals(active.toLowerCase()) || "1".equals(active.toLowerCase())) {
            s.setEnrollStatus(EnrollStatus.CURRENTLY_ENROLLED);
        } else {
            s.setEnrollStatus(EnrollStatus.INACTIVE);
        }
        return s;
    }
}
