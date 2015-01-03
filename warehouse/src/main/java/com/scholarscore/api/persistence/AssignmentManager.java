package com.scholarscore.api.persistence;

import java.util.Collection;

import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.Assignment;

public interface AssignmentManager {
    
    public ServiceResponse<Collection<Assignment>> getAllAssignments(long schoolId, long courseId);

    public StatusCode assignmentExists(long schoolId, long courseId, long assignmentId);
    
    public ServiceResponse<Assignment> getAssignment(long schoolId, long courseId, long assignmentId);

    public ServiceResponse<Long> createAssignment(long schoolId, long courseId, Assignment assignment);

    public ServiceResponse<Long> replaceAssignment(long schoolId, long courseId, long assignmentId, Assignment assignment);
    
    public ServiceResponse<Long> updateAssignment(long schoolId, long courseId, long assignmentId, Assignment assignment);

    public ServiceResponse<Long> deleteAssignment(long schoolId, long courseId, long assignmentId);
}