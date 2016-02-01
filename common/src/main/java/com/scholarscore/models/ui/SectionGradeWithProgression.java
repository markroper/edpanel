package com.scholarscore.models.ui;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.grade.Score;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A conglomerate object that is not persisted.  Its purpose is to communicate to the client various calculated
 * values related to a student's performance in a section including the current grade, grade by assignment type,
 * the grade pro    gression bucketed by week, and the grades within the various terms that the section spans.
 *
 * Created by markroper on 11/14/15.
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SectionGradeWithProgression {
    protected List<ScoreAsOfWeek> weeklyGradeProgression;
    protected Double currentOverallGrade;
    protected Map<String, Double> currentCategoryGrades;
    protected HashMap<Long, Score> termGrades;

    public List<ScoreAsOfWeek> getWeeklyGradeProgression() {
        return weeklyGradeProgression;
    }

    public void setWeeklyGradeProgression(List<ScoreAsOfWeek> weeklyGradeProgression) {
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
