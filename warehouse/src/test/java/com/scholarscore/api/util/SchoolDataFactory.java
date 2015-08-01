package com.scholarscore.api.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.scholarscore.models.Assignment;
import com.scholarscore.models.Course;
import com.scholarscore.models.Gender;
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
            years.add(new SchoolYear(new Date(year -i, startMonth, day), new Date(year - 1 + 1, endMonth, day)));
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
        return null;
    }
    
    /**
     * Generates and returns a map of school year ID to term instances associated with 
     * the school year ID key.
     * @param schoolYears
     * @return
     */
    public static Map<Long, Term> generateTerms(List<SchoolYear> schoolYears) {
        return null;
    }
    
    /**
     * Generates and returns a map of course ID to List of Sections associated with that courseId.
     * @param terms
     * @param courses
     * @return
     */
    public static Map<Long, List<Section>> 
            generateSections(List<Term> terms, List<Course> courses) {
        return null;
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
