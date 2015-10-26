package com.scholarscore.api.persistence;

import com.scholarscore.models.user.Teacher;

import java.util.Collection;

public interface TeacherPersistence {
    Collection<Teacher> selectAll();
    
    Teacher select(long teacherId);

    Teacher select(String username);

    Long createTeacher(Teacher teacher);

    void replaceTeacher(long teacherId, Teacher teacher);

    Long delete(long teacherId);
}
