package com.scholarscore.api.controller;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.Term;
import com.scholarscore.models.assignment.AssignmentType;
import com.scholarscore.models.assignment.GradedAssignment;
import com.scholarscore.models.assignment.StudentAssignment;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.Teacher;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

@Test(groups = { "integration" })
public class StudentAssignmentControllerIntegrationTest extends IntegrationBase {
    private int numberOfItemsCreated = 0;
    private School school;
    private SchoolYear schoolYear;
    private Term term;
    private Course course;
    private Section section;
    private GradedAssignment sectionAssignment;
    private Student student;
    private Teacher teacher;
    
    @BeforeClass
    public void init() {
        authenticate();
        numberOfItemsCreated = 0;
        school = new School();
        school.setName(localeServiceUtil.generateName());
        school = schoolValidatingExecutor.create(school, "Create base school");
        
        teacher = new Teacher();
        teacher.setName("Mr. Jones");
        teacher = teacherValidatingExecutor.create(teacher, "Create a base teacher");
        
        student = new Student();
        student.setName(localeServiceUtil.generateName());
        student.setCurrentSchoolId(school.getId());
        student = studentValidatingExecutor.create(student, "create base student");
        
        schoolYear = new SchoolYear();
        schoolYear.setName(localeServiceUtil.generateName());
        schoolYear.setSchool(school);
        schoolYear = schoolYearValidatingExecutor.create(school.getId(), schoolYear, "create base schoolYear");
        
        term = new Term();
        term.setName(localeServiceUtil.generateName());
        term = termValidatingExecutor.create(school.getId(), schoolYear.getId(), term, "create test base term");
        
        course = new Course();
        course.setName(localeServiceUtil.generateName());
        course = courseValidatingExecutor.create(school.getId(), course, "create base course");
        
        section = new Section();
        section.setCourse(course);
        section.setName(localeServiceUtil.generateName());
        section.setEnrolledStudents(new ArrayList<Student>());
        section.getEnrolledStudents().add(student);
        section.setTeachers(new HashSet<Teacher>());
        section.getTeachers().add(teacher);
        section = sectionValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section, "create test base term");
        
        sectionAssignment = new GradedAssignment();
        sectionAssignment.setType(AssignmentType.FINAL);
        sectionAssignment.setName(localeServiceUtil.generateName());
        LocalDate today = LocalDate.now();
        LocalDate nextYear =today.plusYears(1l);
        sectionAssignment.setAssignedDate(today);
        sectionAssignment.setDueDate(nextYear);
        sectionAssignment = (GradedAssignment) sectionAssignmentValidatingExecutor.create(school.getId(), schoolYear.getId(), 
                term.getId(), section.getId(), sectionAssignment, "create test base term");
    }
    
    /**
     * This integration test really might make more sense in the term controller tests, but this test case already sets up 
     * all the dependent entities needed to execute the test, so here it lives.
     */
    @Test
    public void validateStudentsReturnedByTerm() {
        Collection<Student> studs = termValidatingExecutor.getStudentsInTermTaughtByTeacher(
                school.getId(), schoolYear.getId(), term.getId(), teacher.getId(), "return student taught by teacher in term");
        Assert.assertNotNull(studs, "Unexpected null set of students returned for term");
        Assert.assertEquals(studs.size(), 1, "Unexpected number of students returned!");
    }
    
    //Positive test cases
    @DataProvider
    public Object[][] createStudentAssignmentProvider() {
        //StudentAssignment emptyStudentAssignment = new StudentAssignment();
        
        StudentAssignment namedStudentAssignment = new StudentAssignment();
        namedStudentAssignment.setAssignment(sectionAssignment);
        namedStudentAssignment.setStudent(student);
        
        return new Object[][] {
                //{ "Empty section assignment", emptyStudentAssignment },
                { "Named student assignment", namedStudentAssignment },
        };
    }
    
    @Test(dataProvider = "createStudentAssignmentProvider")
    public void deleteStudentAssignmentTest(String msg, StudentAssignment studentAssignment) {
        StudentAssignment createdSection = studentAssignmentValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section.getId(), sectionAssignment.getId(), studentAssignment, msg);
        studentAssignmentValidatingExecutor.delete(school.getId(), schoolYear.getId(), term.getId(), section.getId(), sectionAssignment.getId(), createdSection.getId(), msg);
    }
    
    @Test(dataProvider = "createStudentAssignmentProvider")
    public void replaceStudentAssignmentTest(String msg, StudentAssignment studentAssignment) {
        StudentAssignment createdSection = studentAssignmentValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), 
                section.getId(), sectionAssignment.getId(), studentAssignment, msg);
        StudentAssignment replacement = new StudentAssignment();
        replacement.setStudent(student);
        replacement.setAssignment(sectionAssignment);
        replacement.setId(createdSection.getId());
        studentAssignmentValidatingExecutor.replace(school.getId(), schoolYear.getId(), term.getId(), 
                section.getId(), sectionAssignment.getId(), createdSection.getId(), replacement, msg);
        studentAssignmentValidatingExecutor.delete(school.getId(), schoolYear.getId(), term.getId(),
                section.getId(), sectionAssignment.getId(), createdSection.getId(), msg);
    }
    
    @Test(dataProvider = "createStudentAssignmentProvider")
    public void updateStudentAssignmentTest(String msg, StudentAssignment studentAssignment) {
        StudentAssignment createdSection = studentAssignmentValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), 
                section.getId(), sectionAssignment.getId(), studentAssignment, msg);
        StudentAssignment updatedStudent = new StudentAssignment();
        updatedStudent.setStudent(createdSection.getStudent());
        updatedStudent.setName(localeServiceUtil.generateName());
        //PATCH the existing record with a new name.
        studentAssignmentValidatingExecutor.update(school.getId(), schoolYear.getId(), term.getId(), 
                section.getId(), sectionAssignment.getId(), createdSection.getId(), updatedStudent, msg);

        studentAssignmentValidatingExecutor.delete(school.getId(), schoolYear.getId(), term.getId(),
                section.getId(), sectionAssignment.getId(), createdSection.getId(), msg);
    }
    
    @Test
    public void getAllItems() {
        studentAssignmentValidatingExecutor.getAll(school.getId(), schoolYear.getId(), term.getId(), 
                section.getId(), sectionAssignment.getId(), "Get all records created so far", numberOfItemsCreated++);
    }
    
    //Negative test cases
    @DataProvider
    public Object[][] createStudentAssignmentNegativeProvider() {
        StudentAssignment gradedSectionNameTooLong = new StudentAssignment();
        gradedSectionNameTooLong.setName(localeServiceUtil.generateName(257));
        
        return new Object[][] {
                { "Section with name exceeding 256 char limit", gradedSectionNameTooLong, HttpStatus.BAD_REQUEST }
        };
    }
    
    @Test(dataProvider = "createStudentAssignmentNegativeProvider")
    public void createSectionNegativeTest(String msg, StudentAssignment studentAssignment, HttpStatus expectedStatus) {
        studentAssignmentValidatingExecutor.createNegative(school.getId(), schoolYear.getId(), term.getId(), 
                section.getId(), sectionAssignment.getId(), studentAssignment, expectedStatus, msg);
    }
    
    @Test(dataProvider = "createStudentAssignmentNegativeProvider")
    public void replaceAssignmentNegativeTest(String msg, StudentAssignment studentAssignment, HttpStatus expectedStatus) {
        StudentAssignment st = new StudentAssignment();
        st.setStudent(student);
        st.setAssignment(sectionAssignment);
        StudentAssignment created = studentAssignmentValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), 
                section.getId(), sectionAssignment.getId(), st, msg);
        studentAssignmentValidatingExecutor.replaceNegative(school.getId(), schoolYear.getId(), term.getId(), 
                section.getId(), sectionAssignment.getId(), created.getId(), studentAssignment, expectedStatus, msg);
        studentAssignmentValidatingExecutor.delete(school.getId(), schoolYear.getId(), term.getId(),
                section.getId(), sectionAssignment.getId(), created.getId(), msg);
    }
}
