package com.scholarscore.models.ui;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Created by markroper on 11/14/15.
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScoreAsOfWeek implements Serializable {
    public LocalDate weekEnding;
    public Double score;

    public ScoreAsOfWeek() {

    }

    public ScoreAsOfWeek(LocalDate date, Double score) {
        this.weekEnding = date;
        this.score = score;
    }

    public LocalDate getWeekEnding() {
        return weekEnding;
    }

    public void setWeekEnding(LocalDate weekEnding) {
        this.weekEnding = weekEnding;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    @Override
    public int hashCode() {
        return Objects.hash(weekEnding, score);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ScoreAsOfWeek other = (ScoreAsOfWeek) obj;
        return Objects.equals(this.weekEnding, other.weekEnding)
                && Objects.equals(this.score, other.score);
    }
}
