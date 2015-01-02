package com.scholarscore.api.persistence;

import com.scholarscore.models.Student;

import java.util.Collection;

/**
 * User: jordan
 * Date: 12/21/14
 * Time: 5:33 PM
 *
 * Classes that implement this interface are able to manage persistence of Student records.
 */
public interface StudentManager {

    public Collection<Student> getAllStudents();

    public boolean studentExists(long studentId);
    public Student getStudent(long studentId);

    // Creates a student and populates the Id field (if ID is specified it will be ignored)
    public long createStudent(Student student);

    // replace the existing student record with this one (ID will be taken from the student)
    public void saveStudent(Student student);

    public void deleteStudent(long studentId);

}
