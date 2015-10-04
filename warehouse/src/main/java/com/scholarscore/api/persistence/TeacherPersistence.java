package com.scholarscore.api.persistence;

import java.util.Collection;

import com.scholarscore.api.util.StatusCode;
import com.scholarscore.models.user.Administrator;
import com.scholarscore.models.user.Teacher;

public interface TeacherPersistence {
    Collection<Teacher> selectAll();
    
    Teacher select(long teacherId);

    Teacher select(String username);

    Long createTeacher(Teacher teacher);

    void replaceTeacher(long teacherId, Teacher teacher);

    Long delete(long teacherId);
}
