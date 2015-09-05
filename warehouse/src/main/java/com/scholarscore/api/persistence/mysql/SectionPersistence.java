package com.scholarscore.api.persistence.mysql;

import java.util.Collection;

import com.scholarscore.models.Section;

public interface SectionPersistence extends EntityPersistence<Section> {
    /**
     * Returns all the sections in a term in which the student with ID studentId is enrolled.
     * @param termId
     * @param studentId
     * @return
     */
    public Collection<Section> selectAllSectionForStudent(
            long termId, long studentId);
}
