package com.scholarscore.etl.schoolbrains;

import com.scholarscore.models.Section;

import java.util.List;
import java.util.Objects;

/**
 * Created by markroper on 4/14/16.
 */
public class SectionContainer {
    Section section;
    //Terms in which the section is active within the year
    //position 0 -> term 1, position 1 -> term 2 and so on.
    List<Boolean> terms;
    String schoolYearId;

    public SectionContainer(Section section,  List<Boolean> terms, String schoolYearId) {
        this.section = section;
        this.terms = terms;
        this.schoolYearId = schoolYearId;
    }

    public String getSchoolYearId() {
        return schoolYearId;
    }

    public void setSchoolYearId(String schoolYearId) {
        this.schoolYearId = schoolYearId;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public List<Boolean> getTerms() {
        return terms;
    }

    public void setTerms(List<Boolean> terms) {
        this.terms = terms;
    }

    @Override
    public int hashCode() {
        return Objects.hash(section, terms, schoolYearId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final SectionContainer other = (SectionContainer) obj;
        return Objects.equals(this.section, other.section)
                && Objects.equals(this.schoolYearId, other.schoolYearId)
                && Objects.equals(this.terms, other.terms);
    }
}