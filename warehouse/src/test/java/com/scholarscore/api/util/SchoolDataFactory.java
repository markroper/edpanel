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
import com.scholarscore.models.user.Administrator;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.Teacher;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

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
    private static final String WHITE = "White";
    private static final String BLACK = "Black";
    private static final String AMERICAN_INDIAN = "American Indian";
    private static final String ASIAN = "Asian";
    private static final String PACIFIC_ISLANDER = "Pacific Islander";
    private static final String HISPANIC_LATINO = "Hispanic or Latino";
    private static final String NON_HISPANIC_LATINO = "Non-Hispanic or Latino";
    private static final String OTHER = "Other";
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
                                        List<Teacher> createdTeachers,
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

    public static List<Administrator> generateAdmins(Long currentSchoolId) {
        List<Administrator> admins = new ArrayList<>();
        Administrator admin1 = new Administrator();
        admin1.setName("Mark Roper");
        admin1.setUsername("mroper");
        admin1.setPassword("admin");
        admin1.setEnabled(true);
        admin1.setCurrentSchoolId(currentSchoolId);
        admins.add(admin1);
        return admins;
    }
    /**
     * Generates and returns an arbitrary teacher object with the school FK set to 
     * currentSchoolId
     * @param currentSchoolId
     * @return
     */
    public static List<Teacher> generateTeachers(Long currentSchoolId) {
        List<Teacher> teachers = new ArrayList<Teacher>();
        Teacher teacher1 = new Teacher();
        teacher1.setName("Ms. Doe");
        teacher1.setCurrentSchoolId(currentSchoolId);
        Teacher teacher2 = new Teacher();
        teacher2.setName("Mr. Smith");
        teacher2.setCurrentSchoolId(currentSchoolId);
        Teacher teacher3 = new Teacher();
        teacher3.setName("Mrs. Matthews");
        teacher3.setCurrentSchoolId(currentSchoolId);
        teachers.add(teacher1);
        teachers.add(teacher2);
        teachers.add(teacher3);
        for(Teacher s : teachers) {
            s.setUsername(s.getName().split("\\s+")[1]);
            s.setPassword("password");
            s.setEnabled(true);
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
        students.add(new Student(WHITE, NON_HISPANIC_LATINO, currentSchoolId, Gender.FEMALE, "Ashley Brown", 2017L));
        students.add(new Student(WHITE, HISPANIC_LATINO, currentSchoolId, Gender.MALE, "Bernard Slim", 2018L));
        students.add(new Student(BLACK, HISPANIC_LATINO, currentSchoolId, Gender.FEMALE, "Bernadette Slim", 2018L));
        students.add(new Student(BLACK, NON_HISPANIC_LATINO, currentSchoolId, Gender.MALE, "Jason Carter", 2018L));
        students.add(new Student(WHITE, HISPANIC_LATINO, currentSchoolId, Gender.FEMALE, "Yasmine Fort", 2018L));
        students.add(new Student(WHITE, NON_HISPANIC_LATINO, currentSchoolId, Gender.MALE, "Alexander Panagopalous", 2017L));
        students.add(new Student(BLACK, HISPANIC_LATINO, currentSchoolId, Gender.FEMALE, "Claire Martinez", 2016L));
        students.add(new Student(BLACK, PACIFIC_ISLANDER, currentSchoolId, Gender.MALE, "Otto Porter", 2016L));
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
            years.add(new SchoolYear(LocalDate.of(year - 1, startMonth, day), LocalDate.of(year - i, endMonth, day)));
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
            List<Teacher> teachers) {
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
                        0);
                section.setCourse(c);
                section.setTerm(t);
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
                        Set<Teacher> sectionTeacher = new HashSet<Teacher>(); 
                        sectionTeacher.add(teachers.get(i));
                        section.setTeachers(sectionTeacher);
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
            List<Teacher> teachers,
            LocalDate beginDate,
            LocalDate endDate) {
        int numDates = (int)Math.abs(ChronoUnit.DAYS.between(beginDate, endDate));
        Map<Long, ArrayList<Behavior>> studentBehaviors = new HashMap<Long, ArrayList<Behavior>>();
        for(Student s: students) {
            int numEventsToProduce = new Random().nextInt(numDates/2);
            studentBehaviors.put(s.getId(), new ArrayList<Behavior>());
            for(int i = 0; i < numEventsToProduce; i++) {
                int teacherIndex = new Random().nextInt(teachers.size() - 1);
                Teacher t = teachers.get(teacherIndex);
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
            Teacher teacher,
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
            sectionGradeGoal.setTeacher(teacher);
            sectionGradeGoal.setApproved(false);
            sectionGradeGoal.setDesiredValue(Double.valueOf(ThreadLocalRandom.current().nextInt(75, 100)));
            sectionGradeGoal.setName("Section Grade Goal");
            studentGoalList.add(sectionGradeGoal);

            BehaviorGoal behaviorGoal = new BehaviorGoal();
            behaviorGoal.setStudent(s);
            behaviorGoal.setTeacher(teacher);
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
            assignmentGoal.setTeacher(teacher);
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
            attendanceGoal.setTeacher(teacher);
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
