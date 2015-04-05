package com.scholarscore.api.persistence.mysql;

import java.util.Collection;

import com.scholarscore.models.Student;

public interface StudentPersistence {
    public Collection<Student> selectAll();
    
    public Collection<Student> selectAllStudentsInSection(long sectionId);

    public Student select(long studentId);

    public Long createStudent(Student student);

    public Long replaceStudent(long studentId, Student student);

    public Long delete(long studentId);
}
