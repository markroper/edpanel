package com.scholarscore.api.persistence;

import com.scholarscore.models.assignment.StudentAssignment;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface StudentAssignmentPersistence extends EntityPersistence<StudentAssignment> {

    public Collection<StudentAssignment> selectAllAssignmentsOneSectionOneStudent(long sectionId, long studentId);

    public Collection<StudentAssignment> selectAllAttendanceSection(long sectionId, long studentId);

    public Collection<StudentAssignment> selectAllBetweenDates(long studentId, LocalDate start, LocalDate end);

    public List<Long> insertAll(long assignmentId, List<StudentAssignment> studentAssignmentList);
}
