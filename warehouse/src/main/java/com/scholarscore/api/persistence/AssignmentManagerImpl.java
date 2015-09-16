package com.scholarscore.api.persistence;

import com.scholarscore.api.persistence.mysql.EntityPersistence;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.Assignment;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

/**
 * Created by cwallace on 9/16/2015.
 */
public class AssignmentManagerImpl implements AssignmentManager {


    private static final String SECTION_ASSIGNMENT = "section assignment";

    private EntityPersistence<Assignment> assignmentPersistence;

    private PersistenceManager pm;

    public void setAssignmentPersistence(EntityPersistence<Assignment> assignmentPersistence) {
        this.assignmentPersistence = assignmentPersistence;
    }

    public void setPm(PersistenceManager pm) {
        this.pm = pm;
    }

    @Override
    public ServiceResponse<Collection<Assignment>> getAllAssignments(
            long schoolId, long yearId, long termId, long sectionId) {
        StatusCode code = pm.getSectionManager().sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.isOK()) {
            return new ServiceResponse<Collection<Assignment>>(code);
        }
        return new ServiceResponse<Collection<Assignment>>(assignmentPersistence.selectAll(sectionId));
    }

    //SECTION ASSIGNMENTS
    @Override
    public StatusCode assignmentExists(long schoolId, long yearId,
                                       long termId, long sectionId, long sectionAssignmentId) {
        StatusCode code = pm.getSectionManager().sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.isOK()) {
            return code;
        }
        Assignment assignment = assignmentPersistence.select(sectionId, sectionAssignmentId);
        if(null == assignment) {
            return StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND,
                    new Object[]{SECTION_ASSIGNMENT, sectionAssignmentId});
        }
        return StatusCodes.getStatusCode(StatusCodeType.OK);
    }

    @Override
    public ServiceResponse<Assignment> getAssignment(
            long schoolId, long yearId, long termId, long sectionId,
            long sectionAssignmentId) {
        StatusCode code = assignmentExists(schoolId, yearId, termId, sectionId, sectionAssignmentId);
        if(!code.isOK()) {
            return new ServiceResponse<Assignment>(code);
        }
        return new ServiceResponse<Assignment>(assignmentPersistence.select(sectionId, sectionAssignmentId));
    }

    @Override
    public ServiceResponse<Long> createAssignment(long schoolId,
                                                  long yearId, long termId, long sectionId,
                                                  Assignment sectionAssignment) {
        StatusCode code = pm.getSectionManager().sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        return new ServiceResponse<Long>(assignmentPersistence.insert(sectionId, sectionAssignment));
    }

    @Override
    public ServiceResponse<Long> replaceAssignment(long schoolId,
                                                   long yearId, long termId, long sectionId, long sectionAssignmentId,
                                                   Assignment sectionAssignment) {
        StatusCode code = assignmentExists(schoolId, yearId, termId, sectionId, sectionAssignmentId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        return new ServiceResponse<Long>(
                assignmentPersistence.update(sectionId, sectionAssignmentId, sectionAssignment));
    }

    @Override
    public ServiceResponse<Long> updateAssignment(long schoolId,
                                                  long yearId, long termId, long sectionId, long sectionAssignmentId,
                                                  Assignment sectionAssignment) {
        StatusCode code = assignmentExists(schoolId, yearId, termId, sectionId, sectionAssignmentId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        sectionAssignment.mergePropertiesIfNull(assignmentPersistence.select(sectionId, sectionAssignmentId));
        assignmentPersistence.update(sectionId, sectionAssignmentId, sectionAssignment);
        return new ServiceResponse<Long>(sectionAssignmentId);
    }

    @Override
    public ServiceResponse<Long> deleteAssignment(long schoolId,
                                                  long yearId, long termId, long sectionId, long sectionAssignmentId) {
        StatusCode code = assignmentExists(schoolId, yearId, termId, sectionId, sectionAssignmentId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        assignmentPersistence.delete(sectionAssignmentId);
        return new ServiceResponse<Long>((Long) null);
    }
}
