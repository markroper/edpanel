package com.scholarscore.etl.schoolbrains;

import com.scholarscore.models.Address;
import com.scholarscore.models.Gender;
import com.scholarscore.models.user.Ethnicity;
import com.scholarscore.models.user.Race;
import com.scholarscore.models.user.Student;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Parses EdPanel student instances from the SchoolBrains student csv extract file.
 *
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
    protected static final int Gpa = 26;

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("M/d/yyyy");
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
        s.setCurrentSchoolId(parseLongOrReturnNull(rec.get(SCHOOL_ID)));
        //DOB
        String dob = rec.get(DATE_OF_BIRTH);
        String[] strings = dob.split("\\s+");
        if(strings.length > 0) {
            dob = strings[0];
        }
        s.setBirthDate(LocalDate.parse(dob, dtf));
        //TODO: convert me
        s.setEmail(rec.get(EMAIL));
        //ETHNICITY
        s.setFederalEthnicity(resolveEthnicity(rec.get(ETHNIC_CATEGORY)));
        s.setFederalRace(resolveRace(rec.get(ETHNIC_ID)));
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
        s.setGender(resolveGender(rec.get(GENDER)));
        //HOME ADDRESS
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
        s.setSped(resolveSped(rec.get(SPECIAL_ED_LEVEL_OF_NEED)));
        s.setSpedDetail(rec.get(SPECIAL_ED_EVAL_RESULTS));
        //ELL
        String ellStatus = rec.get(ELL_STATUS);
        s.setEll(resolveEll(ellStatus));
        return s;
    }

    public static Boolean resolveEll(String s) {
        if(null == s || s.trim().length() == 0) {
            return false;
        }
        String lower = s.toLowerCase();
        if(lower.contains("not enrolled")) {
            return false;
        }
        return true;
    }
    public static Boolean resolveSped(String s) {
        if(null == s || s.trim().length() == 0) {
            return false;
        }
        String lower = s.toLowerCase();
        if(lower.contains("500")) {
            return false;
        }
        return true;
    }

    public static final Gender resolveGender(String g) {
        if(null == g || g.trim().length() == 0) {
            return Gender.OTHER;
        }
        String l = g.toLowerCase();
        if(l.contains("female") || l.contains("girl")){
            return Gender.FEMALE;
        }
        if(l.contains("male")) {
            return Gender.MALE;
        }
        return Gender.OTHER;
    }

    public static final String resolveEthnicity(String e) {
        if(null == e || e.length() == 0) {
            return Ethnicity.UNSPECIFIED.name();
        }
        String lower = e.toLowerCase();
        if(lower.contains("not") || lower.contains("non")) {
            return Ethnicity.NON_HISPANIC_LATINO.name();
        }
        if(lower.contains("hispanic") || lower.contains("latino") || lower.contains("latina")) {
            return Ethnicity.HISPANIC_LATINO.name();
        }
        return Ethnicity.NON_HISPANIC_LATINO.name();
    }

    public static final String resolveRace(String e) {
        if(null == e || e.length() == 0) {
            return Race.UNSPECIFIED.name();
        }
        String lower = e.toLowerCase();
        if(lower.contains("white") || lower.contains("caucasian")) {
            return Race.CAUCASIAN.name();
        }
        if(lower.contains("black") || lower.contains("african")) {
            return Race.AFRICAN_AMERICAN.name();
        }
        if(lower.contains("asian")) {
            return Race.ASIAN.name();
        }
        if(lower.contains("indian") || lower.contains("american indian") || lower.contains("alaska")) {
            return Race.AMERICAN_INDIAN.name();
        }
        if(lower.contains("pacific") || lower.contains("island")) {
            return Race.PACIFIC_ISLANDER.name();
        }
        return Race.UNSPECIFIED.name();
    }
}
