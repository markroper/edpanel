package com.scholarscore.api.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.scholarscore.models.Assignment;
import com.scholarscore.models.AssignmentType;
import com.scholarscore.models.Course;
import com.scholarscore.models.Gender;
import com.scholarscore.models.GradeFormula;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.Student;
import com.scholarscore.models.StudentAssignment;
import com.scholarscore.models.Teacher;
import com.scholarscore.models.Term;

public class SchoolDataFactory {
    private static final int NUM_STUDENTS = 10;
    
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
        Teacher teacher2 = new Teacher();
        teacher2.setName("Mr. Smith");
        Teacher teacher3 = new Teacher();
        teacher3.setName("Mrs. Matthews");
        teachers.add(teacher1);
        teachers.add(teacher2);
        teachers.add(teacher3);
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
        return new ArrayList<Student>(){{
            add(new Student(WHITE, HISPANIC_LATINO, currentSchoolId, Gender.MALE, "Student One", 2017L));
            add(new Student(BLACK, NON_HISPANIC_LATINO, currentSchoolId, Gender.FEMALE, "Studette Two", 2018L));
            add(new Student(WHITE, NON_HISPANIC_LATINO, currentSchoolId, Gender.FEMALE, "Studette Three", 2017L));
            add(new Student(WHITE, HISPANIC_LATINO, currentSchoolId, Gender.MALE, "Studette Four", 2018L));
            add(new Student(BLACK, HISPANIC_LATINO, currentSchoolId, Gender.FEMALE, "Studette Five", 2018L));
            add(new Student(BLACK, NON_HISPANIC_LATINO, currentSchoolId, Gender.MALE, "Studette Six", 2018L));
            add(new Student(WHITE, HISPANIC_LATINO, currentSchoolId, Gender.FEMALE, "Studette Seven", 2018L));
            add(new Student(WHITE, NON_HISPANIC_LATINO, currentSchoolId, Gender.MALE, "Studette Eight", 2017L));
            add(new Student(BLACK, HISPANIC_LATINO, currentSchoolId, Gender.FEMALE, "Studette Nine", 2016L));
            add(new Student(BLACK, PACIFIC_ISLANDER, currentSchoolId, Gender.MALE, "Student Ten", 2016L));
        }};
    }
    
    /**
     * Generates and returns a list of contiguous school years, from the present year 
     * backward in time, associates with the school ID passed in as parameter.
     * @param schoolId
     * @return
     */
    public static List<SchoolYear> generateSchoolYears() {
        int year = 2015;
        int startMonth = 9;
        int endMonth = 6;
        int day = 1;
        ArrayList<SchoolYear> years = new ArrayList<SchoolYear>();
        for(int i = 0; i < 4; i++) {
            years.add(new SchoolYear(new Date(year - i, startMonth, day), new Date(year - i + 1, endMonth, day)));
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
    public static Map<Long, Term> generateTerms(List<SchoolYear> schoolYears) {
        Map<Long, Term> terms = new HashMap<Long, Term>();
        for(SchoolYear year : schoolYears) {
            Date start = year.getStartDate();
            Date end = year.getEndDate();
            
            Calendar endCalendar = new GregorianCalendar();
            endCalendar.setTime(end);
            int endYear = endCalendar.get(Calendar.YEAR);
            
            Term firstTerm = new Term();
            firstTerm.setStartDate(start);
            firstTerm.setEndDate(new Date(endYear - 1, 12, 31));
            Term secondTerm = new Term();
            secondTerm.setStartDate(new Date(endYear, 1, 1));
            secondTerm.setEndDate(end);
            terms.put(year.getId(), firstTerm);
            terms.put(year.getId(), secondTerm);
        }
        return terms;
    }
    
    /**
     * Generates and returns a map of course ID to List of Sections associated with that courseId.
     * @param terms
     * @param courses
     * @return
     */
    public static Map<Long, List<Section>> generateSections(List<Term> terms, List<Course> courses) {
        //Static set of grade formulas
        List<GradeFormula> gradeFormulas = new ArrayList<GradeFormula>();
        Map<AssignmentType, Integer> weight1 = new HashMap<AssignmentType, Integer>() {{
            put(AssignmentType.ATTENDANCE, 10); put(AssignmentType.FINAL, 35);
            put(AssignmentType.MIDTERM, 25); put(AssignmentType.HOMEWORK, 30);
        }};
        Map<AssignmentType, Integer> weight2 = new HashMap<AssignmentType, Integer>() {{
            put(AssignmentType.FINAL, 60); put(AssignmentType.MIDTERM, 40);
        }};
        Map<AssignmentType, Integer> weight3 = new HashMap<AssignmentType, Integer>() {{
            put(AssignmentType.LAB, 40); put(AssignmentType.MIDTERM, 20);
            put(AssignmentType.FINAL, 30); put(AssignmentType.QUIZ, 10);
        }};
        Map<AssignmentType, Integer> weight4 = new HashMap<AssignmentType, Integer>() {{
            put(AssignmentType.GRADED, 25); put(AssignmentType.FINAL, 30);
            put(AssignmentType.QUIZ, 10); put(AssignmentType.HOMEWORK, 35);
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
        Map<Long, List<Section>> sectionMap = new HashMap<Long, List<Section>>();
        for(Term t : terms) {
            for(Course c : courses) {
                Section section = new Section(
                        t.getStartDate(), 
                        t.getEndDate(), 
                        rooms.get(new Random().nextInt(numRooms)), 
                        gradeFormulas.get(new Random().nextInt(gradeFormulas.size())));
                if(null == sectionMap.get(c.getId())) {
                    sectionMap.put(c.getId(), new ArrayList<Section>());
                }
                sectionMap.get(c.getId()).add(section);
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
        return null;
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
        return null;
    }
}
