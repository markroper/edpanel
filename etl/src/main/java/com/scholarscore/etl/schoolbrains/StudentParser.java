package com.scholarscore.etl.schoolbrains;

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
    private static final int  STUDENTID = 1;
    private static final int GRADEID = 2;
    private static final int CURRENTGRADEID = 3;
    private static final int CURRENTSCHOOL = 4;
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
        return s;
    }
}
