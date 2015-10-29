package com.scholarscore.api.controller;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.StudentSectionGrade;
import com.scholarscore.models.Term;
import com.scholarscore.models.user.Student;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test(groups = { "integration" })
public class StudentSectionGradeIntegrationTest extends IntegrationBase {
    private School school;
    private SchoolYear schoolYear;
    private Term term;
    private Section section;
    private Student student;
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
        schoolYear.setSchool(school);
        schoolYear = schoolYearValidatingExecutor.create(school.getId(), schoolYear, "create base schoolYear");
        
        term = new Term();
        term.setName(localeServiceUtil.generateName());
        term = termValidatingExecutor.create(school.getId(), schoolYear.getId(), term, "create test base term");
        
        course = new Course();
        course.setName(localeServiceUtil.generateName());
        course = courseValidatingExecutor.create(school.getId(), course, "create base course");
        
        student = generateNewStudent();
        student3 = generateNewStudent();
        
        section = new Section();
        section.setCourse(course);
        section.setName(localeServiceUtil.generateName());
        section = sectionValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section, "create test base term");
    }
    
    private Student generateNewStudent() { 
        Student student = new Student();
        student.setName(localeServiceUtil.generateName());
        return studentValidatingExecutor.create(student, "create base student");
    }
    
    //Positive test cases
    @DataProvider
    public Object[][] createStudentSectionGradeProvider() {

        Student emptyStudent = generateNewStudent();
        // 'empty' in that there is no grade
        StudentSectionGrade emptyStudentSectionGrade = new StudentSectionGrade();
        emptyStudentSectionGrade.setSection(section);
        emptyStudentSectionGrade.setStudent(emptyStudent);
        
        Student namedStudent = generateNewStudent();
        StudentSectionGrade namedStudentSectionGrade = new StudentSectionGrade(emptyStudentSectionGrade);
        namedStudentSectionGrade.setGrade(5.3);
        namedStudentSectionGrade.setStudent(namedStudent);
        namedStudentSectionGrade.setSection(section);
        namedStudentSectionGrade.setComplete(false);
        
        return new Object[][] {
                { "Empty section assignment", emptyStudentSectionGrade, emptyStudent },
                { "Populated section assignment", namedStudentSectionGrade, namedStudent },
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
        emptyStudentSectionGrade.setStudent(student3);
        emptyStudentSectionGrade.setSection(section);
        studentSectionGradeValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section.getId(), student3.getId(), emptyStudentSectionGrade, "delete");
        studentSectionGradeValidatingExecutor.delete(school.getId(), schoolYear.getId(), term.getId(), section.getId(), student3.getId(), "delete");
    }
    
    @Test(dataProvider = "createStudentSectionGradeProvider")
    public void replaceStudentSectionGradeTest(String msg, StudentSectionGrade studentSectionGrade, Student stud) {
        StudentSectionGrade ssg = studentSectionGradeValidatingExecutor.create(
                school.getId(), schoolYear.getId(), term.getId(), section.getId(), stud.getId(), studentSectionGrade, msg);
        StudentSectionGrade replaceGrade = new StudentSectionGrade();
        replaceGrade.setStudent(stud);
        replaceGrade.setSection(section);
        replaceGrade.setId(ssg.getId());
        studentSectionGradeValidatingExecutor.replace(
                school.getId(), schoolYear.getId(), term.getId(), section.getId(), stud.getId(), replaceGrade, msg);
        numCreated++;
    }
    
    @Test(dataProvider = "createStudentSectionGradeProvider")
    public void updateStudentSectionGradeTest(String msg, StudentSectionGrade studentSectionGrade, Student stud) {
        studentSectionGradeValidatingExecutor.create(
                school.getId(), schoolYear.getId(), term.getId(), section.getId(), stud.getId(), studentSectionGrade, msg);
        StudentSectionGrade updatedSection = new StudentSectionGrade();
        updatedSection.setComplete(true);
        //PATCH the existing record with a new name.
        studentSectionGradeValidatingExecutor.update(school.getId(), schoolYear.getId(), term.getId(), section.getId(), stud.getId(), updatedSection, msg);
        numCreated++;
    }
    
    @Test
    public void getAllItems() {
        studentSectionGradeValidatingExecutor.getAll(school.getId(), schoolYear.getId(), term.getId(), section.getId(), student.getId(), "Get all records created so far", numCreated);
    }
}
