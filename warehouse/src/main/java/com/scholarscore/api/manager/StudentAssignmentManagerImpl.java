package com.scholarscore.api.manager;

import com.scholarscore.api.persistence.StudentAssignmentPersistence;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.assignment.StudentAssignment;

import java.util.Collection;
import java.util.List;

/**
 * Created by cwallace on 9/16/2015.
 */
public class StudentAssignmentManagerImpl implements  StudentAssignmentManager {

    StudentAssignmentPersistence studentAssignmentPersistence;

    OrchestrationManager pm;

    private static final String STUDENT_ASSIGNMENT = "student assignment";

    public void setStudentAssignmentPersistence(StudentAssignmentPersistence studentAssignmentPersistence) {
        this.studentAssignmentPersistence = studentAssignmentPersistence;
    }

    public void setPm(OrchestrationManager pm) {
        this.pm = pm;
    }

    //STUDENT ASSIGNMENTS
    @Override
    public ServiceResponse<Collection<StudentAssignment>> getAllStudentAssignments(
            long schoolId, long yearId, long termId, long sectionId,
            long sectAssignmentId) {
        StatusCode code = pm.getAssignmentManager().assignmentExists(schoolId, yearId, termId, sectionId, sectAssignmentId);
        if(!code.isOK()) {
            return new ServiceResponse<Collection<StudentAssignment>>(code);
        }
        Collection<StudentAssignment> sas = studentAssignmentPersistence.selectAll(sectAssignmentId);
        return new ServiceResponse<Collection<StudentAssignment>>(sas);
    }

    @Override
    public StatusCode studentAssignmentExists(long schoolId, long yearId,
                                              long termId, long sectionId, long sectionAssignmentId,
                                              long studentAssignmentId) {
        StatusCode code = pm.getAssignmentManager().assignmentExists(schoolId, yearId, termId, sectionId, sectionAssignmentId);
        if(!code.isOK()) {
            return code;
        }
        StudentAssignment sa = studentAssignmentPersistence.select(sectionAssignmentId, studentAssignmentId);
        if(null == sa) {
            return StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND,
                    new Object[]{STUDENT_ASSIGNMENT, studentAssignmentId});
        }
        return StatusCodes.getStatusCode(StatusCodeType.OK);
    }

    @Override
    public ServiceResponse<StudentAssignment> getStudentAssignment(
            long schoolId, long yearId, long termId, long sectionId,
            long sectionAssignmentId, long studentAssignmentId) {
        StatusCode code = studentAssignmentExists(schoolId, yearId, termId, sectionId,
                sectionAssignmentId, studentAssignmentId);
        if(!code.isOK()) {
            return new ServiceResponse<StudentAssignment>(code);
        }
        StudentAssignment sa = studentAssignmentPersistence.select(sectionAssignmentId, studentAssignmentId);
        return new ServiceResponse<StudentAssignment>(sa);
    }

    @Override
    public ServiceResponse<Collection<StudentAssignment>> getOneSectionOneStudentsAssignments(
            long studentId, long schoolId, long yearId, long termId,
            long sectionId) {
        Collection<StudentAssignment> sas =
                studentAssignmentPersistence.selectAllAssignmentsOneSectionOneStudent(sectionId, studentId);
        return new ServiceResponse<Collection<StudentAssignment>>(sas);
    }

    @Override
    public ServiceResponse<Long> createStudentAssignment(long schoolId,
                                                         long yearId, long termId, long sectionId, long sectionAssignmentId,
                                                         StudentAssignment studentAssignment) {
        StatusCode code = pm.getAssignmentManager().assignmentExists(schoolId, yearId, termId, sectionId, sectionAssignmentId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        return new ServiceResponse<Long>(
                studentAssignmentPersistence.insert(sectionAssignmentId, studentAssignment));
    }

    @Override
    public ServiceResponse<Void> createBulkStudentAssignment(long schoolId, long yrId, long tId, long sId, long assignId, List<StudentAssignment> studentAssignments) {
        StatusCode code = pm.getAssignmentManager().assignmentExists(schoolId, yrId, tId, sId, assignId);
        if(!code.isOK()) {
            return new ServiceResponse<Void>(code);
        }
        studentAssignmentPersistence.insertAll(assignId, studentAssignments);
        return new ServiceResponse<Void>( (Void)null );
    }

    @Override
    public ServiceResponse<Long> replaceStudentAssignment(long schoolId,
                                                          long yearId, long termId, long sectionId, long sectionAssignmentId,
                                                          long studentAssignmentId, StudentAssignment studentAssignment) {
        StatusCode code = studentAssignmentExists(schoolId, yearId, termId, sectionId,
                sectionAssignmentId, studentAssignmentId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        return new ServiceResponse<Long>(studentAssignmentPersistence.update(
                sectionAssignmentId, studentAssignmentId, studentAssignment));
    }

    @Override
    public ServiceResponse<Long> updateStudentAssignment(long schoolId,
                                                         long yearId, long termId, long sectionId, long sectionAssignmentId,
                                                         long studentAssignmentId, StudentAssignment studentAssignment) {
        StatusCode code = studentAssignmentExists(schoolId, yearId, termId, sectionId,
                sectionAssignmentId, studentAssignmentId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        studentAssignment.setId(studentAssignmentId);
        studentAssignment.mergePropertiesIfNull(
                studentAssignmentPersistence.select(sectionAssignmentId, studentAssignmentId));
        return new ServiceResponse<Long>(
                studentAssignmentPersistence.update(sectionAssignmentId, studentAssignmentId, studentAssignment));
    }

    @Override
    public ServiceResponse<Long> deleteStudentAssignment(long schoolId,
                                                         long yearId, long termId, long sectionId, long sectionAssignmentId,
                                                         long studentAssignmentId) {
        StatusCode code = studentAssignmentExists(schoolId, yearId, termId, sectionId,
                sectionAssignmentId, studentAssignmentId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        studentAssignmentPersistence.delete(studentAssignmentId);
        return new ServiceResponse<Long>((Long) null);
    }
}
