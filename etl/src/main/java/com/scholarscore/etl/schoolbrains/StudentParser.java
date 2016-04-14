package com.scholarscore.etl.schoolbrains;

import com.scholarscore.models.Address;
import com.scholarscore.models.user.Student;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by markroper on 4/14/16.
 */
public class StudentParser extends BaseParser<Student> {
    private final static Logger LOGGER = LoggerFactory.getLogger(StudentParser.class);
    public StudentParser(File f) {
        super(f);
    }

    private static final int SASID = 0;
    private static final int STUDENT_ID = 1;
    private static final int GRADE_ID = 2;
    private static final int CURRENTGRADEID = 3;
    private static final int CURRENT_SCHOOL = 4;
    private static final int DATE_OF_BIRTH = 5;
    private static final int EMAIL = 6;
    private static final int ETHNIC_CATEGORY = 7;
    private static final int ETHNIC_CODE = 8;
    private static final int ETHNIC_ID = 9;
    private static final int FIRST_NAME = 10;
    private static final int MIDDlE_NAME = 11;
    private static final int LAST_NAME = 12;
    private static final int GENDER = 13;
    private static final int HOME_ADDRESS_LINE_1 = 14;
    private static final int HOME_ADDRESS_LINE_2 = 15;
    private static final int HOME_CITY = 16;
    private static final int HOME_STATE_ID = 17;
    private static final int HOME_POSTAL_CODE = 18;
    private static final int LASID = 19;
    private static final int SCHOOL_STATE_ID = 20;
    private static final int YEAR_OF_GRADUATION = 21;
    private static final int SCHOOL_ID = 22;
    private static final int SPECIAL_ED_EVAL_RESULTS = 23;
    private static final int SPECIAL_ED_LEVEL_OF_NEED = 24;
    private static final int ELL_STATUS = 25;

    @Override
    public Student parseRec(CSVRecord rec) {
        Student s = new Student();
        s.setStateStudentId(rec.get(SASID));
        s.setSourceSystemId(rec.get(STUDENT_ID));
        s.setSourceSystemId(rec.get(LASID));
        s.setCurrentGradeLevel(parseLongOrReturnNull(rec.get(GRADE_ID)));
        //SCHOOL
        String school = rec.get(CURRENT_SCHOOL);
        String schoolId = rec.get(SCHOOL_ID);
        String schoolStateId = rec.get(SCHOOL_STATE_ID);
        //TODO: convert me.
        String DOB = rec.get(DATE_OF_BIRTH);
        //TODO: convert me
        s.setEmail(rec.get(EMAIL));
        //ETHNICITY
        String ethnicCat = rec.get(ETHNIC_CATEGORY);
        String ethnicCode = rec.get(ETHNIC_CODE);
        String ethnicId = rec.get(ETHNIC_ID);
        //TODO: resolve me
        //NAME RESOLUTION
        String first = rec.get(FIRST_NAME);
        String middle = rec.get(MIDDlE_NAME);
        String last = rec.get(LAST_NAME);
        String name = first;
        if(null != middle && middle.trim().length() > 0) {
            name += " " + middle;
        }
        if(null != last && last.trim().length() > 0) {
            name += " " + last;
        }
        s.setName(name);
        //GENDER
        String gender = rec.get(GENDER);
        //TODO: convert me

        Address a = new Address();
        a.setStreet(rec.get(HOME_ADDRESS_LINE_1));
        String line2 = rec.get(HOME_ADDRESS_LINE_2);
        if(null != line2 && line2.trim().length() > 0) {
            a.setStreet(a.getStreet() + " " + line2);
        }
        a.setCity(rec.get(HOME_CITY));
        a.setPostalCode(rec.get(HOME_POSTAL_CODE));
        a.setState(rec.get(HOME_STATE_ID));
        s.setHomeAddress(a);
        s.setProjectedGraduationYear(parseLongOrReturnNull(rec.get(YEAR_OF_GRADUATION)));
        //SPED
        String spedEvalResults = rec.get(SPECIAL_ED_EVAL_RESULTS);
        String spedLevel = rec.get(SPECIAL_ED_LEVEL_OF_NEED);
        //TODO: convert me
        //ELL
        String ellStatus = rec.get(ELL_STATUS);
        return s;
    }
}
