package com.scholarscore.api.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.scholarscore.models.Assignment;
import com.scholarscore.models.AssignmentType;
import com.scholarscore.models.AttendanceAssignment;
import com.scholarscore.models.Behavior;
import com.scholarscore.models.BehaviorCategory;
import com.scholarscore.models.Course;
import com.scholarscore.models.Gender;
import com.scholarscore.models.GradeFormula;
import com.scholarscore.models.GradedAssignment;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.StudentAssignment;
import com.scholarscore.models.Term;
import com.scholarscore.models.query.Measure;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.Teacher;

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
        int year = 115;
        int startMonth = 8;
        int endMonth = 5;
        int day = 1;
        ArrayList<SchoolYear> years = new ArrayList<SchoolYear>();
        for(int i = 0; i < 1; i++) {
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
    public static Map<Long, List<Term>> generateTerms(List<SchoolYear> schoolYears) {
        Map<Long, List<Term>> terms = new HashMap<Long, List<Term>>();
        for(SchoolYear year : schoolYears) {
            terms.put(year.getId(), new ArrayList<Term>());
            
            Date start = year.getStartDate();
            Date end = year.getEndDate();
            
            Calendar endCalendar = new GregorianCalendar();
            endCalendar.setTime(end);
            int endYear = endCalendar.get(Calendar.YEAR) - 1900;
            
            Term firstTerm = new Term();
            firstTerm.setStartDate(start);
            firstTerm.setEndDate(new Date(endYear - 1, 12, 31));
            Term secondTerm = new Term();
            secondTerm.setStartDate(new Date(endYear, 1, 1));
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
            put(AssignmentType.TEST, 25); put(AssignmentType.FINAL, 30);
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
                        gradeFormulas.get(new Random().nextInt(gradeFormulas.size())));
                section.setCourse(c);
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
            Date startDate = s.getStartDate();
            Date endDate = s.getEndDate();
            int diffInDays = (int) ((endDate.getTime() - startDate.getTime())/ DAY_IN_MILLIS );
            //Create HW Assignments & Attendance assignments
            for(int i = 0; i < diffInDays; i = i + 3) {
                Calendar c = Calendar.getInstance();
                c.setTime(startDate); // Now use today date.
                c.add(Calendar.DATE, i);
                Date assignmentDate = c.getTime();
                AttendanceAssignment attend = new AttendanceAssignment();
                attend.setDueDate(assignmentDate);
                returnMap.get(s.getId()).add(attend);
                //For every other day, add a homework assignment
                if(i % 6 == 0) {
                    GradedAssignment hw = new GradedAssignment();
                    hw.setAssignedDate(s.getStartDate());
                    hw.setDueDate(assignmentDate);
                    hw.setType(AssignmentType.HOMEWORK);
                    returnMap.get(s.getId()).add(hw);
                }
                
            }
            //Create midterm
            Calendar c = Calendar.getInstance();
            c.setTime(startDate); // Now use today date.
            c.add(Calendar.DATE, diffInDays/2);
            GradedAssignment midterm = new GradedAssignment();
            midterm.setDueDate(c.getTime());
            midterm.setType(AssignmentType.MIDTERM);
            midterm.setAvailablePoints(100L);
            midterm.setAssignedDate(startDate);
            returnMap.get(s.getId()).add(midterm);
            
            //Create final
            c.setTime(endDate); // Now use today date.
            c.add(Calendar.DATE, -2);
            GradedAssignment fin = new GradedAssignment();
            fin.setDueDate(c.getTime());
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
                Boolean completed = true;
                if(a.getId() % 2 == 0 && a.getId() % 3 == 0) {
                    completed = new Random().nextBoolean();
                }
                sa.setCompleted(completed);
                if(null != a.getAvailablePoints() && a.getAvailablePoints() > 0 && completed) {
                    Integer awardedInt = new Random().nextInt(40);
                    awardedInt = ((int)(long)a.getAvailablePoints()) - awardedInt;
                    sa.setAwardedPoints(awardedInt.longValue());
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
     * @param dates
     * @return
     */
    public static Map<Long, ArrayList<Behavior>> generateBehaviorEvents(
            Collection<Student> students, 
            List<Teacher> teachers, 
            Date beginDate,
            Date endDate) {
        int numDates = (int)( (endDate.getTime() - beginDate.getTime()) / (1000 * 60 * 60 * 24));
        Map<Long, ArrayList<Behavior>> studentBehaviors = new HashMap<Long, ArrayList<Behavior>>();
        for(Student s: students) {
            int numEventsToProduce = new Random().nextInt(numDates/2);
            studentBehaviors.put(s.getId(), new ArrayList<Behavior>());
            for(int i = 0; i < numEventsToProduce; i++) {
                int teacherIndex = new Random().nextInt(teachers.size() - 1);
                Teacher t = teachers.get(teacherIndex);
                long inputTs = beginDate.getTime() + ((long) (Math.random() * (endDate.getTime() - beginDate.getTime())));
                inputTs = (inputTs / 1000L) * 1000L;
                Date d = new Date(inputTs);
                Behavior b = new Behavior();
                b.setBehaviorDate(d);
                b.setBehaviorCategory(BehaviorCategory.DEMERIT);
                b.setTeacher(t);
                b.setStudent(s);
                studentBehaviors.get(s.getId()).add(b);
            }
        }
        return studentBehaviors;
    }
}
