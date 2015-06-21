package com.scholarscore.api.persistence.mysql;

import java.util.Collection;

import com.scholarscore.models.Teacher;

public interface TeacherPersistence {
    public Collection<Teacher> selectAll();
    
    public Teacher select(long teacherId);

    public Long createTeacher(Teacher teacher);

    public Long replaceTeacher(long teacherId, Teacher teacher);

    public Long delete(long teacherId);
}
