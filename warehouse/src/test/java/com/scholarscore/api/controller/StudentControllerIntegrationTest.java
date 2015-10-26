package com.scholarscore.api.controller;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.StudentSectionGrade;
import com.scholarscore.models.Term;
import com.scholarscore.models.user.Student;
import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.testng.Assert.assertEquals;

@Test(groups = { "integration" })
public class StudentControllerIntegrationTest extends IntegrationBase {
    
    @BeforeClass
    public void init() {
        authenticate();
    }
    
    //Positive test cases
    @DataProvider
    public Object[][] createStudentProvider() {
        Student namedStudent = new Student();
        namedStudent.setName(localeServiceUtil.generateName());
        return new Object[][] {
                { "Named student", namedStudent }
        };
    }
    
    @Test(dataProvider = "createStudentProvider")
    public void createStudentTest(String msg, Student student) {
        studentValidatingExecutor.create(student, msg);
    }
    
    @Test(dataProvider = "createStudentProvider")
    public void deleteStudentTest(String msg, Student student) {
        Student createdStudent = studentValidatingExecutor.create(student, msg);
        studentValidatingExecutor.delete(createdStudent.getId(), msg);
    }
    
    @Test(dataProvider = "createStudentProvider")
    public void replaceStudentTest(String msg, Student student) {
        Student createdStudent = studentValidatingExecutor.create(student, msg);
        studentValidatingExecutor.replace(createdStudent.getId(), createdStudent, msg);
    }
    
    @Test(dataProvider = "createStudentProvider")
    public void updateStudentTest(String msg, Student student) {
        Student createdStudent = studentValidatingExecutor.create(student, msg);
        Student updatedStudent = new Student();
        updatedStudent.setUsername(createdStudent.getUsername());
        updatedStudent.setPassword(createdStudent.getPassword());
        updatedStudent.setId(createdStudent.getUserId());
        updatedStudent.setUserId(createdStudent.getUserId());
        updatedStudent.setName(localeServiceUtil.generateName());
        //PATCH the existing record with a new name.
        studentValidatingExecutor.update(createdStudent.getId(), updatedStudent, msg);
    }
    
    @Test
    public void getAllItems() {
        studentValidatingExecutor.getAll("Get all records created so far");
    }

    @Test
    public void studentGpaTest() {
        School school = new School();
        school.setName(localeServiceUtil.generateName());
        school = schoolValidatingExecutor.create(school, "Create base school");

        SchoolYear schoolYear = new SchoolYear();
        schoolYear.setName(localeServiceUtil.generateName());
        schoolYear.setSchool(school);
        schoolYear = schoolYearValidatingExecutor.create(school.getId(), schoolYear, "create base schoolYear");

        Term term = new Term();
        term.setName(localeServiceUtil.generateName());
        term.setSchoolYear(schoolYear);
        term = termValidatingExecutor.create(school.getId(), schoolYear.getId(), term, "create test base term");

        Student student = new Student();
        student.setName(localeServiceUtil.generateName());
        student = studentValidatingExecutor.create(student, "create base student");

        Course course = new Course();
        course.setName(localeServiceUtil.generateName());
        course.setSchool(school);
        course = courseValidatingExecutor.create(school.getId(), course, "create base course");

        Double[] grades = { 55.0, 70.0, 85.0, 100.0, 100.0, 100.0 };
        for (Double grade : grades) {

            Section section = new Section();
            section.setCourse(course);
            section.setName(localeServiceUtil.generateName());
            section = sectionValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section, "create base section");
            
            StudentSectionGrade studentSectionGrade = new StudentSectionGrade();
            studentSectionGrade.setGrade(grade);
            studentSectionGrade.setStudent(student);
            studentSectionGrade.setSection(section);
            studentSectionGradeValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section.getId(),
                    student.getId(), studentSectionGrade, "create student section grade w/ value " + grade);
        }

        String gpa = studentValidatingExecutor.getGpa(student.getId(), 4);
        assertEquals(gpa, "3.4");
    }

    //Negative test cases
    @DataProvider
    public Object[][] createStudentNegativeProvider() {
        Student gradedStudentNameTooLong = new Student();
        gradedStudentNameTooLong.setName(localeServiceUtil.generateName(257));
        
        return new Object[][] {
                { "Student with name exceeding 256 char limit", gradedStudentNameTooLong, HttpStatus.BAD_REQUEST }
        };
    }
    
    @Test(dataProvider = "createStudentNegativeProvider")
    public void createStudentNegativeTest(String msg, Student student, HttpStatus expectedStatus) {
        studentValidatingExecutor.createNegative(student, expectedStatus, msg);
    }
    
    @Test(dataProvider = "createStudentNegativeProvider")
    public void replaceStudentNegativeTest(String msg, Student student, HttpStatus expectedStatus) {
        Student s = new Student();
        s.setName(UUID.randomUUID().toString());
        Student created = studentValidatingExecutor.create(s, msg);
        studentValidatingExecutor.replaceNegative(created.getId(), student, expectedStatus, msg);
    }
}
