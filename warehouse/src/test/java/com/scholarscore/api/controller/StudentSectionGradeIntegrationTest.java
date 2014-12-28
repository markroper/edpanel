package com.scholarscore.api.controller;

import java.util.HashMap;
import java.util.HashSet;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.Student;
import com.scholarscore.models.StudentSectionGrade;
import com.scholarscore.models.Term;

@Test(groups = { "integration" })
public class StudentSectionGradeIntegrationTest extends IntegrationBase {
    private int numberOfItemsCreated;
    private School school;
    private SchoolYear schoolYear;
    private Term term;
    private Section section;
    private Student student;
    private Student student2;
    private Student student3;
    
    @BeforeClass
    public void init() {
        numberOfItemsCreated = 0;
        school = new School();
        school.setName(localeServiceUtil.generateName());
        school = schoolValidatingExecutor.create(school, "Create base school");
        
        schoolYear = new SchoolYear();
        schoolYear.setName(localeServiceUtil.generateName());
        schoolYear = schoolYearValidatingExecutor.create(school.getId(), schoolYear, "create base schoolYear");
        
        term = new Term();
        term.setName(localeServiceUtil.generateName());
        term = termValidatingExecutor.create(school.getId(), schoolYear.getId(), term, "create test base term");
        
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
        section.setName(localeServiceUtil.generateName());
        section.setEnrolledStudents(new HashMap<Long, Student>());
        section.getEnrolledStudents().put(student.getId(), student);
        section.getEnrolledStudents().put(student2.getId(), student2);
        section.getEnrolledStudents().put(student3.getId(), student3);
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
        numberOfItemsCreated++;
    }

    @Test
    public void deleteStudentSectionGradeTest() {
        StudentSectionGrade emptyStudentSectionGrade = new StudentSectionGrade();
        StudentSectionGrade createdSection = studentSectionGradeValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section.getId(), student3.getId(), emptyStudentSectionGrade, "delete");
        studentSectionGradeValidatingExecutor.delete(school.getId(), schoolYear.getId(), term.getId(), section.getId(), student3.getId(), createdSection.getId(), "delete");
    }
    
    @Test(dataProvider = "createStudentSectionGradeProvider")
    public void replaceStudentSectionGradeTest(String msg, StudentSectionGrade studentSectionGrade) {
        StudentSectionGrade createdSection = studentSectionGradeValidatingExecutor.create(
                school.getId(), schoolYear.getId(), term.getId(), section.getId(), student.getId(), studentSectionGrade, msg);
        StudentSectionGrade replaceGrade = new StudentSectionGrade();
        studentSectionGradeValidatingExecutor.replace(
                school.getId(), schoolYear.getId(), term.getId(), section.getId(), student.getId(), createdSection.getId(), replaceGrade, msg);
    }
    
    @Test(dataProvider = "createStudentSectionGradeProvider")
    public void updateStudentSectionGradeTest(String msg, StudentSectionGrade studentSectionGrade, Student stud) {
        StudentSectionGrade createdSection = studentSectionGradeValidatingExecutor.create(
                school.getId(), schoolYear.getId(), term.getId(), section.getId(), stud.getId(), studentSectionGrade, msg);
        StudentSectionGrade updatedSection = new StudentSectionGrade();
        updatedSection.setComplete(true);
        //PATCH the existing record with a new name.
        studentSectionGradeValidatingExecutor.update(school.getId(), schoolYear.getId(), term.getId(), section.getId(), stud.getId(), createdSection.getId(), updatedSection, msg);
    }
    
    @Test
    public void getAllItems() {
        studentSectionGradeValidatingExecutor.getAll(school.getId(), schoolYear.getId(), term.getId(), section.getId(), student.getId(), "Get all records created so far", 1);
    }
}
