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
            Long sectionId,
            Long studentId,
            StudentSectionGrade ssg);

    Assignment createSectionAssignment(
            Long schoolId,
            Long yearId,
            Long termId,
            Long sectionId,
            Assignment ssg);

    StudentAssignment createStudentAssignment(
            Long schoolId,
            Long yearId,
            Long termId,
            Long sectionId,
            Long assignmentId,
            StudentAssignment studentAssignment
    );

    void createStudentAssignments(
            Long schoolId,
            Long yearId,
            Long termId,
            Long sectionId,
            Long assignmentId,
            List<StudentAssignment> studentAssignment
    );
}
