package com.scholarscore.api.controller;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.Student;
import com.scholarscore.models.StudentSectionGrade;
import com.scholarscore.models.Term;

@Test(groups = { "integration" })
public class StudentSectionGradeIntegrationTest extends IntegrationBase {
    private School school;
    private SchoolYear schoolYear;
    private Term term;
    private Section section;
    private Student student;
    private Student student2;
    private Student student3;
    private Course course;
    
    private int numCreated = 0;
    @BeforeClass
    public void init() {
        authenticate();
        school = new School();
        school.setName(localeServiceUtil.generateName());
        school = schoolValidatingExecutor.create(school, "Create base school");
        
        schoolYear = new SchoolYear();
        schoolYear.setName(localeServiceUtil.generateName());
        schoolYear = schoolYearValidatingExecutor.create(school.getId(), schoolYear, "create base schoolYear");
        
        term = new Term();
        term.setName(localeServiceUtil.generateName());
        term = termValidatingExecutor.create(school.getId(), schoolYear.getId(), term, "create test base term");
        
        course = new Course();
        course.setName(localeServiceUtil.generateName());
        course = courseValidatingExecutor.create(school.getId(), course, "create base course");
        
        student = new Student();
        student.setName(localeServiceUtil.generateName());
        student = studentValidatingExecutor.create(student, "create base student");
        
        student3 = new Student();
        student3.setName(localeServiceUtil.generateName());
        student3 = studentValidatingExecutor.create(student3, "create base student");
        
        student2 = new Student();
        student2.setName(localeServiceUtil.generateName());
        student2 = studentValidatingExecutor.create(student2, "create base student");
        
        section = new Section();
        section.setCourse(course);
        section.setName(localeServiceUtil.generateName());
        section = sectionValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section, "create test base term");
    }
    
    //Positive test cases
    @DataProvider
    public Object[][] createStudentSectionGradeProvider() {
        StudentSectionGrade emptyStudentSectionGrade = new StudentSectionGrade();
        
        StudentSectionGrade namedStudentSectionGrade = new StudentSectionGrade(emptyStudentSectionGrade);
        namedStudentSectionGrade.setGrade(5.3);
        namedStudentSectionGrade.setComplete(false);
        
        return new Object[][] {
                { "Empty section assignment", emptyStudentSectionGrade, student },
                { "Populated section assignment", namedStudentSectionGrade, student2 },
        };
    }
    
    @Test(dataProvider = "createStudentSectionGradeProvider")
    public void createStudentSectionGradeTest(String msg, StudentSectionGrade studentSectionGrade, Student stud) {
        studentSectionGradeValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section.getId(), stud.getId(), studentSectionGrade, msg);
        numCreated++;
    }

    @Test
    public void deleteStudentSectionGradeTest() {
        StudentSectionGrade emptyStudentSectionGrade = new StudentSectionGrade();
        studentSectionGradeValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section.getId(), student3.getId(), emptyStudentSectionGrade, "delete");
        studentSectionGradeValidatingExecutor.delete(school.getId(), schoolYear.getId(), term.getId(), section.getId(), student3.getId(), "delete");
    }
    
    @Test(dataProvider = "createStudentSectionGradeProvider")
    public void replaceStudentSectionGradeTest(String msg, StudentSectionGrade studentSectionGrade, Student stud) {
        Student student4 = new Student();
        student4.setName(localeServiceUtil.generateName());
        student4 = studentValidatingExecutor.create(student4, "create base student");
        studentSectionGradeValidatingExecutor.create(
                school.getId(), schoolYear.getId(), term.getId(), section.getId(), student4.getId(), studentSectionGrade, msg);
        StudentSectionGrade replaceGrade = new StudentSectionGrade();
        studentSectionGradeValidatingExecutor.replace(
                school.getId(), schoolYear.getId(), term.getId(), section.getId(), student4.getId(), replaceGrade, msg);
        numCreated++;
    }
    
    @Test(dataProvider = "createStudentSectionGradeProvider")
    public void updateStudentSectionGradeTest(String msg, StudentSectionGrade studentSectionGrade, Student stud) {
        Student student5 = new Student();
        student5.setName(localeServiceUtil.generateName());
        student5 = studentValidatingExecutor.create(student5, "create base student");
        studentSectionGradeValidatingExecutor.create(
                school.getId(), schoolYear.getId(), term.getId(), section.getId(), student5.getId(), studentSectionGrade, msg);
        StudentSectionGrade updatedSection = new StudentSectionGrade();
        updatedSection.setComplete(true);
        //PATCH the existing record with a new name.
        studentSectionGradeValidatingExecutor.update(school.getId(), schoolYear.getId(), term.getId(), section.getId(), student5.getId(), updatedSection, msg);
        numCreated++;
    }
    
    @Test
    public void getAllItems() {
        studentSectionGradeValidatingExecutor.getAll(school.getId(), schoolYear.getId(), term.getId(), section.getId(), student.getId(), "Get all records created so far", numCreated);
    }
}
