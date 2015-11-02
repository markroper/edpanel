package com.scholarscore.api.persistence;

import com.scholarscore.models.PrepScore;
import com.scholarscore.models.user.Student;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface StudentPersistence {
    Collection<Student> selectAll(Long schoolId);
    
    Collection<Student> selectAllStudentsInSection(long sectionId);

    Student select(long studentId);

    Student select(String username);

    Student selectBySsid(Long ssid);

    Long createStudent(Student student);

    Long replaceStudent(long studentId, Student student);

    Long delete(long studentId);
}
