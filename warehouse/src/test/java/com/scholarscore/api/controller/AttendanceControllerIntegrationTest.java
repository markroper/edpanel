package com.scholarscore.api.controller;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.*;
import com.scholarscore.models.attendance.Attendance;
import com.scholarscore.models.attendance.AttendanceStatus;
import com.scholarscore.models.attendance.AttendanceType;
import com.scholarscore.models.attendance.SchoolDay;
import com.scholarscore.models.user.Student;
import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;


@Test( groups = { "integration" })
public class AttendanceControllerIntegrationTest extends IntegrationBase {
    private School school;
    private SchoolYear schoolYear;
    private Term term;
    private Student student;
    private List<SchoolDay> days;
    private Course course;
    private Section section;
    private int numAttendanceCreated = 0;
    
    @SuppressWarnings("deprecation")
    @BeforeClass
    public void init() {
        authenticate();
        school = new School();
        school.setName(localeServiceUtil.generateName());
        school = schoolValidatingExecutor.create(school, "Create base school");
        
        student = new Student();
        student.setName("mark roper");
        student.setCurrentSchoolId(school.getId());
        student = studentValidatingExecutor.create(student, "creating student");
        
        schoolYear = new SchoolYear();
        schoolYear.setName(localeServiceUtil.generateName());
        schoolYear.setStartDate(LocalDate.of(2015, 9, 1));
        schoolYear.setEndDate(LocalDate.of(2016, 6, 1));
        schoolYear.setSchool(school);
        schoolYear = schoolYearValidatingExecutor.create(school.getId(), schoolYear, "create base schoolYear");
        
        term = new Term();
        term.setSchoolYear(schoolYear);
        term.setStartDate(schoolYear.getStartDate());
        term.setEndDate(schoolYear.getEndDate());
        term = termValidatingExecutor.create(school.getId(), schoolYear.getId(), term, "Creating a term...");

        course = new Course();
        course.setName(localeServiceUtil.generateName());
        course = courseValidatingExecutor.create(school.getId(), course, "create base course");

        section = new Section();
        section.setTerm(term);
        section.setCourse(course);
        section = sectionValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section, "create base section");

        school = schoolValidatingExecutor.get(school.getId(), "caching school");
        
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
    }
    
    @DataProvider
    public Object[][] createAttendanceProvider() {
        List<AttendanceStatus> statusValues =
                Collections.unmodifiableList(Arrays.asList(AttendanceStatus.values()));
        Object[][] cases = new Object[days.size()*2][2];
        for(int i = 0; i < days.size() * 2; i++) {
            if (i < days.size()) {
                cases[i][0] = "case " + i;
                Attendance a = new Attendance();
                a.setDescription("some desc");
                a.setSchoolDay(days.get(i));
                a.setStudent(student);
                a.setType(AttendanceType.DAILY);
                a.setStatus(statusValues.get(new Random().nextInt(statusValues.size())));
                cases[i][1] = a;
            } else {
                cases[i][0] = " section case " + i;
                Attendance a = new Attendance();
                a.setDescription("some section desc");
                a.setSchoolDay(days.get(i - days.size()));
                a.setStudent(student);
                a.setType(AttendanceType.SECTION);
                a.setSection(section);
                a.setStatus(statusValues.get(new Random().nextInt(statusValues.size())));
                cases[i][1] = a;
            }

        }

        return cases;
    }
    
    @Test(dataProvider = "createAttendanceProvider")
    public void createAttendance(String msg, Attendance a) {
        attendanceValidatingExecutor.create(school.getId(), student.getId(), a, msg);
        numAttendanceCreated++;
    }
    
    @Test(dependsOnMethods = { "createAttendance" })
    public void getAllInYear() {
        attendanceValidatingExecutor.getAllInTerm(school.getId(), student.getId(), schoolYear.getId(), term.getId(), numAttendanceCreated, "Get all in year");
    }
    
    @Test(dependsOnMethods = { "createAttendance" })
    public void getAllForStudentAllTime() {
        attendanceValidatingExecutor.getAll(school.getId(), student.getId(), numAttendanceCreated, "Get all in year");
    }

    @Test(dependsOnMethods = { "createAttendance" })
    public void getAllForStudentSection() {
        attendanceValidatingExecutor.getAllInSection(school.getId(), student.getId(), section.getId(), numAttendanceCreated/2, "Get all in section");
    }
    
    @Test(dataProvider = "createAttendanceProvider")
    public void deleteAttendance(String msg, Attendance a) {
        Attendance created = attendanceValidatingExecutor.create(school.getId(), student.getId(), a, msg);
        attendanceValidatingExecutor.delete(school.getId(), student.getId(), created, msg);
    }
    
    @Test(dataProvider = "createAttendanceProvider")
    public void createAttendanceNegative(String msg, Attendance a) {
        attendanceValidatingExecutor.createNegative(school.getId() + 1, student.getId(), a, HttpStatus.NOT_FOUND, msg);
    }
}
