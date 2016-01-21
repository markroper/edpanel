package com.scholarscore.api.util;

import com.scholarscore.models.Behavior;
import com.scholarscore.models.BehaviorCategory;
import com.scholarscore.models.Course;
import com.scholarscore.models.Gender;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.Term;
import com.scholarscore.models.assignment.Assignment;
import com.scholarscore.models.assignment.AssignmentType;
import com.scholarscore.models.assignment.AttendanceAssignment;
import com.scholarscore.models.assignment.GradedAssignment;
import com.scholarscore.models.assignment.StudentAssignment;
import com.scholarscore.models.goal.AssignmentGoal;
import com.scholarscore.models.goal.AttendanceGoal;
import com.scholarscore.models.goal.BehaviorGoal;
import com.scholarscore.models.goal.CumulativeGradeGoal;
import com.scholarscore.models.goal.Goal;
import com.scholarscore.models.gradeformula.GradeFormula;
import com.scholarscore.models.survey.Survey;
import com.scholarscore.models.survey.SurveyResponse;
import com.scholarscore.models.survey.answer.BooleanAnswer;
import com.scholarscore.models.survey.answer.MultipleChoiceAnswer;
import com.scholarscore.models.survey.answer.OpenAnswer;
import com.scholarscore.models.survey.answer.QuestionAnswer;
import com.scholarscore.models.survey.question.SurveyBooleanQuestion;
import com.scholarscore.models.survey.question.SurveyMultipleChoiceQuestion;
import com.scholarscore.models.survey.question.SurveyOpenResponseQuestion;
import com.scholarscore.models.survey.question.SurveyQuestion;
import com.scholarscore.models.user.Staff;
import com.scholarscore.models.user.Student;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates arbitrary school data for use in testing and developing the UI.
 * 
 * @author markroper
 *
 */
public class SchoolDataFactory {
    private final static long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final String WHITE = "W";
    private static final String BLACK = "B";
    private static final String AMERICAN_INDIAN = "I";
    private static final String ASIAN = "A";
    private static final String PACIFIC_ISLANDER = "P";
    private static final String HISPANIC_LATINO = "YES";
    private static final String NON_HISPANIC_LATINO = "NO";
    private static final String OTHER = "Other";
    
    private static Random random = new Random();
    
    /**
     * Generates and returns an arbitrary school object
     * @return
     */
    public static School generateSchool() {
        School school = new School();
        school.setName("Xavier Academy");
        return school;
    }

    public static Map<Survey, List<SurveyResponse>> generateSurveysAndResponses(
                                        List<Student> generatedStudents,
                                        List<Staff> createdTeachers,
                                        List<Section> sections,
                                        School school) {
        Map<Survey, List<SurveyResponse>> respMap = new HashMap<>();
        for(int i = 0; i < 6; i++) {
            Survey s = new Survey();
            s.setCreator(createdTeachers.get(RandomUtils.nextInt(0, createdTeachers.size())));
            s.setCreatedDate(LocalDate.now());
            s.setAdministeredDate(LocalDate.now().plusDays(RandomUtils.nextInt(1, 30)));
            s.setName("Survey " + RandomUtils.nextInt(0, 2000000));
            if(i % 3 == 0) {
                s.setSchoolFk(school.getId());
            } else if(i % 2 == 0) {
                s.setSchoolFk(school.getId());
                s.setSectionFk(sections.get(RandomUtils.nextInt(0, sections.size())).getId());
            }
            List<SurveyQuestion> questions = new ArrayList<>();
            s.setQuestions(questions);
            for(int j = 0; j < RandomUtils.nextInt(2, 10); j++) {
                if(j % 3 == 0) {
                    SurveyBooleanQuestion bq = new SurveyBooleanQuestion();
                    bq.setQuestion("Do you like the number " + RandomUtils.nextInt(0, 15000) + "?");
                    questions.add(bq);
                } else if(j % 2 == 0) {
                    SurveyMultipleChoiceQuestion mc = new SurveyMultipleChoiceQuestion();
                    mc.setQuestion(RandomStringUtils.randomAscii(10) + "?");
                    mc.setResponseRequired(true);
                    mc.setChoices(new ArrayList<String>(){{
                        add(RandomStringUtils.randomAscii(10));
                        add(RandomStringUtils.randomAscii(6));
                        add(RandomStringUtils.randomAscii(8));
                    }});
                    questions.add(mc);
                } else {
                    SurveyOpenResponseQuestion oq = new SurveyOpenResponseQuestion();
                    oq.setQuestion(RandomStringUtils.randomAlphabetic(10) + "?");
                    oq.setMaxResponseLength(200);
                    oq.setResponseRequired(false);
                    questions.add(oq);
                }
            }
            List<SurveyResponse> responses = new ArrayList<>();
            for(Student student: generatedStudents) {
                if(new Random().nextBoolean()) {
                    SurveyResponse resp = new SurveyResponse();
                    resp.setSurvey(s);
                    resp.setRespondent(student);
                    resp.setResponseDate(LocalDate.now().plusDays(RandomUtils.nextInt(2, 30)));
                    List<QuestionAnswer> qas = new ArrayList<>();
                    resp.setAnswers(qas);
                    for(SurveyQuestion q : questions) {
                        if(q instanceof SurveyBooleanQuestion) {
                            BooleanAnswer a = new BooleanAnswer();
                            a.setAnswer(new Random().nextBoolean());
                            a.setQuestion((SurveyBooleanQuestion)q);
                            qas.add(a);
                        } else if(q instanceof SurveyOpenResponseQuestion) {
                            OpenAnswer a = new OpenAnswer();
                            a.setAnswer(RandomStringUtils.randomAscii(75));
                            a.setQuestion((SurveyOpenResponseQuestion)q);
                            qas.add(a);
                        } else if(q instanceof SurveyMultipleChoiceQuestion) {
                            SurveyMultipleChoiceQuestion mcq = (SurveyMultipleChoiceQuestion)q;
                            MultipleChoiceAnswer a = new MultipleChoiceAnswer();
                            a.setAnswer(RandomUtils.nextInt(0, mcq.getChoices().size()));
                            a.setQuestion(mcq);
                            qas.add(a);
                        }
                    }
                    responses.add(resp);
                }
            }
            respMap.put(s, responses);
        }
        return respMap;
    }

    public static List<Staff> generateAdmins(Long currentSchoolId) {
        List<Staff> admins = new ArrayList<>();
        Staff admin1 = new Staff();
        admin1.setName("Chris Wallace");
        admin1.setUsername("cwallace");
        admin1.setPassword("admin");
        admin1.setEnabled(true);
        admin1.setCurrentSchoolId(currentSchoolId);
        admin1.setAdmin(true);
        admins.add(admin1);
        return admins;
    }
    /**
     * Generates and returns an arbitrary teacher object with the school FK set to 
     * currentSchoolId
     * @param currentSchoolId
     * @return
     */
    public static List<Staff> generateTeachers(Long currentSchoolId) {
        List<Staff> teachers = new ArrayList<Staff>();
        Staff teacher1 = new Staff();
        teacher1.setName("Ms. Doe");
        teacher1.setCurrentSchoolId(currentSchoolId);
        teacher1.setTeacher(true);
        Staff teacher2 = new Staff();
        teacher2.setName("Mr. Smith");
        teacher2.setCurrentSchoolId(currentSchoolId);
        teacher2.setTeacher(true);
        Staff teacher3 = new Staff();
        teacher3.setName("Mrs. Matthews");
        teacher3.setCurrentSchoolId(currentSchoolId);
        teacher3.setTeacher(true);
        teachers.add(teacher1);
        teachers.add(teacher2);
        teachers.add(teacher3);
        for(Staff s : teachers) {
            s.setUsername(s.getName().split("\\s+")[1]);    
            // password, onetime pass and enabled flag cannot be directly set on this object in this manner
//            s.setOneTimePass("onetimepass");
//            s.setPassword("password");
//            s.setEnabled(true);
        }
        return teachers;
    }
    
    /**
     * Generates and returns a list of arbitrary students with the students' current 
     * school IDs set to the parameter provided.
     * @param currentSchoolId
     * @return
     */
    @SuppressWarnings("serial")
    public static List<Student> generateStudents(final Long currentSchoolId) {
        List<Student> students = new ArrayList<>();
        students.add(new Student(WHITE, HISPANIC_LATINO, currentSchoolId, Gender.MALE, "Carlos Vasquez", 2017L));
        students.add(new Student(BLACK, NON_HISPANIC_LATINO, currentSchoolId, Gender.FEMALE, "Tanya Segel", 2018L));
        students.add(new Student(ASIAN, NON_HISPANIC_LATINO, currentSchoolId, Gender.FEMALE, "Ashley Brown", 2017L));
        students.add(new Student(WHITE, HISPANIC_LATINO, currentSchoolId, Gender.MALE, "Bernard Slim", 2018L));
        students.add(new Student(BLACK, HISPANIC_LATINO, currentSchoolId, Gender.FEMALE, "Bernadette Slim", 2018L));
        students.add(new Student(BLACK, NON_HISPANIC_LATINO, currentSchoolId, Gender.MALE, "Jason Carter", 2018L));
        students.add(new Student(WHITE, HISPANIC_LATINO, currentSchoolId, Gender.FEMALE, "Yasmine Fort", 2018L));
        students.add(new Student(WHITE, NON_HISPANIC_LATINO, currentSchoolId, Gender.MALE, "Alexander Panagopalous", 2017L));
        students.add(new Student(ASIAN, NON_HISPANIC_LATINO, currentSchoolId, Gender.FEMALE, "Claire Martinez", 2016L));
        students.add(new Student(PACIFIC_ISLANDER, NON_HISPANIC_LATINO, currentSchoolId, Gender.MALE, "Otto Porter", 2016L));
        int i = 0;
        for(Student s : students) {
            s.setCurrentSchoolId(currentSchoolId);
            s.setUsername(s.getName().split("\\s+")[0]);
            s.setPassword("password");
            s.setEnabled(true);
            if(i % 3 == 0) {
                s.setEnabled(false);
            }
            i++;
        }
        return students;
    }
    
    /**
     * Generates and returns a list of contiguous school years, from the present year 
     * backward in time, associates with the school ID passed in as parameter.
     * @return
     */
    public static List<SchoolYear> generateSchoolYears() {
        int year = 2016;
        int startMonth = 8;
        int endMonth = 5;
        int day = 1;
        ArrayList<SchoolYear> years = new ArrayList<SchoolYear>();
        for(int i = 0; i < 1; i++) {
            SchoolYear schoolYear = new SchoolYear(LocalDate.of(year - 1, startMonth, day), LocalDate.of(year - i, endMonth, day));
            schoolYear.setName("SchoolYear " + new BigInteger(130, random).toString(32));
            years.add(schoolYear);
        }
        return years;
    }
    
    /**
     * Generates and returns a list of course instances associated with the schoolId 
     * passed in as a parameter.
     * @param schoolId
     * @return
     */
    public static List<Course> generateCourses(Long schoolId) {
        Course calc = new Course();
        calc.setName("BC Calculus");
        Course geo = new Course();
        geo.setName("Geometry");
        Course alg = new Course();
        alg.setName("Algebra 2");
        Course history = new Course();
        history.setName("History");
        Course apHistory = new Course();
        apHistory.setName("AP History");
        Course bio = new Course();
        bio.setName("Biology");
        Course chem = new Course();
        chem.setName("Chemistry");
        Course spanish = new Course();
        spanish.setName("Spanish");
        Course eng = new Course();
        eng.setName("English");
        Course art = new Course();
        art.setName("Art");
        Course phys = new Course();
        phys.setName("Physics");
        Course gym = new Course();
        gym.setName("Gym");
        return Arrays.asList(calc, geo, alg, history, apHistory, bio, chem, spanish, eng, art, phys, gym);
    }
    
    /**
     * Generates and returns a map of school year ID to term instances associated with 
     * the school year ID key.
     * @param schoolYears
     * @return
     */
    public static Map<Long, List<Term>> generateTerms(List<SchoolYear> schoolYears) {
        Map<Long, List<Term>> terms = new HashMap<Long, List<Term>>();
        for(SchoolYear year : schoolYears) {
            terms.put(year.getId(), new ArrayList<Term>());
            
            LocalDate start = year.getStartDate();
            LocalDate end = year.getEndDate();
            
            Term firstTerm = new Term();
            firstTerm.setStartDate(start);
            firstTerm.setEndDate(end.plusMonths(-6l));
            Term secondTerm = new Term();
            secondTerm.setStartDate(end.plusMonths(-6l));
            secondTerm.setEndDate(end);
            
            terms.get(year.getId()).add(firstTerm);
            terms.get(year.getId()).add(secondTerm);
        }
        return terms;
    }
    
    /**
     * Generates and returns a map of course ID to List of Sections associated with that courseId.
     * @param terms
     * @param courses
     * @return
     */
    public static Map<Long, List<Section>> generateSections(
            Collection<Term> terms, 
            List<Course> courses, 
            List<Student> students, 
            List<Staff> teachers) {
        //Static set of grade formulas
        List<GradeFormula> gradeFormulas = new ArrayList<GradeFormula>();
        Map<String, Double> weight1 = new HashMap<String, Double>() {{
            put(AssignmentType.ATTENDANCE.name(), 10D); put(AssignmentType.FINAL.name(), 35D);
            put(AssignmentType.MIDTERM.name(), 25D); put(AssignmentType.HOMEWORK.name(), 30D);
        }};
        Map<String, Double> weight2 = new HashMap<String, Double>() {{
            put(AssignmentType.FINAL.name(), 60D); put(AssignmentType.MIDTERM.name(), 40D);
        }};
        Map<String, Double> weight3 = new HashMap<String, Double>() {{
            put(AssignmentType.LAB.name(), 40D); put(AssignmentType.MIDTERM.name(), 20D);
            put(AssignmentType.FINAL.name(), 30D); put(AssignmentType.QUIZ.name(), 10D);
        }};
        Map<String, Double> weight4 = new HashMap<String, Double>() {{
            put(AssignmentType.TEST.name(), 25D); put(AssignmentType.FINAL.name(), 30D);
            put(AssignmentType.QUIZ.name(), 10D); put(AssignmentType.HOMEWORK.name(), 35D);
        }};
        gradeFormulas.add(new GradeFormula(weight1));
        gradeFormulas.add(new GradeFormula(weight2));
        gradeFormulas.add(new GradeFormula(weight3));
        gradeFormulas.add(new GradeFormula(weight4));
        
        List<String> rooms = new ArrayList<String>(){{
            add("101"); add("102");
            add("103"); add("104");
            add("105"); add("106");
        }};
        int numRooms = rooms.size();
        int sectionsCreated = 0;
        int studentListMidwayPoint = students.size() / 2;
        Map<Long, List<Section>> sectionMap = new HashMap<Long, List<Section>>();
        for(Term t : terms) {
            for(Course c : courses) {
                sectionsCreated++;
                Section section = new Section(
                        t.getStartDate(), 
                        t.getEndDate(), 
                        rooms.get(new Random().nextInt(numRooms)),
                        gradeFormulas.get(new Random().nextInt(gradeFormulas.size())),
                        0,
                        new HashMap<String, ArrayList<Long>>());
                section.setCourse(c);
                section.setTerm(t);
                section.setName(getRandomSectionName());
                section.setEnrolledStudents(new ArrayList<Student>());
                //Add an alternating half of students to each section
                for(int i = studentListMidwayPoint * (sectionsCreated % 2); 
                        i < students.size() - studentListMidwayPoint * ((sectionsCreated + 1) % 2); 
                        i++) {
                    section.getEnrolledStudents().add(students.get(i));
                }
                //Assign a random teacher to be the teacher of the class
                int teacherIndex = new Random().nextInt(teachers.size() - 1);
                for(int i = 0; i <= teacherIndex; i++) {
                    if(teacherIndex == i) {
                        Set<Staff> sectionTeacher = new HashSet<>();
                        sectionTeacher.add(teachers.get(i));
                        section.setStaffs(sectionTeacher);
                    }
                }
                
                if(null == sectionMap.get(t.getId())) {
                    sectionMap.put(t.getId(), new ArrayList<Section>());
                }
                sectionMap.get(t.getId()).add(section);
            }
        }
        return sectionMap;
    }

    private static final ArrayList<String> sampleSectionNames = new ArrayList<String>(){{
        add("World History 2(B) 4(E-F) 7(A,C-D)");
        add("World History 3(E-F) 4(C-D) 6(A-B)");
        add("World History 2(F) 3(B) 7(E) 8(A,C-D)");
        add("World History 2(E) 3(C) 4(A-B,F) 6(D)");
        add("Study Hall 9(A-E)");
        add("Biology support 4(B)");
        add("Introduction to Lab Science 4(A) 6(D)");
        add("Foundations of Art 8(A,D)");
        add("Foundations of Art 7(A,D)");
        add("ELD-Reading & Writing 3(C) 4(A-B) 6(D) 7(E)");
        add("ELD 1 2(B) 7(A,C-D)");
        add("Spanish Literature 1 2(B) 7(A,C-D)");
        add("Advisory 1(E)");
        add("Algebra support 3(B)");
        add("Geometry support 3(B)");
        add("History support 4(C)");
        add("History support 8(A)");
        add("6th Grade Art 10(D)");
        add("Biology support 6(B)");
        add("Biology support 8(D)");
        add("English support 4(D)");
        add("7th Grade Art 6(A)");
        add("English support 8(C)");
        add("Study Hall 2(A,D-E) 6(C) 7(B)");
        add("5th Grade Math 3(A-B,D-E) 9(B-C) 10(A,D)");
        add("7th Grade Art 9(A)");
        add("5th Grade Morning Activities 1(A-E)");
        add("Lunch 5(A-E)");
        add("8th Grade Art 9(B)");
        add("5th Grade English 2(B-E) 6(B-D) 9(A)");
        add("8th Grade Art 10(B)");
        add("6th Grade Morning Activities 1(A-E)");
        add("5th Grade Science 2(A) 3(C) 5(B,E) 6(A) 9(D) 10(B-C)");
        add("5th Grade Fitness 9(D)");
        add("Spanish 1 4(C-D) 6(A-B)");
        add("5th Grade Social Studies 2(A) 3(C) 5(B,E) 6(A) 9(D) 10(B-C)");
        add("5th Grade Fitness 10(D)");
        add("7th Grade Morning Activities 1(A-E)");
        add("6th Grade Fitness 9(C)");
        add("5th Grade Math 2(A-B,D-E) 6(A-B,D) 10(C)");
        add("ELD History 3(E) 4(C-D) 6(A-B)");
        add("8th Grade Morning Activities 1(A-E)");
        add("6th Grade Fitness 10(C)");
        add("Physical Education 9 4(C)");
        add("5th Grade English 5(B-E) 9(C-D) 10(A-B)");
        add("7th Grade Fitness 9(B)");
        add("Study Hall 6(A)");
        add("Physical Education 9 8(C)");
        add("5th Grade Break 4(A-E)");
        add("7th Grade Fitness 10(B)");
        add("5th Grade Science 2(C) 3(B,E) 5(A) 6(C) 9(A-B) 10(D)");
        add("Study Hall 6(A,D)");
        add("Physical Education 9 3(C)");
        add("5th Grade Social Studies 2(C) 3(B,E) 5(A) 6(C) 9(A-B) 10(D)");
        add("8th Grade Fitness 9(A)");
        add("Study Hall 2(A,D) 3-4(E) 6(C-D) 7(B)");
        add("Physical Education 9 7(C)");
        add("6th Grade Break 4(A-E)");
        add("8th Grade Fitness 10(A)");
        add("Study Hall 2(B)");
        add("6th Grade Math 2(D-E) 3(A-B) 9(C-D) 10(A-B)");
        add("7th Grade Break 4(A-E)");
        add("English support 7(C)");
        add("English support 6(D)");
        add("History support 3(C)");
        add("History support 7(A)");
        add("Geometry support 4(A)");
        add("Biology support 7(D)");
        add("English Skills 1 4(C-D) 6(A-B)");
        add("8th Grade Break 4(A-E)");
        add("6th Grade English 5(A,C-E) 6(C-D) 9(A-B)");
        add("Life Skills 1 4(A)");
        add("Math 1 2(C) 3(A,D) 6(E-F) 8(B)");
        add("Geometry 2(C) 3(A,D) 6(E-F) 8(B)");
        add("5th Grade Lunch 7(A-E)");
        add("6th Grade Science 2(A-C) 3(E) 6(A-B) 10(C-D)");
        add("Geometry 3(F) 4(C-D) 6(A-B) 7(E)");
        add("6th Grade Lunch 7(A-E)");
        add("Biology 3(B,E) 6(F) 8(A,C-D)");
        add("6th Grade Social Studies 2(A-C) 3(E) 6(A-B) 10(C-D)");
        add("7th Grade Lunch 8(A-D) 7(E)");
        add("Biology 2(C) 3(A,D) 4(E) 6(F) 8(B)");
        add("5th Grade Math-Modified 5(B-C,E) 10(A-B,D)");
        add("6th Grade Math 2(A-B) 3(E) 6(A-B,D) 10(C-D)");
        add("5th Grade English-Modified 2(E) 5(A,D) 6(B-C)");
        add("Biology 2(B,E) 3(F) 7(A,C-D)");
        add("8th Grade Lunch 8(A-D) 7(E)");
        add("Modified Math 8 2(A-E) 6(A-B,D)");
        add("6th Grade English 2(C-E) 3(A,C-D) 10(A-B)");
        add("5th Grade DEAR 8(A-D)");
        add("College Readiness 9 6(B)");
        add("College Readiness 9 3(B)");
        add("6th Grade Science 5(A-B,E) 6(C) 9(A-D)");
        add("6th Grade DEAR 8(A-D)");
        add("College Readiness 9 4(B)");
        add("College Readiness 9 2(B)");
        add("6th Grade Social Studies 5(A-B,E) 6(C) 9(A-D)");
        add("7th Grade DEAR 7(A-D)");
        add("Model United Nations 4(D) 6(A)");
        add("Geometry 2(A,D-F) 6(C) 7(B)");
        add("7th Grade Math 3(E) 5-6(A-C) 10(D)");
        add("8th Grade DEAR 7(A-D)");
        add("5th Grade PM Homeroom 11(A-E)");
        add("7th Grade English 2(B-E) 3(B-D) 10(A)");
        add("English 9 2(C) 3(A,D,F) 6-7(E) 8(B)");
        add("English 9 2(A,D) 3(E) 4(E-F) 6(C) 7(B)");
        add("6th Grade PM Homeroom 11(A-E)");
        add("Intermediate Spanish 1 3(C) 4(A-B) 6(D)");
        add("7th Grade Science 2-3(A) 5(D-E) 6(D) 9(C) 10(B-C)");
        add("Intermediate Spanish 1 3(B) 8(A,C-D)");
        add("7th Grade PM Homeroom 11(A-E)");
        add("7th Grade Social Studies 2-3(A) 5(D-E) 6(D) 9(C) 10(B-C)");
        add("8th Grade PM Homeroom 11(A-E)");
        add("Algebra I 2(A,D-F) 6(C) 7(B)");
        add("7th Grade English 5(B-E) 6(B-D) 9(A)");
        add("Algebra I 3(F) 4(C-D) 6(A-B) 7(E)");
        add("5th Grade Focus 12(A-D)");
        add("Algebra support 4(A)");
        add("Biology 2(A,D,F) 6(C,E) 7(B)");
        add("7th Grade Science 2(C-D) 3(C-E) 5-6(A) 9(B)");
        add("Study Hall 4(E)");
        add("6th Grade Focus 12(A-D)");
        add("7th Grade Social Studies 2(C-D) 3(C-E) 5-6(A) 9(B)");
        add("7th Grade Focus 12(A-D)");
        add("5th Grade DEAR 8(A-E)");
        add("8th Grade English 5(B-C,E) 6(B-C) 9(A,D) 10(D)");
        add("8th Grade Focus 12(A-D)");
        add("5th Grade Life Skills 6(A)");
        add("8th Grade Science 2(C) 3(C,E) 5-6(A,D) 9(B)");
        add("5th Grade Life Skills 9(A)");
        add("8th Grade Social Studies 2(C) 3(C,E) 5-6(A,D) 9(B)");
        add("5th Grade Computer Science 6(C)");
        add("5th Grade Computer Science 9(C)");
        add("8th Grade Math 3(E) 5-6(A-B,D) 10(C)");
        add("5th Grade Tech & Englneering 6(C)");
        add("5th Grade Tech & Englneering 9(C)");
        add("8th Grade English 2(B-E) 3(B-D) 10(A)");
        add("6th Grade Tech & Engineering 3(D)");
        add("8th Grade Science 2-3(A) 5(C,E) 6(C) 9(D) 10(B,D)");
        add("6th Grade Tech & Engineering 5(D)");
        add("6th Grade Computer Science 3(D)");
        add("ELL 6 12(B,D)");
        add("8th Grade Social Studies 2-3(A) 5(C,E) 6(C) 9(D) 10(B,D)");
        add("5th Grade Focus 12(A-E)");
        add("6th Grade Computer Science 5(D)");
        add("ELL 5 and 6 1(B,D)");
        add("7th Grade Math 2(A-B,E) 3(A-B) 9(C-D) 10(C)");
        add("ELL  5 12(A,C)");
        add("6th Grade Leadership 3(D)");
        add("6th Grade Leadership 5(D)");
        add("8th Grade Math 2(A-B,D-E) 3(A-B,D) 9(C)");
        add("6th Grade Tech & Engineering 6(B)");
        add("7th Grade Computer Science 10(B)");
        add("7th Grade Computer Science 6(B)");
        add("6th Grade Tech & Engineering 9(B)");
        add("7th Grade Public Speaking 10(B)");
        add("7th Grade Public Speaking 6(B)");
        add("5th Grade Computer Science 6(A)");
        add("8th Grade Public Speaking 3(A)");
        add("5th Grade Computer Science 9(A)");
        add("8th Grade Public Speaking 5(A)");
        add("5th Grade Tech & Englneering 6(A)");
        add("8th Grade Tech & Engineering 3(A)");
        add("5th Grade Tech & Englneering 9(A)");
        add("8th Grade Tech & Engineering 5(A)");
        add("8th Grade HS Transition 3(A)");
        add("8th Grade HS Transition 5(A)");
        add("6th Grade Computer Science 6(B)");
        add("5th Grade Math 2(A-E) 6(A-B,D)");
        add("6th Grade Computer Science 9(B)");
        add("5th Grade Math 3(A-E) 9(A-B,D)");
        add("5th Grade English 5(A-E) 10(B-D)");
        add("6th Grade Leadership 6(B)");
        add("5th Grade English 2(A-E) 6(B-D)");
        add("6th Grade Leadership 9(B)");
        add("5th Grade Science 3(A-C,E) 9(A-D)");
        add("7th Grade Computer Science 10(D)");
        add("5th Grade Science 5(A-C,E) 10(A-D)");
        add("7th Grade Computer Science 6(D)");
        add("5th Grade Social Studies 3(A-C,E) 9(A-D)");
        add("5th Grade Social Studies 5(A-C,E) 10(A-D)");
        add("6th Grade Math 2(A-E) 6(A,B,D)");
        add("5th Grade Life Skills 5(A)");
        add("6th Grade Math 3(A-E) 9(A,B,D)");
        add("5th Grade Life Skills 3(A)");
        add("6th Grade English 5(A-E) 10(A,C,D)");
        add("7th Grade Public Speaking 10(D)");
        add("5th Grade Computer Science 5(A)");
        add("6th Grade English 2(A-E) 6(A,C,D)");
        add("7th Grade Public Speaking 6(D)");
        add("5th Grade Computer Science 3(A)");
        add("8th Grade Public Speaking 10(C)");
        add("5th Grade Tech & Englneering 5(A)");
        add("6th Grade Science 3(A,B,C,E) 9(A-D)");
        add("8th Grade Public Speaking 6(C)");
        add("5th Grade Tech & Englneering 3(A)");
        add("6th Grade Science 5(A,B,C,E) 10(A-D)");
        add("8th Grade Tech & Engineering 10(C)");
        add("6th Grade Social Studies 3(A,B,C,E) 9(A-D)");
        add("8th Grade Tech & Engineering 6(C)");
        add("6th Grade Social Studies 5(A,B,C,E) 10(A-D)");
        add("7th Grade Math 2(A-E) 6(A,B,C)");
        add("6th Grade Tech & Engineering 5(B)");
        add("7th Grade Math 3(A-E) 9(A,B,C)");
        add("6th Grade Tech & Engineering 3(B)");
        add("8th Grade HS Transition 10(C)");
        add("7th Grade English 5(A-E) 10(A,C,D)");
        add("6th Grade Computer Science 5(B)");
        add("8th Grade HS Transition 6(C)");
        add("6th Grade Computer Science 3(B)");
        add("5th Grade Math 5(A-E) 10(A-B,D)");
        add("7th Grade English 2(A-E) 6(A,C,D)");
        add("7th Grade Science 3(B-E) 9(A-D)");
        add("6th Grade Leadership 5(B)");
        add("7th Grade Science 5(B-E) 10(A-D)");
        add("6th Grade Leadership 3(B)");
        add("7th Grade Social Studies 3(B-E) 9(A-D)");
        add("5th Grade English 3(A-E) 9(B-D)");
        add("7th Grade Social Studies 5(B-E) 10(A-D)");
        add("7th Grade Computer Science 9(A)");
        add("5th Grade Science 3(A-E) 9(A-C)");
        add("8th Grade Math 2(A-E) 6(A,B,D)");
        add("7th Grade Computer Science 10(A)");
        add("5th Grade Science 5(A-E) 10(A-C)");
        add("8th Grade Math 3(A-E) 9(A,B,D)");
        add("5th Grade Social Studies 3(A-E) 9(A-C)");
        add("5th Grade Social Studies 5(A-E) 10(A-C)");
        add("8th Grade English 5(A-E) 10(A,C,D)");
        add("6th Grade Math 5(A-E) 10(A,B,D)");
        add("8th Grade English 2(A-E) 6(A,C,D)");
        add("8th Grade Science 3(B-E) 9(A-D)");
        add("8th Grade Science 5(B-E) 10(A-D)");
        add("7th Grade Public Speaking 9(A)");
        add("8th Grade Social Studies 3(B-E) 9(A-D)");
        add("7th Grade Public Speaking 10(A)");
        add("8th Grade Social Studies 5(B-E) 10(A-D)");
        add("6th Grade English 3(A-E) 9(A,C-D)");
        add("8th Grade Public Speaking 10(B)");
        add("5th Grade Art 3(E)");
        add("8th Grade Public Speaking 9(B)");
        add("6th Grade Science 3(A-E) 9(A,B) 10(C)");
        add("5th Grade Art 5(E)");
        add("6th Grade Science 5(A-E) 10(A,B) 6(C)");
        add("8th Grade Tech & Engineering 10(B)");
        add("6th Grade Art 10(B)");
        add("6th Grade Social Studies 3(A-E) 9(A,B) 10(C)");
        add("8th Grade Tech & Engineering 9(B)");
        add("6th Grade Art 6(B)");
        add("6th Grade Social Studies 5(A-E) 10(A,B) 6(C)");
        add("7th Grade Art 3(A)");
        add("5th Grade ELD 2(C) 3(B,E) 6(C)");
        add("7th Grade Math 5(A-E) 10(A-C)");
        add("7th Grade Art 5(A)");
        add("ELD DEAR 7(A-D)");
        add("7th Grade Math 2(A-E) 6(A-C)");
        add("8th Grade Art 6(C)");
        add("ELD DEAR 8(A-D)");
        add("8th Grade HS Transition 10(B)");
        add("7th Grade English 2(A-E) 6(B-D)");
        add("8th Grade Art 9(C)");
        add("8th Grade HS Transition 9(B)");
        add("7th Grade English 3(A-E) 9(B-D)");
        add("5th Grade Fitness 10(A)");
        add("STARI English 2(B-E) 3(B-D) 10(A)");
        add("5th Grade Fitness 6(A)");
        add("7th Grade Science 3(A-E) 9(A,C,D)");
        add("Modified Math 8 3(E) 5-6(A-B,D) 10(C)");
        add("Modified Math 6 2(E) 3(A-B,D) 9(B-D) 10(A)");
        add("6th Grade Fitness 6(C)");
        add("7th Grade Science 5(A-E) 10(A,C,D)");
        add("DC Math 2(A) 3(A,E) 6(B,D) 10(B-D)");
        add("Social Studies Skills 6 2(A-C) 3(E) 6(A-B) 9(C) 10(D)");
        add("Math Skills 6 2(D-E) 3(A-B) 6(C) 9(D) 10(A-C)");
        add("7th Grade Social Studies 3(A-E) 9(A,C,D)");
        add("7th Grade Fitness 6(D)");
        add("English Skills 6 5(A-E) 6(D) 9(A-B)");
        add("7th Grade Social Studies 5(A-E) 10(A,C,D)");
        add("7th Grade Fitness 9(D)");
        add("5th Grade Fitness 5(C)");
        add("8th Grade Math 5(A-E) 10(A-B,D)");
        add("8th Grade Fitness 10(B)");
        add("5th Grade Fitness 3(C)");
        add("8th Grade Fitness 6(B)");
        add("8th Grade Math 2(A-E) 6(A-B,D)");
        add("6th Grade Fitness 3(D)");
        add("7th Grade Modified Math 6(A) 2(C) 6(C,D)");
        add("8th Grade English 2(A-E) 6(B-D)");
        add("7th Grade Modified English 5(B,D,E)");
        add("6th Grade Fitness 5(D)");
        add("6th & 7th Grade Modified SS 3(A-E)");
        add("6th & 7th Grade Modified Science 3(A-E)");
        add("8th Grade English 3(A-E) 9(B-D)");
        add("8th Grade Modified Math 2(A-E)");
        add("6th Grade Modified English 2(A-D)");
        add("7th & 8th Grade Modified Math 2(D-E) 9(A-B,D)");
        add("6th Grade Modified Math 5(A,E) 9(B)");
        add("8th Grade Science 3(A-E) 6(A) 9(C-D)");
        add("8th Grade Science 5(A-E) 9(A) 10(C-D)");
        add("8th Grade Social Studies 3(A-E) 6(A) 9(C-D)");
        add("8th Grade Social Studies 5(A-E) 9(A) 10(C-D)");
        add("5th Grade Art 10(C)");
        add("5th Grade Art 6(C)");
        add("6th Grade Art 9(D)");
        add("5th Grade Art 5(D)");
        add("5th Grade Art 3(D)");
        add("7th Grade Art 9(D)");
        add("7th Grade Art 10(D)");
        add("6th Grade Art 3(C)");
        add("6th Grade Art 5(C)");
        add("8th Grade Art 10(C)");
        add("7th Grade Modified Math 3(A-E)");
    }};
    
    private static String getRandomSectionName() {
        return sampleSectionNames.get(random.nextInt(sampleSectionNames.size()));
    }

    /**
     * Generates and returns a map of section ID to a list of assignments generated for 
     * that section.
     * @param sections
     * @return
     */
    public static Map<Long, List<Assignment>> generateAssignments(List<Section> sections) {
        Map<Long, List<Assignment>> returnMap = new HashMap<Long, List<Assignment>>();
        for(Section s: sections) {
            returnMap.put(s.getId(), new ArrayList<Assignment>());
            LocalDate startDate = s.getStartDate();
            LocalDate endDate = s.getEndDate();
            int diffInDays = (int) ChronoUnit.DAYS.between(startDate, endDate);
            //Create HW Assignments & Attendance assignments
            for(int i = 0; i < diffInDays; i = i + 3) {
                LocalDate assignmentDate = startDate.plusDays(i);
                AttendanceAssignment attend = new AttendanceAssignment();
                attend.setAvailablePoints(1L);
                attend.setDueDate(assignmentDate);
                returnMap.get(s.getId()).add(attend);
                //For every other day, add a homework assignment
                if(i % 6 == 0) {
                    GradedAssignment hw = new GradedAssignment();
                    hw.setAssignedDate(s.getStartDate());
                    hw.setDueDate(assignmentDate);
                    hw.setAvailablePoints(5L);
                    hw.setType(AssignmentType.HOMEWORK);
                    returnMap.get(s.getId()).add(hw);
                }
                
            }
            //Create midterm
            GradedAssignment midterm = new GradedAssignment();
            midterm.setDueDate(startDate.plusDays(diffInDays/2));
            midterm.setType(AssignmentType.MIDTERM);
            midterm.setAvailablePoints(100L);
            midterm.setAssignedDate(startDate);
            returnMap.get(s.getId()).add(midterm);
            
            //Create final
            GradedAssignment fin = new GradedAssignment();
            fin.setDueDate(endDate.plusDays(-2l));
            fin.setType(AssignmentType.FINAL);
            fin.setAvailablePoints(100L);
            fin.setAssignedDate(startDate);
            returnMap.get(s.getId()).add(fin);
            
            //TODO: Create Labs or quizes?
        }
        return returnMap;
    }
    
    /**
     * Generates and returns a map of assignment ID to a list of StudentAssignment instances
     * associated with the assignment.
     * @param assignments
     * @param students
     * @return
     */
    public static Map<Long, List<StudentAssignment>>
            generateStudentAssignments(List<Assignment> assignments, List<Student> students) {
        Map<Long, List<StudentAssignment>> assIdToStudAss = 
                new HashMap<Long, List<StudentAssignment>>();
        for(Assignment a : assignments) {
            assIdToStudAss.put(a.getId(), new ArrayList<StudentAssignment>());
            for(Student s: students) {
                StudentAssignment sa = new StudentAssignment();
                sa.setAssignment(a);
                if(null != a.getAvailablePoints() && a.getAvailablePoints() > 0) {
                    Integer awardedInt = new Random().nextInt(40);
                    awardedInt = ((int)(long)a.getAvailablePoints()) - awardedInt;
                    if(awardedInt < 0) {
                        awardedInt = ((int)(long)a.getAvailablePoints()) -
                                new Random().nextInt((int)(long)a.getAvailablePoints());
                    }
                    sa.setAwardedPoints(awardedInt.doubleValue());
                    sa.setCompletionDate(a.getDueDate());
                }
                sa.setStudent(s);
                assIdToStudAss.get(a.getId()).add(sa);
            }
        }
        return assIdToStudAss;
    }
    
    /**
     * Generates a map of student ID to a list of behavior events associated with the student having that 
     * student ID.
     * @param students
     * @param teachers
     * @return
     */
    public static Map<Long, ArrayList<Behavior>> generateBehaviorEvents(
            Collection<Student> students, 
            List<Staff> teachers,
            LocalDate beginDate,
            LocalDate endDate) {
        int numDates = (int)Math.abs(ChronoUnit.DAYS.between(beginDate, endDate));
        Map<Long, ArrayList<Behavior>> studentBehaviors = new HashMap<Long, ArrayList<Behavior>>();
        for(Student s: students) {
            int numEventsToProduce = new Random().nextInt(numDates/2);
            studentBehaviors.put(s.getId(), new ArrayList<Behavior>());
            for(int i = 0; i < numEventsToProduce; i++) {
                int teacherIndex = new Random().nextInt(teachers.size() - 1);
                Staff t = teachers.get(teacherIndex);
                LocalDate local = beginDate.plusDays(RandomUtils.nextLong(0l, numDates));
                Behavior b = new Behavior();
                b.setBehaviorDate(local);
                b.setBehaviorCategory(BehaviorCategory.DEMERIT);
                b.setPointValue("-2");
                b.setAssigner(t);
                b.setStudent(s);
                studentBehaviors.get(s.getId()).add(b);
            }
        }
        return studentBehaviors;
    }

    public static Map<Long, ArrayList<Goal>> generateGoalEvents(
            Collection<Student> students,
            Staff teacher,
            LocalDate beginDate,
            LocalDate endDate,
            Map<Long, List<Long>> studentToSSGId,
            Map<Long, List<Long>> studentToAssignmentId
    ) {
        Map<Long, ArrayList<Goal>> studentGoals = new HashMap<Long, ArrayList<Goal>>();
        for (Student s: students) {
            List<Long> enrolledSections = studentToSSGId.get(s.getId());
            List<Long> studentAssignments = studentToAssignmentId.get(s.getId());
            int index = ThreadLocalRandom.current().nextInt(1, enrolledSections.size() - 1);
            int assignmentIndex = ThreadLocalRandom.current().nextInt(studentAssignments.size()-1);
            ArrayList<Goal> studentGoalList = new ArrayList<Goal>();

            CumulativeGradeGoal sectionGradeGoal = new CumulativeGradeGoal();

            if (null == enrolledSections.get(index)) {
                sectionGradeGoal.setParentId(enrolledSections.get(0));
            } else {
                sectionGradeGoal.setParentId(enrolledSections.get(index));
            }

            sectionGradeGoal.setStudent(s);
            sectionGradeGoal.setStaff(teacher);
            sectionGradeGoal.setApproved(false);
            sectionGradeGoal.setDesiredValue(Double.valueOf(ThreadLocalRandom.current().nextInt(75, 100)));
            sectionGradeGoal.setName("Section Grade Goal");
            studentGoalList.add(sectionGradeGoal);

            BehaviorGoal behaviorGoal = new BehaviorGoal();
            behaviorGoal.setStudent(s);
            behaviorGoal.setStaff(teacher);
            behaviorGoal.setApproved(false);
            behaviorGoal.setDesiredValue(Double.valueOf(ThreadLocalRandom.current().nextInt(0, 60)));
            behaviorGoal.setName("Weekly Demerit Goal");
            behaviorGoal.setEndDate(endDate);
            behaviorGoal.setStartDate(beginDate);
            behaviorGoal.setBehaviorCategory(BehaviorCategory.DEMERIT);
            studentGoalList.add(behaviorGoal);

            AssignmentGoal assignmentGoal = new AssignmentGoal();
            if (studentAssignments.get(assignmentIndex) == null) {
                assignmentGoal.setParentId(studentAssignments.get(0));
            } else {
                assignmentGoal.setParentId(studentAssignments.get(assignmentIndex));
            }


            assignmentGoal.setStudent(s);
            assignmentGoal.setStaff(teacher);
            assignmentGoal.setApproved(false);
            assignmentGoal.setDesiredValue(Double.valueOf(ThreadLocalRandom.current().nextInt(75, 100)));
            assignmentGoal.setName("Bio Final Goal");
            studentGoalList.add(assignmentGoal);

            studentGoals.put(s.getId(), studentGoalList);

            AttendanceGoal attendanceGoal = new AttendanceGoal();
            if (null == enrolledSections.get(index)) {
                attendanceGoal.setParentId(enrolledSections.get(0));
            } else {
                attendanceGoal.setParentId(enrolledSections.get(index));
            }

            attendanceGoal.setStudent(s);
            attendanceGoal.setStaff(teacher);
            attendanceGoal.setApproved(false);
            attendanceGoal.setDesiredValue(Double.valueOf(ThreadLocalRandom.current().nextInt(0, 4)));
            attendanceGoal.setName("Weekly Attendance Goal");
            attendanceGoal.setEndDate(endDate);
            attendanceGoal.setStartDate(beginDate);

            studentGoalList.add(attendanceGoal);

        }
        return studentGoals;
    }
}
