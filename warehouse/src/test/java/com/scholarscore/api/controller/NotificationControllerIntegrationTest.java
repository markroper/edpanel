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
import com.scholarscore.models.notification.group.FilteredStudents;
import com.scholarscore.models.notification.group.SchoolAdministrators;
import com.scholarscore.models.notification.group.SectionStudents;
import com.scholarscore.models.notification.group.SingleStudent;
import com.scholarscore.models.notification.group.SingleTeacher;
import com.scholarscore.models.notification.window.Duration;
import com.scholarscore.models.notification.window.NotificationWindow;
import com.scholarscore.models.query.AggregateFunction;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.Teacher;
import org.apache.commons.lang.RandomStringUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by markroper on 1/11/16.
 */
@Test(groups = {"integration"})
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

        student1 = new Student();
        student1.setName(localeServiceUtil.generateName());
        student1.setCurrentSchoolId(school.getId());
        student1.setFederalEthnicity("true");
        student1.setGender(Gender.MALE);
        student1.setFederalRace("W");
        student1 = studentValidatingExecutor.create(student1, "create base student");

        student2 = new Student();
        student2.setName(localeServiceUtil.generateName());
        student2.setCurrentSchoolId(school.getId());
        student2.setFederalEthnicity("false");
        student2.setGender(Gender.FEMALE);
        student2.setFederalRace("A");
        student2 = studentValidatingExecutor.create(student2, "create base student");

        student3 = new Student();
        student3.setName(localeServiceUtil.generateName());
        student3.setCurrentSchoolId(school.getId());
        student3.setFederalEthnicity("true");
        student3.setGender(Gender.MALE);
        student3.setFederalRace("B");
        student3 = studentValidatingExecutor.create(student3, "create base student");

        student4 = new Student();
        student4.setName(localeServiceUtil.generateName());
        student4.setCurrentSchoolId(school.getId());
        student4.setFederalEthnicity("false");
        student4.setGender(Gender.FEMALE);
        student4.setFederalRace("I");
        student4 = studentValidatingExecutor.create(student4, "create base student");

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
        SingleStudent singleStudentSubject = new SingleStudent();
        singleStudentSubject.setStudentId(student2.getId());
        studentSectionGrade.setSubjects(singleStudentSubject);

        Notification behaviorScoreNotification = new Notification();
        behaviorScoreNotification.setCreatedDate(LocalDate.now());
        behaviorScoreNotification.setExpiryDate(LocalDate.now().plusMonths(3));
        behaviorScoreNotification.setMeasure(NotificationMeasure.BEHAVIOR_SCORE);
        behaviorScoreNotification.setName("School wide boys behavior score grade goal");
        behaviorScoreNotification.setOwner(teacher);
        behaviorScoreNotification.setSchoolId(school.getId());
        behaviorScoreNotification.setAggregateFunction(AggregateFunction.AVG);
        behaviorScoreNotification.setTriggerValue(80D);
        //subscribers & subjects group are the same in this case
        SchoolAdministrators schoolAdmins = new SchoolAdministrators();
        behaviorScoreNotification.setSubscribers(schoolAdmins);
        FilteredStudents filteredStudents = new FilteredStudents();
        filteredStudents.setGender(Gender.MALE);
        behaviorScoreNotification.setSubjects(filteredStudents);

        Notification hwCompletion = new Notification();
        hwCompletion.setCreatedDate(LocalDate.now());
        hwCompletion.setExpiryDate(LocalDate.now().plusMonths(3));
        hwCompletion.setMeasure(NotificationMeasure.HOMEWORK_COMPLETION);
        hwCompletion.setName("Section homework completion rate change of 5% in a week");
        hwCompletion.setOwner(teacher);
        hwCompletion.setSchoolId(school.getId());
        hwCompletion.setSectionId(section.getId());
        hwCompletion.setTriggerValue(0.05);
        NotificationWindow w = new NotificationWindow();
        w.setTriggerIsPercent(true);
        w.setWindow(Duration.WEEK);
        hwCompletion.setWindow(w);
        //subscribers & subjects group are the same in this case
        SectionStudents sectionStudents = new SectionStudents();
        sectionStudents.setSectionId(section.getId());
        hwCompletion.setSubjects(sectionStudents);
        SingleTeacher teach = new SingleTeacher();
        teach.setTeacherId(teacher.getId());
        hwCompletion.setSubscribers(teach);

        return new Object[][] {
                { "Notify on the GPA of students within a section", teacherStudentGpa },
                { "Notify on a single section grade for a single student", studentSectionGrade },
                { "Notify on boys behavior score", behaviorScoreNotification },
                { "Notify on section homework completion rate change of 5% in a week", hwCompletion }
        };
    }

    @Test(dataProvider = "createNotificationsProvider")
    public void createNotifications(String msg, Notification notification) {
        Notification n = notificationValidatingExecutor.create(notification, msg);
        notificationValidatingExecutor.delete(n.getId(), msg);
    }

    @Test(dataProvider = "createNotificationsProvider")
    public void createAndThenUpdateNotifications(String msg, Notification notification) {
        Notification n = notificationValidatingExecutor.create(notification, msg);
        n.setName(RandomStringUtils.randomAlphabetic(10));
        notificationValidatingExecutor.update(n, msg);
        notificationValidatingExecutor.delete(n.getId(), msg);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void createTestGetByOwner() {
        Object[][] inputs = createNotificationsProvider();
        for(int i = 0; i < inputs.length; i++) {
            notificationValidatingExecutor.create((Notification)inputs[i][1], (String)inputs[i][0]);
        }
        List<Notification> notifications = notificationValidatingExecutor.getByUserId(
                teacher.getId(), "Get all notifications owned by a teacher");
        Assert.assertEquals(notifications.size(), 3, "Unexpected number of notifications for a user");
        List<Notification> allNotifications = notificationValidatingExecutor.getAll("all notifications");
        Assert.assertEquals(allNotifications.size(), 4, "Unexpected number of notifications returned by getAll()");
    }
}
