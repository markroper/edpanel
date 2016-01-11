package com.scholarscore.api.controller;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.Course;
import com.scholarscore.models.Gender;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.Term;
import com.scholarscore.models.notification.Notification;
import com.scholarscore.models.notification.NotificationMeasure;
import com.scholarscore.models.notification.group.SectionStudents;
import com.scholarscore.models.notification.group.SingleStudent;
import com.scholarscore.models.notification.group.SingleTeacher;
import com.scholarscore.models.query.AggregateFunction;
import com.scholarscore.models.user.Administrator;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.Teacher;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by markroper on 1/11/16.
 */
public class NotificationControllerIntegrationTest extends IntegrationBase {
    private School school;
    private SchoolYear schoolYear;
    private Term term;
    private Course course;
    private Section section;
    private Student student1;
    private Student student2;
    private Student student3;
    private Student student4;
    private Teacher teacher;
    private Administrator administrator;

    @BeforeClass
    public void init() {
        authenticate();
        school = new School();
        school.setName(localeServiceUtil.generateName());
        school = schoolValidatingExecutor.create(school, "Create base school");

        teacher = new Teacher();
        teacher.setName("Mr. Jones");
        teacher.setCurrentSchoolId(school.getId());
        teacher = teacherValidatingExecutor.create(teacher, "Create a base teacher");

        administrator = new Administrator();
        administrator.setName("Ms. Admin");
        administrator.setCurrentSchoolId(school.getId());
        administrator = userValidatingExecutor.createAdmin(administrator, "Create a base teacher");

        student1 = new Student();
        student1.setName(localeServiceUtil.generateName());
        student1.setCurrentSchoolId(school.getId());
        student1 = studentValidatingExecutor.create(student1, "create base student");
        student1.setFederalEthnicity("true");
        student1.setGender(Gender.MALE);
        student1.setFederalRace("W");

        student2 = new Student();
        student2.setName(localeServiceUtil.generateName());
        student2.setCurrentSchoolId(school.getId());
        student2 = studentValidatingExecutor.create(student2, "create base student");
        student2.setFederalEthnicity("false");
        student2.setGender(Gender.FEMALE);
        student2.setFederalRace("A");

        student3 = new Student();
        student3.setName(localeServiceUtil.generateName());
        student3.setCurrentSchoolId(school.getId());
        student3 = studentValidatingExecutor.create(student3, "create base student");
        student3.setFederalEthnicity("true");
        student3.setGender(Gender.MALE);
        student3.setFederalRace("B");

        student4 = new Student();
        student4.setName(localeServiceUtil.generateName());
        student4.setCurrentSchoolId(school.getId());
        student4 = studentValidatingExecutor.create(student4, "create base student");
        student4.setFederalEthnicity("false");
        student4.setGender(Gender.FEMALE);
        student4.setFederalRace("I");

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
        section.getEnrolledStudents().add(student1);
        section.getEnrolledStudents().add(student2);
        section.setTeachers(new HashSet<Teacher>());
        section.getTeachers().add(teacher);
        section.setTerm(term);
        section = sectionValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section, "create test base term");
    }

    @DataProvider
    public Object[][] createNotificationsProvider() {
        Notification teacherStudentGpa = new Notification();
        teacherStudentGpa.setAggregateFunction(AggregateFunction.AVG);
        teacherStudentGpa.setCreatedDate(LocalDate.now());
        teacherStudentGpa.setExpiryDate(LocalDate.now().plusMonths(3));
        teacherStudentGpa.setMeasure(NotificationMeasure.GPA);
        teacherStudentGpa.setName("Average student GPA in section");
        teacherStudentGpa.setOwner(teacher);
        teacherStudentGpa.setSchoolId(school.getId());
        teacherStudentGpa.setTriggerValue(3.0);
        //Subject group
        SectionStudents sectionGroup = new SectionStudents();
        sectionGroup.setSectionId(section.getId());
        teacherStudentGpa.setSubjects(sectionGroup);
        //subscribers group
        SingleTeacher singleTeacher = new SingleTeacher();
        singleTeacher.setTeacherId(teacher.getId());
        teacherStudentGpa.setSubscribers(singleTeacher);


        Notification studentSectionGrade = new Notification();
        studentSectionGrade.setCreatedDate(LocalDate.now());
        studentSectionGrade.setExpiryDate(LocalDate.now().plusMonths(3));
        studentSectionGrade.setMeasure(NotificationMeasure.SECTION_GRADE);
        studentSectionGrade.setName("Single Student grade goal");
        studentSectionGrade.setOwner(student2);
        studentSectionGrade.setSchoolId(school.getId());
        studentSectionGrade.setSectionId(section.getId());
        studentSectionGrade.setTriggerValue(0.85);
        //subscribers & subjects group are the same in this case
        SingleStudent singleStudent = new SingleStudent();
        singleStudent.setStudentId(student2.getId());
        studentSectionGrade.setSubscribers(singleStudent);
        studentSectionGrade.setSubjects(singleStudent);

        return new Object[][] {
                { "Notify on the GPA of students within a section", teacherStudentGpa },
                { "Notify on a single section grade for a single student", studentSectionGrade },
//                { "Fully populated behavior", adminNotificationBehaviorScore },
//                { "Fully populated behavior", schoolNotificationSchoolAbsenses },
//                { "Fully populated behavior", studentFilterNotificationHwCompletion },
        };
    }

}
