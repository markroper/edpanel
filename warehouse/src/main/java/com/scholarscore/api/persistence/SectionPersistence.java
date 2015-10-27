package com.scholarscore.api.persistence;

import com.scholarscore.models.Section;

import java.util.Collection;

public interface SectionPersistence extends EntityPersistence<Section> {
    /**
     * Returns all the sections in a term in which the student with ID studentId is enrolled.
     * @param termId
     * @param studentId
     * @return
     */
    public Collection<Section> selectAllSectionForStudent(
            long termId, long studentId);
    
    /**
     * Returns all the sections in a term in taught by the teacher with teacherId
     * @param termId
     * @param teacherId
     * @return
     */
    public Collection<Section> selectAllSectionForTeacher(
            long termId, long teacherId);

    /**
     * Returns all the sections in a term in taught by the teacher with teacherId
     * @param schoolId
     * @return
     */
    public Collection<Section> selectAllInSchool(
            long schoolId);
}
