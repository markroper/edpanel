package com.scholarscore.api.controller;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.Course;
import com.scholarscore.models.Gender;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.Term;
import com.scholarscore.models.attendance.Attendance;
import com.scholarscore.models.attendance.AttendanceStatus;
import com.scholarscore.models.attendance.AttendanceTypes;
import com.scholarscore.models.attendance.SchoolDay;
import com.scholarscore.models.gpa.AddedValueGpa;
import com.scholarscore.models.grade.SectionGrade;
import com.scholarscore.models.grade.StudentSectionGrade;
import com.scholarscore.models.notification.Notification;
import com.scholarscore.models.notification.NotificationMeasure;
import com.scholarscore.models.notification.TriggeredNotification;
import com.scholarscore.models.notification.group.FilteredStudents;
import com.scholarscore.models.notification.group.SchoolAdministrators;
import com.scholarscore.models.notification.group.SectionStudents;
import com.scholarscore.models.notification.group.SingleStudent;
import com.scholarscore.models.notification.group.SingleTeacher;
import com.scholarscore.models.notification.window.Duration;
import com.scholarscore.models.notification.window.NotificationWindow;
import com.scholarscore.models.query.AggregateFunction;
import com.scholarscore.models.user.Staff;
import com.scholarscore.models.user.Student;
import org.apache.commons.lang3.RandomStringUtils;
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
    private Staff teacher;
    private List<SchoolDay> days;

    private static final double STUDENT_3_ABSENCE_THRESHOLD = 4;
    
    @BeforeClass
    public void init() {
        authenticate();
        school = new School();
        school.setName(localeServiceUtil.generateName());
        school = schoolValidatingExecutor.create(school, "Create base school");

        teacher = new Staff();
        teacher.setName(localeServiceUtil.generateName());
        teacher.setIsTeacher(true);
        teacher.setCurrentSchoolId(school.getId());
        teacher = teacherValidatingExecutor.create(teacher, "Create a base teacher");

        student1 = new Student();
        student1.setName(localeServiceUtil.generateName());
        student1.setCurrentSchoolId(school.getId());
        student1.setFederalEthnicity("true");
        student1.setGender(Gender.MALE);
        student1.setFederalRace("W");
        student1 = studentValidatingExecutor.create(student1, "create base student");
        AddedValueGpa gpa1 = new AddedValueGpa();
        gpa1.setCalculationDate(LocalDate.now());
        gpa1.setStudentId(student1.getId());
        gpa1.setScore(3.3);
        gpaValidatingExecutor.create(student1.getUserId(), gpa1, "GPA for student1");

        student2 = new Student();
        student2.setName(localeServiceUtil.generateName());
        student2.setCurrentSchoolId(school.getId());
        student2.setFederalEthnicity("false");
        student2.setGender(Gender.FEMALE);
        student2.setFederalRace("A");
        student2 = studentValidatingExecutor.create(student2, "create base student");
        AddedValueGpa gpa2 = new AddedValueGpa();
        gpa2.setCalculationDate(LocalDate.now());
        gpa2.setStudentId(student2.getId());
        gpa2.setScore(2.8);
        gpaValidatingExecutor.create(student2.getUserId(), gpa2, "GPA for student1");

        student3 = new Student();
        student3.setName(localeServiceUtil.generateName());
        student3.setCurrentSchoolId(school.getId());
        student3.setFederalEthnicity("true");
        student3.setGender(Gender.MALE);
        student3.setFederalRace("B");
        student3 = studentValidatingExecutor.create(student3, "create base student");
        AddedValueGpa gpa3 = new AddedValueGpa();
        gpa3.setCalculationDate(LocalDate.now());
        gpa3.setStudentId(student3.getId());
        gpa3.setScore(2.9);
        gpaValidatingExecutor.create(student3.getUserId(), gpa3, "GPA for student1");

        student4 = new Student();
        student4.setName(localeServiceUtil.generateName());
        student4.setCurrentSchoolId(school.getId());
        student4.setFederalEthnicity("false");
        student4.setGender(Gender.FEMALE);
        student4.setFederalRace("I");
        student4 = studentValidatingExecutor.create(student4, "create base student");
        AddedValueGpa gpa4 = new AddedValueGpa();
        gpa4.setCalculationDate(LocalDate.now());
        gpa4.setStudentId(student4.getId());
        gpa4.setScore(3.8);
        gpaValidatingExecutor.create(student4.getUserId(), gpa4, "GPA for student1");

        schoolYear = new SchoolYear();
        schoolYear.setName(localeServiceUtil.generateName());
        schoolYear.setSchool(school);
        schoolYear.setStartDate(LocalDate.of(2015, 8, 25));
        schoolYear.setEndDate(LocalDate.of(2016, 6, 20));
        schoolYear = schoolYearValidatingExecutor.create(school.getId(), schoolYear, "create base schoolYear");

        term = new Term();
        term.setName(localeServiceUtil.generateName());
        term.setStartDate(LocalDate.of(2015, 8, 25));
        term.setEndDate(LocalDate.of(2016, 6, 20));
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
        section.setTeachers(new HashSet<Staff>());
        section.getTeachers().add(teacher);
        section.setTerm(term);
        section = sectionValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section, "create test base term");

        SectionGrade sg = new SectionGrade();
        sg.setDate(LocalDate.now());
        sg.setScore(0.85);
        sg.setSectionFk(section.getId());
        sg.setStudentFk(student1.getId());
        StudentSectionGrade g1 = new StudentSectionGrade();
        g1.setOverallGrade(sg);
        g1.setStudent(student1);
        g1.setComplete(true);
        g1.setSection(section);

        SectionGrade sg2 = new SectionGrade();
        sg2.setDate(LocalDate.now());
        sg2.setScore(0.75);
        sg2.setSectionFk(section.getId());
        sg2.setStudentFk(student2.getId());
        StudentSectionGrade g2 = new StudentSectionGrade();
        g2.setOverallGrade(sg2);
        g2.setStudent(student2);
        g2.setComplete(true);
        g2.setSection(section);

        studentSectionGradeValidatingExecutor.update(school.getId(), schoolYear.getId(), term.getId(), section.getId(), student1.getId(), g1, "Student 1 grade");
        studentSectionGradeValidatingExecutor.update(school.getId(), schoolYear.getId(), term.getId(), section.getId(), student2.getId(), g2, "Student 1 grade");

        //Make some school days
        days = new ArrayList<>();
        for(int i = 0; i < AttendanceStatus.values().length; i++) {
            LocalDate date = term.getStartDate().plusDays(i);
            SchoolDay day = new SchoolDay();
            day.setSchool(school);
            day.setDate(date);
            day.setSchool(school);
            days.add(schoolDayValidatingExecutor.create(school.getId(), day, "creating a school"));
        }
        //Make some absences
        section.setEnrolledStudents(new ArrayList<>());
        for(SchoolDay day: days) {
            Attendance a = new Attendance();
            a.setSection(section);
            a.setType(AttendanceTypes.SECTION);
            a.setStatus(AttendanceStatus.TARDY);
            a.setStudent(student1);
            a.setSchoolDay(day);
            attendanceValidatingExecutor.create(school.getId(), student1.getId(), a, "Section absence for studenta");

            Attendance a2 = new Attendance();
            a2.setType(AttendanceTypes.DAILY);
            a2.setStatus(AttendanceStatus.ABSENT);
            a2.setStudent(student3);
            a2.setSchoolDay(day);
            attendanceValidatingExecutor.create(school.getId(), student3.getId(), a2, "Daily absence for student3");
        }
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
        teacherStudentGpa.setTriggerValue(3.2);
        //Subject group
        SectionStudents sectionGroup = new SectionStudents();
        sectionGroup.setSection(section);
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
        studentSectionGrade.setSection(section);
        studentSectionGrade.setTriggerValue(0.85);
        //subscribers & subjects group are the same in this case
        SingleStudent singleStudent = new SingleStudent();
        singleStudent.setStudent(student2);
        studentSectionGrade.setSubscribers(singleStudent);
        SingleStudent singleStudentSubject = new SingleStudent();
        singleStudentSubject.setStudent(student2);
        studentSectionGrade.setSubjects(singleStudentSubject);

        Notification behaviorScoreNotification = new Notification();
        behaviorScoreNotification.setCreatedDate(LocalDate.now());
        behaviorScoreNotification.setExpiryDate(LocalDate.now().plusMonths(3));
        behaviorScoreNotification.setMeasure(NotificationMeasure.BEHAVIOR_SCORE);
        behaviorScoreNotification.setName("School wide boys behavior score grade goal");
        behaviorScoreNotification.setOwner(teacher);
        behaviorScoreNotification.setSchoolId(school.getId());
        behaviorScoreNotification.setAggregateFunction(AggregateFunction.AVG);
        behaviorScoreNotification.setTriggerWhenGreaterThan(true);
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
        hwCompletion.setSection(section);
        hwCompletion.setTriggerValue(0.05);
        NotificationWindow w = new NotificationWindow();
        w.setTriggerIsPercent(true);
        w.setWindow(Duration.WEEK);
        hwCompletion.setWindow(w);
        //subscribers & subjects group are the same in this case
        SectionStudents sectionStudents = new SectionStudents();
        sectionStudents.setSection(section);
        hwCompletion.setSubjects(sectionStudents);
        SingleTeacher teach = new SingleTeacher();
        teach.setTeacherId(teacher.getId());
        hwCompletion.setSubscribers(teach);

        Notification sectionTardy = new Notification();
        sectionTardy.setCreatedDate(LocalDate.now());
        sectionTardy.setTriggerWhenGreaterThan(true);
        sectionTardy.setExpiryDate(LocalDate.now().plusMonths(3));
        sectionTardy.setMeasure(NotificationMeasure.SECTION_TARDY);
        sectionTardy.setName("Section tardy");
        sectionTardy.setOwner(teacher);
        sectionTardy.setSchoolId(school.getId());
        sectionTardy.setSection(section);
        sectionTardy.setTriggerValue(5D);
        NotificationWindow win = new NotificationWindow();
        win.setTriggerIsPercent(false);
        win.setWindow(Duration.YEAR);
        sectionTardy.setWindow(win);
        //subscribers & subjects group are the same in this case
        SectionStudents sStudents = new SectionStudents();
        sStudents.setSection(section);
        sectionTardy.setSubjects(sStudents);
        SingleTeacher teach1 = new SingleTeacher();
        teach1.setTeacherId(teacher.getId());
        sectionTardy.setSubscribers(teach1);

        Notification dailyAbsence = new Notification();
        dailyAbsence.setCreatedDate(LocalDate.now());
        dailyAbsence.setTriggerWhenGreaterThan(true);
        dailyAbsence.setExpiryDate(LocalDate.now().plusMonths(3));
        dailyAbsence.setMeasure(NotificationMeasure.SCHOOL_ABSENCE);
        dailyAbsence.setName("Daily Absence");
        dailyAbsence.setOwner(student3);
        dailyAbsence.setSchoolId(school.getId());
        dailyAbsence.setTriggerValue(STUDENT_3_ABSENCE_THRESHOLD);
        //subscribers & subjects group are the same in this case
        SingleStudent stud3 = new SingleStudent();
        stud3.setStudent(student3);
        dailyAbsence.setSubjects(stud3);
        SingleStudent s3 = new SingleStudent();
        s3.setStudent(student3);
        dailyAbsence.setSubscribers(s3);

        return new Object[][] {
                { "Notify on the GPA of students within a section", teacherStudentGpa },
                { "Notify on a single section grade for a single student", studentSectionGrade },
                { "Notify on boys behavior score", behaviorScoreNotification },
                { "Notify on section homework completion rate change of 5% in a week", hwCompletion },
                { "Notify 5 tardies for a single section within a year", sectionTardy },
                { "Notify 4 school absences for a single student within a year", dailyAbsence }
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

    @Test
    @SuppressWarnings("unchecked")
    public void createAndEvaluateAll() {
        Object[][] inputs = createNotificationsProvider();
        for(int i = 0; i < inputs.length; i++) {
            notificationValidatingExecutor.create((Notification)inputs[i][1], (String)inputs[i][0]);
        }
        List<Notification> notifications = notificationValidatingExecutor.getByUserId(
                teacher.getId(), "Get all notifications owned by a teacher");
        Assert.assertEquals(notifications.size(), 4, "Unexpected number of notifications for a user");
        List<Notification> allNotifications = notificationValidatingExecutor.getAll("all notifications");
        Assert.assertTrue(allNotifications.size() >= 6, "Unexpected number of notifications returned by getAll()");

        // evaluate all notifications
        notificationValidatingExecutor.evaluateNotifications(school.getId());
        
        // check that teacher has 2 triggered notifications
        List<TriggeredNotification> teacherTriggeredNotifications =
                notificationValidatingExecutor.getTriggeredNotificationsForUser(teacher.getId(), "Teacher triggered notifications");
        Assert.assertEquals(teacherTriggeredNotifications.size(), 2, "Unexpected number of teacher triggered notifications returned");

        // check that student 2 has 1 triggered notification
        List<TriggeredNotification> student2TriggeredNotifications =
                notificationValidatingExecutor.getTriggeredNotificationsForUser(student2.getId(), "Student 2 triggered notifications");
        Assert.assertEquals(student2TriggeredNotifications.size(), 1, "Unexpected number of student2 triggered notifications returned");
        
        // get student 2's one triggered notification and disable (acknowledge) it
        TriggeredNotification student2TriggeredNotification = student2TriggeredNotifications.get(0);
        notificationValidatingExecutor.disableTriggeredNotification(
                student2TriggeredNotification.getNotification().getId(), 
                student2TriggeredNotification.getId(),
                student2.getId(),
                "Disabling notification");
        
        // confirm that the acknowledgement of the one triggered notification worked, and that now there are zero
        student2TriggeredNotifications =
                notificationValidatingExecutor.getTriggeredNotificationsForUser(student2.getId(), "Student 2 triggered notifications");
        Assert.assertEquals(student2TriggeredNotifications.size(), 0, "Unexpected number of Student 2 triggered notifications after acknowledgement");
        
        // check that student 3 has 0 triggered notifications 
        List<TriggeredNotification> student3TriggeredNotifications =
                notificationValidatingExecutor.getTriggeredNotificationsForUser(student3.getId(), "Student 3 triggered notifications");
        
        // if there are the same number or more absences than the trigger threshold, expect the alert to be triggered
        int triggeredNotifications = (AttendanceStatus.values().length >= STUDENT_3_ABSENCE_THRESHOLD) ? 1 : 0;
        Assert.assertEquals(student3TriggeredNotifications.size(), triggeredNotifications, "Unexpected number of Student 3 triggered notifications returned");
    }
}
