package com.scholarscore.client;

import com.scholarscore.models.Behavior;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.StudentSectionGrade;
import com.scholarscore.models.Term;
import com.scholarscore.models.assignment.Assignment;
import com.scholarscore.models.assignment.StudentAssignment;
import com.scholarscore.models.attendance.Attendance;
import com.scholarscore.models.attendance.SchoolDay;
import com.scholarscore.models.user.Administrator;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.Teacher;
import com.scholarscore.models.user.User;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Created by mattg on 7/3/15.
 */
public interface IAPIClient {
    School createSchool(School school) throws HttpClientException;
    School getSchool(Long id) throws HttpClientException;
    School[] getSchools() throws HttpClientException;
    School updateSchool(School school) throws IOException;
    void deleteSchool(School school) throws HttpClientException;

    Student createStudent(Student student) throws HttpClientException;
    Student updateStudent(Long studentId, Student student) throws HttpClientException;
    Collection<Student> getStudents(Long schoolId) throws HttpClientException;
    Student getStudent(Long ssid) throws HttpClientException;
    
    Collection<Teacher> getTeachers() throws HttpClientException;
    Collection<Administrator> getAdministrators() throws HttpClientException;
    
    Collection<Behavior> getBehaviors(Long studentId) throws HttpClientException;
    Behavior createBehavior(Long studentId, Behavior behavior) throws HttpClientException;
    Behavior updateBehavior(Long studentId, Long behaviorId, Behavior behavior) throws HttpClientException;

    //USERS
    Teacher createTeacher(Teacher teacher) throws HttpClientException;
    Administrator createAdministrator(Administrator administrator) throws HttpClientException;
    User createUser(User login) throws HttpClientException;
    User[] getUsers(Long schoolId) throws HttpClientException;
    User updateUser(User user) throws IOException;
    User replaceUser(User user) throws IOException;

    //Courses
    Course createCourse(Long schoolId, Course course) throws HttpClientException;
    void deleteCourse(Long schoolId, Course course) throws HttpClientException;
    Course replaceCourse(Long schoolId, Course course) throws IOException;
    Course[] getCourses(Long schoolId) throws HttpClientException;

    //SCHOOL YEARS
    SchoolYear createSchoolYear(Long schoolId, SchoolYear year) throws HttpClientException;
    void deleteSchoolYear(Long schoolId, SchoolYear year) throws HttpClientException;
    SchoolYear updateSchoolYear(Long schoolId, SchoolYear year);
    SchoolYear[] getSchoolYears(Long schoolId) throws HttpClientException;

    //TERMS
    Term createTerm(Long schoolId, Long schoolYearId, Term year) throws HttpClientException;
    void deleteTerm(Long schoolId, Long schoolYearId, Term term) throws HttpClientException;
    Term updateTerm(Long schoolId, Long schoolYearId, Term term) throws IOException;
    Term[] getTerms(Long schoolId, Long schoolYearId) throws HttpClientException;

    //School days
    SchoolDay createSchoolDays(Long schoolId, SchoolDay day) throws HttpClientException;
    void createSchoolDays(Long schoolId, List<SchoolDay> days) throws HttpClientException;
    void deleteSchoolDays(Long schoolId, SchoolDay day) throws HttpClientException;
    SchoolDay updateSchoolDays(Long schoolId, SchoolDay day) throws IOException;
    SchoolDay[] getSchoolDays(Long schoolId) throws HttpClientException;

    //Attendance
    Attendance createAttendance(Long schoolId, Long studentId, Attendance attend) throws HttpClientException;
    void createAttendance(Long schoolId, Long studentId, List<Attendance> attends) throws HttpClientException;
    void deleteAttendance(Long schoolId, Long studentId, Attendance attend) throws HttpClientException;
    Attendance updateAttendance(Long schoolId, Long studentId, Attendance attend) throws IOException;
    Attendance[] getAttendance(Long schoolId, Long studentId) throws HttpClientException;


    //SECTIONS
    Section createSection(Long schoolId, Long schoolYearId, Long termId, Section section) throws HttpClientException;
    Section[] getSections(Long schoolId) throws HttpClientException;
    Section replaceSection(Long schoolId, Long schoolYearId, Long termId, Section section) throws IOException;
    void deleteSection(Long schoolId, Long schoolYearId, Long termId, Section section) throws HttpClientException;

    //Student section grades
    StudentSectionGrade createStudentSectionGrade(
            Long schoolId, 
            Long yearId,
            Long termId,
            Long sectionId,
            Long studentId,
            StudentSectionGrade ssg) throws HttpClientException;
    void createStudentSectionGrades(
            Long schoolId,
            Long yearId,
            Long termId,
            Long sectionId,
            List<StudentSectionGrade> ssgs) throws HttpClientException;
    StudentSectionGrade replaceStudentSectionGrade(
            Long schoolId,
            Long yearId,
            Long termId,
            Long sectionId,
            Long studentId,
            StudentSectionGrade ssg) throws IOException;
    void deleteStudentSectionGrade(
            Long schoolId,
            Long yearId,
            Long termId,
            Long sectionId,
            Long studentId,
            StudentSectionGrade ssg) throws HttpClientException;
    StudentSectionGrade[] getStudentSectionGrades(
            Long schoolId,
            Long yearId,
            Long termId,
            Long sectionId) throws HttpClientException;

    //ASSIGNMENTS
    Assignment createSectionAssignment(
            Long schoolId,
            Long yearId,
            Long termId,
            Long sectionId,
            Assignment ssg) throws HttpClientException;
    Assignment replaceSectionAssignment(
            Long schoolId,
            Long yearId,
            Long termId,
            Long sectionId,
            Assignment ssg) throws IOException;
    void deleteSectionAssignment(
            Long schoolId,
            Long yearId,
            Long termId,
            Long sectionId,
            Assignment ssg) throws HttpClientException;
    Assignment[] getSectionAssignments(
            Long schoolId,
            Long yearId,
            Long termId,
            Long sectionId) throws HttpClientException;

    StudentAssignment createStudentAssignment(
            Long schoolId,
            Long yearId,
            Long termId,
            Long sectionId,
            Long assignmentId,
            StudentAssignment studentAssignment
    ) throws HttpClientException;

    //STUDENT ASSIGNMENTS
    void createStudentAssignments(
            Long schoolId,
            Long yearId,
            Long termId,
            Long sectionId,
            Long assignmentId,
            List<StudentAssignment> studentAssignment
    ) throws HttpClientException;
    StudentAssignment[] getStudentAssignments(
            Long schoolId,
            Long yearId,
            Long termId,
            Long sectionId,
            Long assignmentId
    ) throws HttpClientException;
    void deleteStudentAssignment(
            Long schoolId,
            Long yearId,
            Long termId,
            Long sectionId,
            Long assignmentId,
            StudentAssignment studentAssignment
    ) throws HttpClientException;
    void replaceStudentAssignment(
            Long schoolId,
            Long yearId,
            Long termId,
            Long sectionId,
            Long assignmentId,
            StudentAssignment studentAssignment
    ) throws IOException;

}
