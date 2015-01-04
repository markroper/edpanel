package com.scholarscore.api.persistence.mysql;

import java.util.Collection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.scholarscore.models.Section;
import com.scholarscore.models.Term;

public interface SectionPersistence {
    
    public Collection<Section> selectAllSections(
            long termId);

    public Section selectSection(
            long termId,
            long sectionId);

    public Long insertSection (
            long termId,
            Section section) throws JsonProcessingException;

    public Long updateSection(
            long termId,
            long sectionId,
            Section section) throws JsonProcessingException;

    public Long deleteSection(long sectionId);
}
