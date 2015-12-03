package com.scholarscore.api.manager;

import com.scholarscore.api.persistence.TeacherPersistence;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.user.Teacher;

import java.util.Collection;

/**
 * Created by cwallace on 9/16/2015.
 */
public class TeacherManagerImpl implements TeacherManager {
    private static final int CREATE_RETRY_MAX = 15;
    private TeacherPersistence teacherPersistence;

    private OrchestrationManager pm;

    private static final String TEACHER = "teacher";

    public void setTeacherPersistence(TeacherPersistence teacherPersistence) {
        this.teacherPersistence = teacherPersistence;
    }

    public void setPm(OrchestrationManager pm) {
        this.pm = pm;
    }

    @Override
    public ServiceResponse<Long> createTeacher(Teacher teacher) {
        String initUsername = teacher.getUsername();
        boolean retry = true;
        int suffix = 0;
        while(retry && suffix < CREATE_RETRY_MAX) {
            retry = false;
            try {
                return new ServiceResponse<Long>(teacherPersistence.createTeacher(teacher));
            } catch (Throwable e) {
                suffix++;
                retry = true;
                if (null == initUsername) {
                    initUsername = teacher.getUsername();
                }
                teacher.setUsername(initUsername + suffix);
            }
        }
        return null;
    }

    @Override
    public StatusCode teacherExists(long teacherId) {
        Teacher stud = teacherPersistence.select(teacherId);
        if(null == stud) {
            return StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{TEACHER, teacherId});
        }
        return StatusCodes.getStatusCode(StatusCodeType.OK);
    }


    @Override
    public ServiceResponse<Long> deleteTeacher(long teacherId) {
        StatusCode code = teacherExists(teacherId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        teacherPersistence.delete(teacherId);
        return new ServiceResponse<Long>((Long) null);
    }

    @Override
    public ServiceResponse<Collection<Teacher>> getAllTeachers() {
        return new ServiceResponse<Collection<Teacher>>(
                teacherPersistence.selectAll());
    }

    @Override
    public ServiceResponse<Teacher> getTeacher(long teacherId) {
        StatusCode code = teacherExists(teacherId);
        if(!code.isOK()) {
            return new ServiceResponse<Teacher>(code);
        }
        return new ServiceResponse<Teacher>(teacherPersistence.select(teacherId));
    }

    @Override
    public ServiceResponse<Long> replaceTeacher(long teacherId, Teacher teacher) {
        StatusCode code = teacherExists(teacherId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        teacherPersistence.replaceTeacher(teacherId, teacher);
        return new ServiceResponse<Long>(teacherId);
    }

    @Override
    public ServiceResponse<Long> updateTeacher(long teacherId, Teacher teacher) {
        StatusCode code = teacherExists(teacherId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        teacher.setId(teacherId);
        teacher.mergePropertiesIfNull(teacherPersistence.select(teacherId));
        replaceTeacher(teacherId, teacher);
        return new ServiceResponse<Long>(teacherId);
    }
}
