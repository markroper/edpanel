package com.scholarscore.api.persistence;

import java.util.Collection;

import com.scholarscore.models.StudentAssignment;

public interface StudentAssignmentPersistence extends EntityPersistence<StudentAssignment> {

    public Collection<StudentAssignment> selectAllAssignmentsOneSectionOneStudent(long sectionId, long studentId);

    public Collection<StudentAssignment> selectAllAttendanceSection(long sectionId, long studentId);
}
