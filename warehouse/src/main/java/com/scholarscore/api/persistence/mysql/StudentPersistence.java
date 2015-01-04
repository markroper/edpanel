package com.scholarscore.api.persistence.mysql;

import java.util.Collection;

import com.scholarscore.models.Student;

public interface StudentPersistence {
    public Collection<Student> selectAllStudents();

    public Student selectStudent(long studentId);

    public Long createStudent(Student student);

    public Long replaceStudent(long studentId, Student student);

    public Long deleteStudent(long studentId);
}
