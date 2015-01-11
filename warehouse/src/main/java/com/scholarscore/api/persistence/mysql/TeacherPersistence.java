package com.scholarscore.api.persistence.mysql;

import java.util.Collection;

import com.scholarscore.models.Teacher;

public interface TeacherPersistence {
    public Collection<Teacher> selectAllTeachers();
    
    public Teacher selectTeacher(long teacherId);

    public Long createTeacher(Teacher teacher);

    public Long replaceTeacher(long teacherId, Teacher teacher);

    public Long deleteTeacher(long teacherId);
}
