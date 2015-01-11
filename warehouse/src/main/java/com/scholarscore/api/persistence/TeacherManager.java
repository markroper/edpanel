package com.scholarscore.api.persistence;

import java.util.Collection;

import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.models.Teacher;

public interface TeacherManager {
    public ServiceResponse<Collection<Teacher>> getAllTeachers();
    public StatusCode teacherExists(long teacherId);
    public ServiceResponse<Teacher> getTeacher(long teacherId);
    public ServiceResponse<Long> createTeacher(Teacher teacher);
    public ServiceResponse<Long> replaceTeacher(long teacherId, Teacher teacher);
    public ServiceResponse<Long> updateTeacher(long teacherId, Teacher teacher);
    public ServiceResponse<Long> deleteTeacher(long teacherId);
    
}
