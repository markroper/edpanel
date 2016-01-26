package com.scholarscore.api.manager;

import com.scholarscore.api.persistence.StudentAssignmentPersistence;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.assignment.StudentAssignment;
import com.scholarscore.models.user.User;
import com.scholarscore.models.user.UserType;

import java.time.LocalDate;
import java.util.ArrayList;
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
    public ServiceResponse<Collection<StudentAssignment>> getAllStudentAssignmentsBetweenDates(
            long studentId, LocalDate startDate, LocalDate endDate) {
        Collection<StudentAssignment> sas =
                studentAssignmentPersistence.selectAllBetweenDates(studentId, startDate, endDate);
        return new ServiceResponse<>(sas);
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
        User curr = pm.getUserManager().getCurrentUserDetails().getUser();
        if(UserType.STUDENT.equals(curr.getType())) {
            List<StudentAssignment> filtered = new ArrayList<>();
            if(null != sas) {
                for(StudentAssignment sAss: sas) {
                    if(curr.getId().equals(sAss.getStudent().getId())) {
                        filtered.add(sAss);
                    }
                }
            }
        }
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
            return new ServiceResponse<>(code);
        }
        StudentAssignment sa = studentAssignmentPersistence.select(sectionAssignmentId, studentAssignmentId);
        User curr = pm.getUserManager().getCurrentUserDetails().getUser();
        if(null != sa && curr.getType().equals(UserType.STUDENT) && !curr.getId().equals(sa.getStudent().getId())) {
            return new ServiceResponse<>(
                    StatusCodes.getStatusCode(
                            StatusCodeType.MODEL_NOT_FOUND, new Object[]{ STUDENT_ASSIGNMENT, studentAssignmentId }));
        }
        return new ServiceResponse<>(sa);
    }

    @Override
    public ServiceResponse<Collection<StudentAssignment>> getOneSectionOneStudentsAssignments(
            long studentId, long sectionId) {
        Collection<StudentAssignment> sas =
                studentAssignmentPersistence.selectAllAssignmentsOneSectionOneStudent(sectionId, studentId);
        return new ServiceResponse<>(sas);
    }

    @Override
    public ServiceResponse<Long> createStudentAssignment(long schoolId,
                                                         long yearId, long termId, long sectionId, long sectionAssignmentId,
                                                         StudentAssignment studentAssignment) {
        StatusCode code = pm.getAssignmentManager().assignmentExists(schoolId, yearId, termId, sectionId, sectionAssignmentId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        return new ServiceResponse<>(
                studentAssignmentPersistence.insert(sectionAssignmentId, studentAssignment));
    }

    @Override
    public ServiceResponse<List<Long>> createBulkStudentAssignment(long schoolId, long yrId, long tId, long sId, long assignId, List<StudentAssignment> studentAssignments) {
        StatusCode code = pm.getAssignmentManager().assignmentExists(schoolId, yrId, tId, sId, assignId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        return new ServiceResponse<>(studentAssignmentPersistence.insertAll(assignId, studentAssignments));
    }

    @Override
    public ServiceResponse<Long> replaceStudentAssignment(long schoolId,
                                                          long yearId, long termId, long sectionId, long sectionAssignmentId,
                                                          long studentAssignmentId, StudentAssignment studentAssignment) {
        StatusCode code = studentAssignmentExists(schoolId, yearId, termId, sectionId,
                sectionAssignmentId, studentAssignmentId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        return new ServiceResponse<>(studentAssignmentPersistence.update(
                sectionAssignmentId, studentAssignmentId, studentAssignment));
    }

    @Override
    public ServiceResponse<Long> updateStudentAssignment(long schoolId,
                                                         long yearId, long termId, long sectionId, long sectionAssignmentId,
                                                         long studentAssignmentId, StudentAssignment studentAssignment) {
        StatusCode code = studentAssignmentExists(schoolId, yearId, termId, sectionId,
                sectionAssignmentId, studentAssignmentId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        studentAssignment.setId(studentAssignmentId);
        studentAssignment.mergePropertiesIfNull(
                studentAssignmentPersistence.select(sectionAssignmentId, studentAssignmentId));
        return new ServiceResponse<>(
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
