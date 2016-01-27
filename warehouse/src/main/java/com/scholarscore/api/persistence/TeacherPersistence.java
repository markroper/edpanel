package com.scholarscore.api.persistence;

import com.scholarscore.models.user.Staff;

import java.util.Collection;

public interface TeacherPersistence {
    Collection<Staff> selectAll();
    
    Staff select(long teacherId);

    Staff select(String username);

    Long createTeacher(Staff teacher);

    void replaceTeacher(long teacherId, Staff teacher);

    Long delete(long teacherId);
}
