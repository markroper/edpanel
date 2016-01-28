package com.scholarscore.api.persistence;

import com.scholarscore.models.grade.StudentSectionGrade;

import java.util.Collection;
import java.util.List;

public interface StudentSectionGradePersistence {
    public Collection<StudentSectionGrade> selectAll(
            long sectionId);

    public Collection<StudentSectionGrade> selectAllByStudent(
            long studentId);

    public Collection<StudentSectionGrade> selectAllByTerm(
            long termId, long schoolId);

    public StudentSectionGrade select(
            long sectionId,
            long student);

    public Long insert (
            long sectionId,
            long studentId,
            StudentSectionGrade entity);

    public void insertAll (
            long sectionId,
            List<StudentSectionGrade> entities);

    public Long update(
            long sectionId,
            long studentId,
            StudentSectionGrade entity);

    public Long delete(long sectionId, long studentId);
}
