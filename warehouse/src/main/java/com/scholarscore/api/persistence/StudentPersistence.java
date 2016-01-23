package com.scholarscore.api.persistence;

import com.scholarscore.models.notification.group.FilteredStudents;
import com.scholarscore.models.user.Student;

import java.util.Collection;

public interface StudentPersistence {
    Collection<Student> selectAll(Long schoolId);

    Collection<Student> selectAll(Long schoolId, FilteredStudents students);
    
    Collection<Student> selectAllStudentsInSection(long sectionId);

    Student select(long studentId);

    Student select(String username);

    Student selectBySsid(Long ssid);

    Long createStudent(Student student);

    Long replaceStudent(long studentId, Student student);

    Long delete(long studentId);
}
