package com.scholarscore.api.persistence.mysql;

import java.util.Collection;

import com.scholarscore.models.StudentAssignment;

public interface StudentAssignmentPersistence extends EntityPersistence<StudentAssignment> {

    public Collection<StudentAssignment> selectAllAssignmentsOneSectionOneStudent(long sectionId, long studentId);
}
