package com.scholarscore.models.attendance;

import com.scholarscore.models.AbstractBuilderUnitTest;
import com.scholarscore.models.user.Student;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * AttendanceBuilderUnitTest tests out the Attendance object's Builder to ensure that constructing an object with the builder
 * is equivalent to creating one with setters.
 * Created by cschneider on 10/11/15.
 */
@Test(groups = { "unit" })
public class AttendanceBuilderUnitTest extends AbstractBuilderUnitTest<Attendance>{

    /**
     * Each builderProvider should provide test cases for empty and full objects in the for of
     * {[string msg describing what is happening], <T> [object made with builder], <T>[object made with setter]}
     * @return a two dimensional array of test cases
     */
    @DataProvider
    public Object[][] builderProvider(){
        Attendance emptyAttendance = new Attendance();
        Attendance emptyAttendanceByBuilder = new Attendance.AttendanceBuilder().build();

        Long id = RandomUtils.nextLong(0L, Long.MAX_VALUE);
        SchoolDay schoolDay = new SchoolDay();
        Student student = new Student();
        String description = RandomStringUtils.randomAlphanumeric(20);

        Attendance fullAttendance = new Attendance();
        fullAttendance.setId(id);
        fullAttendance.setStatus(AttendanceStatus.ABSENT);
        fullAttendance.setSchoolDay(schoolDay);
        fullAttendance.setStudent(student);
        fullAttendance.setDescription(description);

        Attendance fullAttendanceByBuilder = new Attendance.AttendanceBuilder().
                withId(id).
                withAttendanceStatus(AttendanceStatus.ABSENT).
                withSchoolDay(schoolDay).
                withStudent(student).
                withDescription(description).
                build();

        return new Object[][]{
                {"Empty attendance object", emptyAttendanceByBuilder, emptyAttendance},
                {"Full attendance object", fullAttendance, fullAttendanceByBuilder}
        };
    }

}
