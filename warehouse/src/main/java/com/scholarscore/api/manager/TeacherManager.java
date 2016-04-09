package com.scholarscore.api.manager;

import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.models.user.Staff;

import java.util.Collection;

public interface TeacherManager {
    ServiceResponse<Collection<Staff>> getAllTeachers();
    ServiceResponse<Collection<Staff>> getAllTeachers(long schoolId);
    StatusCode teacherExists(long teacherId);
    ServiceResponse<Staff> getTeacher(long teacherId);
    ServiceResponse<Long> createTeacher(Staff teacher);
    ServiceResponse<Long> replaceTeacher(long teacherId, Staff teacher);
    ServiceResponse<Long> updateTeacher(long teacherId, Staff teacher);
    ServiceResponse<Long> deleteTeacher(long teacherId);
}
