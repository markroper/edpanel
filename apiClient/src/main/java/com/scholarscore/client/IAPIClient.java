package com.scholarscore.client;

import com.scholarscore.models.*;
import com.scholarscore.models.user.Administrator;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.Teacher;
import com.scholarscore.models.user.User;

import java.util.Collection;

/**
 * Created by mattg on 7/3/15.
 */
public interface IAPIClient {
    School createSchool(School school);
    School getSchool(Long id);
    Student createStudent(Student student);
    Student updateStudent(Long studentId, Student student);
    Collection<Student> getStudents();
    
    Collection<Teacher> getTeachers();
    
    Collection<Behavior> getBehaviors(Long studentId);
    Behavior createBehavior(Long studentId, Behavior behavior);
    Behavior updateBehavior(Long studentId, Long behaviorId, Behavior behavior);
    
    Teacher createTeacher(Teacher teacher);
    Administrator createAdministrator(Administrator administrator);
    User createUser(User login);
    Course createCourse(Long schoolId, Course course);
    SchoolYear createSchoolYear(Long schoolId, SchoolYear year);
    Term createTerm(Long schoolId, Long schoolYearId, Term year);
    Section createSection(Long schoolId, Long schoolYearId, Long termId, Section section);
    StudentSectionGrade createStudentSectionGrade(
            Long schoolId, 
            Long yearId,
            Long termId,
            Long sectionIf,
            Long studentId,
            StudentSectionGrade ssg);
}
