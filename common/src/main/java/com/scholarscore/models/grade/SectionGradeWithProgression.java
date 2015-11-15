package com.scholarscore.models.grade;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.Score;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by markroper on 11/14/15.
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SectionGradeWithProgression {
    protected List<GradeAsOfWeek> weeklyGradeProgression;
    protected Double currentOverallGrade;
    protected Map<String, Double> currentCategoryGrades;
    protected HashMap<Long, Score> termGrades;

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

    public HashMap<Long, Score> getTermGrades() {
        return termGrades;
    }

    public void setTermGrades(HashMap<Long, Score> termGrades) {
        this.termGrades = termGrades;
    }

    public Map<String, Double> getCurrentCategoryGrades() {
        return currentCategoryGrades;
    }

    public void setCurrentCategoryGrades(Map<String, Double> currentCategoryGrades) {
        this.currentCategoryGrades = currentCategoryGrades;
    }

    @Override
    public int hashCode() {
        return Objects.hash(weeklyGradeProgression, currentOverallGrade, termGrades, currentCategoryGrades);
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
                && Objects.equals(this.termGrades, other.termGrades)
                && Objects.equals(this.currentCategoryGrades, other.currentCategoryGrades)
                && Objects.equals(this.currentOverallGrade, other.currentOverallGrade);
    }
}
