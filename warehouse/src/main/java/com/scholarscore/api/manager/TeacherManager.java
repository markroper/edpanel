package com.scholarscore.api.manager;

import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.models.user.Teacher;

import java.util.Collection;

public interface TeacherManager {
    ServiceResponse<Collection<Teacher>> getAllTeachers();
    StatusCode teacherExists(long teacherId);
    ServiceResponse<Teacher> getTeacher(long teacherId);
    ServiceResponse<Long> createTeacher(Teacher teacher);
    ServiceResponse<Long> replaceTeacher(long teacherId, Teacher teacher);
    ServiceResponse<Long> updateTeacher(long teacherId, Teacher teacher);
    ServiceResponse<Long> deleteTeacher(long teacherId);
}
