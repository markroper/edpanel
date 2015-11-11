package com.scholarscore.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * Created by markroper on 11/10/15.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SectionGrades implements Serializable {
    protected Map<Long, Double> termIdToGrade;
    protected Double finalGrade;

    public Map<Long, Double> getTermIdToGrade() {
        return termIdToGrade;
    }

    public void setTermIdToGrade(Map<Long, Double> termIdToGrade) {
        this.termIdToGrade = termIdToGrade;
    }

    public Double getFinalGrade() {
        return finalGrade;
    }

    public void setFinalGrade(Double finalGrade) {
        this.finalGrade = finalGrade;
    }

    @Override
    public int hashCode() {
        return Objects.hash(termIdToGrade, finalGrade);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final SectionGrades other = (SectionGrades) obj;
        return Objects.equals(this.termIdToGrade, other.termIdToGrade)
                && Objects.equals(this.finalGrade, other.finalGrade);
    }
}
