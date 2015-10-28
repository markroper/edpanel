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
import com.scholarscore.models.user.Administrator;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.Teacher;
import com.scholarscore.models.user.User;

import java.util.Collection;
import java.util.List;

/**
 * Created by mattg on 7/3/15.
 */
public interface IAPIClient {
    School createSchool(School school);
    School getSchool(Long id);
    School[] getSchools();
    School updateSchool(School school);
    void deleteSchool(School school);

    Student createStudent(Student student);
    Student updateStudent(Long studentId, Student student);
    Collection<Student> getStudents(Long schoolId);
    
    Collection<Teacher> getTeachers();
    Collection<Administrator> getAdministrators();
    
    Collection<Behavior> getBehaviors(Long studentId);
    Behavior createBehavior(Long studentId, Behavior behavior);
    Behavior updateBehavior(Long studentId, Long behaviorId, Behavior behavior);

    //USERS
    Teacher createTeacher(Teacher teacher);
    Administrator createAdministrator(Administrator administrator);
    User createUser(User login);
    User[] getUsers(Long schoolId);
    User updateUser(User user);
    User replaceUser(User user);

    //Courses
    Course createCourse(Long schoolId, Course course);
    void deleteCourse(Long schoolId, Course course);
    Course replaceCourse(Long schoolId, Course course);
    Course[] getCourses(Long schoolId);

    //SCHOOL YEARS
    SchoolYear createSchoolYear(Long schoolId, SchoolYear year);
    void deleteSchoolYear(Long schoolId, SchoolYear year);
    SchoolYear updateSchoolYear(Long schoolId, SchoolYear year);
    SchoolYear[] getSchoolYears(Long schoolId);

    //TERMS
    Term createTerm(Long schoolId, Long schoolYearId, Term year);
    void deleteTerm(Long schoolId, Long schoolYearId, Term term);
    Term updateTerm(Long schoolId, Long schoolYearId, Term term);
    Term[] getTerms(Long schoolId, Long schoolYearId);

    //SECTIONS
    Section createSection(Long schoolId, Long schoolYearId, Long termId, Section section);
    Section[] getSections(Long schoolId);
    Section replaceSection(Long schoolId, Long schoolYearId, Long termId, Section section);
    void deleteSection(Long schoolId, Long schoolYearId, Long termId, Section section);

    //Student section grades
    StudentSectionGrade createStudentSectionGrade(
            Long schoolId, 
            Long yearId,
            Long termId,
            Long sectionId,
            Long studentId,
            StudentSectionGrade ssg);
    void createStudentSectionGrades(
            Long schoolId,
            Long yearId,
            Long termId,
            Long sectionId,
            List<StudentSectionGrade> ssgs);
    StudentSectionGrade replaceStudentSectionGrade(
            Long schoolId,
            Long yearId,
            Long termId,
            Long sectionId,
            Long studentId,
            StudentSectionGrade ssg);
    void deleteStudentSectionGrade(
            Long schoolId,
            Long yearId,
            Long termId,
            Long sectionId,
            Long studentId,
            StudentSectionGrade ssg);
    StudentSectionGrade[] getStudentSectionGrades(
            Long schoolId,
            Long yearId,
            Long termId,
            Long sectionId);

    //ASSIGNMENTS
    Assignment createSectionAssignment(
            Long schoolId,
            Long yearId,
            Long termId,
            Long sectionId,
            Assignment ssg);
    Assignment replaceSectionAssignment(
            Long schoolId,
            Long yearId,
            Long termId,
            Long sectionId,
            Assignment ssg);
    void deleteSectionAssignment(
            Long schoolId,
            Long yearId,
            Long termId,
            Long sectionId,
            Assignment ssg);
    Assignment[] getSectionAssignments(
            Long schoolId,
            Long yearId,
            Long termId,
            Long sectionId);

    StudentAssignment createStudentAssignment(
            Long schoolId,
            Long yearId,
            Long termId,
            Long sectionId,
            Long assignmentId,
            StudentAssignment studentAssignment
    );

    //STUDENT ASSIGNMENTS
    void createStudentAssignments(
            Long schoolId,
            Long yearId,
            Long termId,
            Long sectionId,
            Long assignmentId,
            List<StudentAssignment> studentAssignment
    );
    StudentAssignment[] getStudentAssignments(
            Long schoolId,
            Long yearId,
            Long termId,
            Long sectionId,
            Long assignmentId
    );
    void deleteStudentAssignment(
            Long schoolId,
            Long yearId,
            Long termId,
            Long sectionId,
            Long assignmentId,
            StudentAssignment studentAssignment
    );
    void replaceStudentAssignment(
            Long schoolId,
            Long yearId,
            Long termId,
            Long sectionId,
            Long assignmentId,
            StudentAssignment studentAssignment
    );

}
