package com.scholarscore.models.grade;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

/**
 * Created by markroper on 11/14/15.
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SectionGradeWithProgression {
    protected List<GradeAsOfWeek> weeklyGradeProgression;
    protected Double currentOverallGrade;

    public List<GradeAsOfWeek> getWeeklyGradeProgression() {
        return weeklyGradeProgression;
    }

    public void setWeeklyGradeProgression(List<GradeAsOfWeek> weeklyGradeProgression) {
        this.weeklyGradeProgression = weeklyGradeProgression;
    }

    public Double getCurrentOverallGrade() {
        return currentOverallGrade;
    }

    public void setCurrentOverallGrade(Double currentOverallGrade) {
        this.currentOverallGrade = currentOverallGrade;
    }

    @Override
    public int hashCode() {
        return Objects.hash(weeklyGradeProgression, currentOverallGrade);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final SectionGradeWithProgression other = (SectionGradeWithProgression) obj;
        return Objects.equals(this.weeklyGradeProgression, other.weeklyGradeProgression)
                && Objects.equals(this.currentOverallGrade, other.currentOverallGrade);
    }
}
