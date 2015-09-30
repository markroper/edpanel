package com.scholarscore.api.persistence;

import java.util.Collection;

import com.scholarscore.models.Student;

public interface StudentPersistence {
    Collection<Student> selectAll();
    
    Collection<Student> selectAllStudentsInSection(long sectionId);

    Student select(long studentId);

    Student select(String username);

    Long createStudent(Student student);

    Long replaceStudent(long studentId, Student student);

    Long delete(long studentId);
}
