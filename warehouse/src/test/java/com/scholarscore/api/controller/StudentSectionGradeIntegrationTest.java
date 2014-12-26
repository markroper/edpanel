package com.scholarscore.api.controller;

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
        section.setEnrolledStudents(new HashSet<Long>());
        section.getEnrolledStudents().add(student.getId());
        section.getEnrolledStudents().add(student2.getId());
        section.getEnrolledStudents().add(student3.getId());
        section = sectionValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section, "create test base term");
    }
    
    //Positive test cases
    @DataProvider
    public Object[][] createStudentSectionGradeProvider() {
        StudentSectionGrade emptyStudentSectionGrade = new StudentSectionGrade();
        emptyStudentSectionGrade.setStudentId(student.getId());
        emptyStudentSectionGrade.setSectionId(section.getId());
        
        StudentSectionGrade namedStudentSectionGrade = new StudentSectionGrade(emptyStudentSectionGrade);
        namedStudentSectionGrade.setStudentId(student2.getId());
        namedStudentSectionGrade.setGrade(5.3);
        namedStudentSectionGrade.setComplete(false);
        
        return new Object[][] {
                { "Empty section assignment", emptyStudentSectionGrade },
                { "Populated section assignment", namedStudentSectionGrade },
        };
    }
    
    @Test(dataProvider = "createStudentSectionGradeProvider")
    public void createStudentSectionGradeTest(String msg, StudentSectionGrade studentSectionGrade) {
        studentSectionGradeValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section.getId(), studentSectionGrade, msg);
        numberOfItemsCreated++;
    }

    @Test
    public void deleteStudentSectionGradeTest() {
        StudentSectionGrade emptyStudentSectionGrade = new StudentSectionGrade();
        emptyStudentSectionGrade.setStudentId(student3.getId());
        emptyStudentSectionGrade.setSectionId(section.getId());
        StudentSectionGrade createdSection = studentSectionGradeValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section.getId(), emptyStudentSectionGrade, "delete");
        studentSectionGradeValidatingExecutor.delete(school.getId(), schoolYear.getId(), term.getId(), section.getId(), createdSection.getStudentId(), "delete");
    }
    
    @Test(dataProvider = "createStudentSectionGradeProvider")
    public void replaceStudentSectionGradeTest(String msg, StudentSectionGrade studentSectionGrade) {
        StudentSectionGrade createdSection = studentSectionGradeValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section.getId(), studentSectionGrade, msg);
        StudentSectionGrade replaceGrade = new StudentSectionGrade();
        replaceGrade.setStudentId(student.getId());
        replaceGrade.setSectionId(section.getId());
        studentSectionGradeValidatingExecutor.replace(school.getId(), schoolYear.getId(), term.getId(), section.getId(), createdSection.getStudentId(), replaceGrade, msg);
    }
    
    @Test(dataProvider = "createStudentSectionGradeProvider")
    public void updateStudentSectionGradeTest(String msg, StudentSectionGrade studentSectionGrade) {
        StudentSectionGrade createdSection = studentSectionGradeValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section.getId(), studentSectionGrade, msg);
        StudentSectionGrade updatedSection = new StudentSectionGrade();
        updatedSection.setComplete(true);
        //PATCH the existing record with a new name.
        studentSectionGradeValidatingExecutor.update(school.getId(), schoolYear.getId(), term.getId(), section.getId(), createdSection.getStudentId(), updatedSection, msg);
    }
    
    @Test
    public void getAllItems() {
        studentSectionGradeValidatingExecutor.getAll(school.getId(), schoolYear.getId(), term.getId(), section.getId(), "Get all records created so far", numberOfItemsCreated);
    }
}
