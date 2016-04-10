package com.scholarscore.etl.state.ma;

import com.scholarscore.models.state.ma.McasComplexity;
import com.scholarscore.models.state.ma.McasPerfLevel;
import com.scholarscore.models.state.ma.McasPerfLevel2;
import com.scholarscore.models.state.ma.McasResult;
import com.scholarscore.models.state.ma.McasStatus;
import com.scholarscore.models.state.ma.McasTopicScore;
import com.scholarscore.models.user.Student;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Capable of parsing an MCAS results CSV and marshalling that into an EdPanel McasResult object.
 *
 * Some notes on the MCAS conventions:
 *
 *    Massachusetts fulfills the requirements of the federal No Child Left Behind Act by administering
 *    MCAS tests in English language arts (ELA) and Mathematics to students in grades 3-8 and 10.
 *    Additional MCAS tests are administered in Science and Technology/Engineering (grades 5, 8, 9/10).
 *
 *    Multiple choice (MC) items:
 *      Correct response - alpha (+)
 *      Incorrect response - alpha (A-D)
 *      Omit - blank; Multiple mark - (*)
 *    Open Response (OR) items:
 *      Number of points earned (0-4)
 *      Invalid Response = 0
 *      no response - blank
 *
 *    All columns:
 *      sprp_dis,sprp_sch,system,district,school,schname,schtype,adminyear,bookletnumber,
 *      sasid,grade,stugrade,lastname,firstname,mi,gender,race_off,dob,yrsinmass,yrsinmass_num,yrsinsch,yrsindis,
 *      ever_ell,highneeds,freelunch_off,title1_off,lep_off,lepflep_off,flep_off,sped_off,plan504_off,
 *      firstlanguage,natureofdis,levelofneed,spedplacement,nclb_choice,octenr,conenr_sch,conenr_dis,conenr_sta,
 *      access_part,ealt,ecomplexity,eteststat,wptopdev,wpcompconv,eitem1,eitem2,eitem3,eitem4,eitem5,eitem6,
 *      eitem7,eitem8,eitem9,eitem10,eitem11,eitem12,eitem13,eitem14,eitem15,eitem16,eitem17,eitem18,eitem19,
 *      eitem20,eitem21,eitem22,eitem23,eitem24,eitem25,eitem26,eitem27,eitem28,eitem29,eitem30,eitem31,eitem32,
 *      eitem33,eitem34,eitem35,eitem36,eitem37,eitem38,eitem39,eitem40,eitem41,eitem42,erawsc,emcpts,eorpts,
 *      escaleds,eperflev,eperf2,ecpi,enumin,eassess,esgp,amendela,malt,mcomplexity,mteststat,mitem1,mitem2,
 *      mitem3,mitem4,mitem5,mitem6,mitem7,mitem8,mitem9,mitem10,mitem11,mitem12,mitem13,mitem14,mitem15,mitem16,
 *      mitem17,mitem18,mitem19,mitem20,mitem21,mitem22,mitem23,mitem24,mitem25,mitem26,mitem27,mitem28,mitem29,
 *      mitem30,mitem31,mitem32,mitem33,mitem34,mitem35,mitem36,mitem37,mitem38,mitem39,mitem40,mitem41,mitem42,
 *      mrawsc,mmcpts,morpts,mscaleds,mperflev,mperf2,mcpi,mnumin,massess,msgp,amendmat,salt,scomplexity,
 *      steststat,scitry,sitem1,sitem2,sitem3,sitem4,sitem5,sitem6,sitem7,sitem8,sitem9,sitem10,sitem11,sitem12,
 *      sitem13,sitem14,sitem15,sitem16,sitem17,sitem18,sitem19,sitem20,sitem21,sitem22,sitem23,sitem24,sitem25,
 *      sitem26,sitem27,sitem28,sitem29,sitem30,sitem31,sitem32,sitem33,sitem34,sitem35,sitem36,sitem37,sitem38,
 *      sitem39,sitem40,sitem41,sitem42,sitem43,sitem44,sitem45,srawsc,smcpts,sorpts,sscaleds,sperflev,sperf2,
 *      scpi,snumin,sassess,amendsci,ela_cd,math_cd,sci_cd,accom_l,accom_c,accom_m,accom_s,accom26l,accom29c,
 *      accom30m,datachanged,mcasrowid,grade2010,grade2011,grade2012,escaleds2010,escaleds2011,escaleds2012,
 *      mscaleds2010,mscaleds2011,mscaleds2012,esgp2010,esgp2011,esgp2012,msgp2010,msgp2011,msgp2012,summarize
 *
 * Created by markroper on 4/9/16.
 */
public class McasParser {
    private final static Logger LOGGER = LoggerFactory.getLogger(McasParser.class);
    protected File input;
    public McasParser(File file) {
        this.input = file;
    }

    private static final String DISTRICT_ID = "sprp_dis";
    private static final String SCHOOL_ID = "sprp_sch";
    private static final String SYSTEM = "system";
    private static final String DISTRICT_NAME = "district";
    private static final String SCHOOL_NAME = "schname";
    private static final String SCHOOL_TYPE = "schtype";
    private static final String ADMIN_YEAR = "adminyear";
    private static final String SASID = "sasid";
    private static final String EXAM_GRADE_LEVEL = "grade";
    private static final String STUDENT_GRADE_LEVEL = "stugrade";
    private static final String YEARS_IN_MA = "yrsinmass_num";
    /*
            ENGLISH
     */
    //ealt	C	Student took ELA alternate assessment (1= yes, blank for others)
    private static final String ELA_ALT = "ealt";
    private static final String ELA_COMPLEXITY = "ecomplexity";
    //02-12
    //NS = non-scorable
    //BL = not answered
    //blank = no composition booklet
    private static final String ELA_TOPIC_SCORE = "wptopdev";
    // T = tested
    // NTA = not tested absence
    // NTM = not tested medically documented absence
    // NTO = not tested other
    private static final String ELA_TEST_STATUS = "eteststat";
    // 02-08
    // NS = non-scorable
    // BL = not answered
    // blank = no composition booklet
    private static final String ELA_COMPOSITION_SCORE = "wpcompconv";
    //Raw score in ELA (Blank for ALT, LEP, MED, INC,TRN except if there are item responses)
    private static final String ELA_RAW_SCORE = "erawsc";
    //Scaled score in ELA (Blank for ALT, LEP, MED, INC,TRN)
    private static final String ELA_SCALED_SCORE = "escaleds";
    //A=advanced(grades4-8,10),
    // P+=Above Proficient(grade 3 only),
    // P=proficient,
    // NI=needs improvement,
    // F=failing (grade 10 only),
    // W=Warning (grades 3-8),
    // INC=incomplete,
    // MED=medically documented absence,
    // LEP=limited English proficient in US school for less than a year,
    // TRN=Transferred, INP=Incomplete alt, AWR=Awareness alt,
    // EMG=Emerging alt,
    // PRG=Progressing alt,
    // NIA=Needs Improvement alt,
    // P_A=Proficient alt,
    // A_A=Advanced alt
    //    279	2	eperf2	C
    private static final String ELA_PERF_LEVEL = "eperflev";
    //F=Failing(grade 10 only),
    // W=Warning(grades3-8),
    // NI=Needs Improvement,
    // P=Proficient,
    // A=Advanced(grades 4-8),
    // P+=Above Proficient(grade 3 only),
    // Blank
    private static final String ELA_PERF_2 = "eperf2";
    //ELA composite performance index (0, 25, 50, 75, 100)
    private static final String ELA_COMPOSITE_INDEX = "ecpi";
    /*
            MATH
     */
    //Student took math alternate assessment (1= yes, blank for others)
    private static final String MATH_ALT = "malt";
    //Math complexity level in alternate assessment
    // (ALT=Alternate Achievement Standards, MOD=Modified Achievement Level, GL=Grade Level Achievement Standards)
    private static final String MATH_COMPLEXITY = "mcomplexity";
    //T = tested
    //NTA = not tested absence
    //NTM = not tested medically documented absence
    //NTO = not tested other
    private static final String MATH_TEST_STATUS = "mteststat";
    //Raw score in math (Blank for ALT, LEP, MED, INC, TRN except if there are item responses)
    private static final String MATH_RAW_SCORE = "mrawsc";
    //Scaled score in  math (Blank for ALT, LEP, MED, INC,TRN)
    private static final String MATH_SCALED_SCORE = "mscaleds";
    //Performance level in math:
    // A=advanced(grades 4-8,10),
    // P+=Above Proficient(grade 3 only),
    // P=proficient,
    // NI=needs improvement,
    // F=failing (grade 10 only),
    // W=Warning (grades 3-8),
    // INC=incomplete,
    // MED=medically documented absence,
    // LEP=limited English proficient in US school for less than a year,
    // TRN=Transferred,INP=Incomplete alt,
    // AWR=Awareness alt,
    // EMG=Emerging alt,
    // PRG=Progressing alt,
    // NIA=Needs Improvement alt,
    // P_A=Proficient alt,
    // A_A=Advanced alt
    private static final String MATH_PERF_LEVEL = "mperflev";
    //F=Failing(grade 10 only),W=Warning(grades3-8),NI=Needs Improvement,P=Proficient,A=Advanced(grades 4-8),P+=Above Proficient,Blank
    private static final String MATH_PERF_LEVEL_2 = "mperf2";
    //Math composite performance index (0, 25, 50, 75, 100)
    private static final String MATH_COMPOSITE_INDEX = "mcpi";
    // 1=Student has cpi points,0=no cpi points(Scholarship grade 10 students will receive 0)
    private static final String MATH_CPI_POINTS = "mnumin";
    //Student was assessed(1=student has cpi points or student is first year LEP,0=ABS or MED,blank=TRN or INC or Scholarship grade 10 students)
    private static final String MATH_ASSESSED = "mnumin";
    /*
            SCIENCE
     */
    //Student took science alternate assessment (1= yes, blank for others)
    private static final String SCIENCE_ALT = "salt";
    //Science complexity level in alternate assessment
    // (ALT=Alternate Achievement Standards, MOD=Modified Achievement Standards, GL=Grade Level Achievement Standards)
    private static final String SCIENCE_COMPLEXITY = "scomplexity";
    //Testing status in Science & Technology
    // T = tested
    // NTA = not tested absence
    // NTM = not tested medically documented absence
    // NTO = not tested other
    private static final String SCIENCE_TEST_STATUS = "steststat";
    //Reserve Science Form taken:Y=yes
    private static final String SCIENCE_RESERVE_FORM = "sform";
    //Raw score in S&T (Blank for ALT, LEP, MED, INC,TRN except if there are item responses)
    private static final String SCIENCE_RAW_SCORE = "srawsc";
    //Scaled score in  S&T (Blank for ALT, LEP, MED, INC,TRN)
    private static final String SCIENCE_SCALED_SCORE = "sscaleds";
    //Performance level in science:
    // A=advanced(Grades 4-8),
    // P+=Above Proficient,
    // P=proficient,
    // NI=needs improvement,
    // F=failing (grade 10 only),
    // W=Warning (grades 3-8),
    // INC=incomplete,
    // MED=medically documented absence,
    // LEP=limited English proficient in US school for less than a year,
    // TRN=Transferred,
    // INP=Incomplete alt,
    // AWR=Awareness alt,
    // EMG=Emerging alt,
    // PRG=Progressing alt,
    // NIA=Needs Improvement alt,
    // P_A=Proficient alt,
    // A_A=Advanced alt
    private static final String SCIENCE_PERF_LEVEL = "sperflev";
    //F=Failing(grade 10 only),
    // W=Warning(grades3-8),
    // NI=Needs Improvement,
    // P=Proficient,
    // A=Advanced(Grades 4-8),
    // P+=Above Proficient,Blank
    private static final String SCIENCE_PERF_LEVEL_2 = "sperf2";
    //Science composite performance index (0, 25, 50, 75, 100)
    private static final String SCIENCE_COMPOSITE_INDEX = "scpi";
    //1=Student has cpi points,0=no cpi points(Scholarship grade 10 students will receive 0)
    private static final String SCIENCE_CPI_POINTS = "snumin";
    //Student was assessed(1=student has cpi points or student is first year LEP,0=ABS or MED,blank=TRN or INC or Scholarship grade 10 students)
    private static final String SCIENCE_ASSESSED = "sassess";

    public List<McasResult> parse() {
        if(null == input) {
            return null;
        }
        try {
            List<McasResult> results = new ArrayList<>();
            CSVParser parser = CSVParser.parse(input, Charset.defaultCharset(), CSVFormat.DEFAULT.withHeader());
            for(CSVRecord rec: parser) {
                McasResult result = new McasResult();
                try {
                    result.setSchoolId(Long.valueOf(rec.get(SCHOOL_ID)));
                } catch (NumberFormatException nfe) {
                    LOGGER.debug("Unable to parse school ID from input: " + rec.get(SCHOOL_ID));
                }
                result.setSchoolName(rec.get(SCHOOL_NAME));
                result.setStudent(new Student());
                result.getStudent().setStateStudentId(rec.get(SASID));
                result.setExamGradeLevel(parseLongOrReturnNull(rec.get(EXAM_GRADE_LEVEL)));
                result.setStudentGradeLevel(parseLongOrReturnNull(rec.get(STUDENT_GRADE_LEVEL)));
                result.setEnglishTopicScore(parseDoubleOrReturnNull(rec.get(ELA_TOPIC_SCORE)));
                result.setEnglishCompositionScore(parseDoubleOrReturnNull(rec.get(ELA_COMPOSITION_SCORE)));
                //ENGLISH - ADD GRADES
                McasTopicScore ela = new McasTopicScore();
                ela.setAlternateExam("1".equals(rec.get(ELA_ALT)));
                ela.setComplexity(McasComplexity.generate(rec.get(ELA_COMPLEXITY)));
                ela.setExamStatus(McasStatus.generate(rec.get(ELA_TEST_STATUS)));
                ela.setRawScore(parseDoubleOrReturnNull(rec.get(ELA_RAW_SCORE)));
                ela.setScaledScore(parseDoubleOrReturnNull(rec.get(ELA_SCALED_SCORE)));
                ela.setPerformanceLevel(McasPerfLevel.generate(rec.get(ELA_PERF_LEVEL)));
                ela.setPerformanceLevel2(McasPerfLevel2.generate(rec.get(ELA_PERF_2)));
                ela.setQuartile(parseLongOrReturnNull(rec.get(ELA_COMPOSITE_INDEX)));
                result.setEnglishScore(ela);
                //MATH - ALL GRADES
                McasTopicScore math = new McasTopicScore();
                math.setAlternateExam("1".equals(rec.get(MATH_ALT)));
                math.setComplexity(McasComplexity.generate(rec.get(MATH_COMPLEXITY)));
                math.setExamStatus(McasStatus.generate(rec.get(MATH_TEST_STATUS)));
                math.setRawScore(parseDoubleOrReturnNull(rec.get(MATH_RAW_SCORE)));
                math.setScaledScore(parseDoubleOrReturnNull(rec.get(MATH_SCALED_SCORE)));
                math.setPerformanceLevel(McasPerfLevel.generate(rec.get(MATH_PERF_LEVEL)));
                math.setPerformanceLevel2(McasPerfLevel2.generate(rec.get(MATH_PERF_LEVEL_2)));
                math.setQuartile(parseLongOrReturnNull(rec.get(MATH_COMPOSITE_INDEX)));
                result.setMathScore(math);
                //SCIENCE - SOME GRADES
                McasTopicScore science = new McasTopicScore();
                science.setAlternateExam("1".equals(rec.get(SCIENCE_ALT)));
                science.setComplexity(McasComplexity.generate(rec.get(SCIENCE_COMPLEXITY)));
                science.setExamStatus(McasStatus.generate(rec.get(SCIENCE_TEST_STATUS)));
                science.setRawScore(parseDoubleOrReturnNull(rec.get(SCIENCE_RAW_SCORE)));
                science.setScaledScore(parseDoubleOrReturnNull(rec.get(SCIENCE_SCALED_SCORE)));
                science.setPerformanceLevel(McasPerfLevel.generate(rec.get(SCIENCE_PERF_LEVEL)));
                science.setPerformanceLevel2(McasPerfLevel2.generate(rec.get(SCIENCE_PERF_LEVEL_2)));
                science.setQuartile(parseLongOrReturnNull(rec.get(SCIENCE_COMPOSITE_INDEX)));
                //Science is not always administered in all grades.
                if(null != science.getExamStatus()) {
                    result.setScienceScore(science);
                }
                results.add(result);
            }
            return results;
        } catch (IOException e) {
            LOGGER.error("Unable to parse MCAS file. " + e.getMessage());
            return null;
        }
    }

    public Double parseDoubleOrReturnNull(String input) {
        try {
            return Double.parseDouble(input);
        } catch(NumberFormatException | NullPointerException e) {
            LOGGER.debug("Unable to parse double from input: " + input);
        }
        return null;
    }
    public Long parseLongOrReturnNull(String input) {
        try {
            return Long.parseLong(input);
        } catch(NumberFormatException | NullPointerException e) {
            LOGGER.debug("Unable to parse long from input: " + input);
        }
        return null;
    }
}
