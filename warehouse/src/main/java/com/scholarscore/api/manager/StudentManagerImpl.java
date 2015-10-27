package com.scholarscore.api.manager;

import com.scholarscore.api.persistence.StudentPersistence;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.user.Student;

import java.util.Collection;

/**
 * Created by cwallace on 9/16/2015.
 */
public class StudentManagerImpl implements StudentManager {

    StudentPersistence studentPersistence;

    OrchestrationManager pm;

    private static final String STUDENT = "student";

    public void setStudentPersistence(StudentPersistence studentPersistence) {
        this.studentPersistence = studentPersistence;
    }

    public void setPm(OrchestrationManager pm) {
        this.pm = pm;
    }

    //Student
    @Override
    public ServiceResponse<Long> createStudent(Student student) {
        return new ServiceResponse<Long>(studentPersistence.createStudent(student));
    }

    @Override
    public StatusCode studentExists(long studentId) {
        Student stud = studentPersistence.select(studentId);
        if(null == stud) {
            return StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{STUDENT, studentId});
        }
        return StatusCodes.getStatusCode(StatusCodeType.OK);
    }

    @Override
    public ServiceResponse<Long> deleteStudent(long studentId) {
        StatusCode code = studentExists(studentId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        studentPersistence.delete(studentId);
        return new ServiceResponse<Long>((Long) null);
    }

    @Override
    public ServiceResponse<Collection<Student>> getAllStudents(Long schoolId) {
        return new ServiceResponse<Collection<Student>>(
                studentPersistence.selectAll(schoolId));
    }

    @Override
    public ServiceResponse<Student> getStudent(long studentId) {
        StatusCode code = studentExists(studentId);
        if(!code.isOK()) {
            return new ServiceResponse<Student>(code);
        }
        return new ServiceResponse<Student>(studentPersistence.select(studentId));
    }

    @Override
    public ServiceResponse<Long> replaceStudent(long studentId, Student student) {
        StatusCode code = studentExists(studentId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        studentPersistence.replaceStudent(studentId, student);
        return new ServiceResponse<Long>(studentId);
    }

    @Override
    public ServiceResponse<Long> updateStudent(long studentId, Student student) {
        StatusCode code = studentExists(studentId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        student.setId(studentId);
        student.mergePropertiesIfNull(studentPersistence.select(studentId));
        replaceStudent(studentId, student);
        return new ServiceResponse<Long>(studentId);
    }

}
