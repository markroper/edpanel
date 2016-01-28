package com.scholarscore.models.grade;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by markroper on 11/12/15.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Score implements Serializable {
    protected String comment;
    protected Boolean manuallyOverridden;
    protected String letterGrade;
    protected Double score;
    protected Long termId;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getManuallyOverridden() {
        return manuallyOverridden;
    }

    public void setManuallyOverridden(Boolean manuallyOverridden) {
        this.manuallyOverridden = manuallyOverridden;
    }

    public String getLetterGrade() {
        return letterGrade;
    }

    public void setLetterGrade(String letterGrade) {
        this.letterGrade = letterGrade;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Long getTermId() {
        return termId;
    }

    public void setTermId(Long termId) {
        this.termId = termId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(comment, manuallyOverridden, letterGrade, score, termId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Score other = (Score) obj;
        return Objects.equals(this.comment, other.comment)
                && Objects.equals(this.manuallyOverridden, other.manuallyOverridden)
                && Objects.equals(this.letterGrade, other.letterGrade)
                && Objects.equals(this.score, other.score)
                && Objects.equals(this.termId, other.termId);
    }

    public static class ScoreBuilder {
        protected String comment;
        protected Boolean manuallyOverridden;
        protected String letterGrade;
        protected Double score;
        protected Long termId;
        protected Boolean excludeFromGpa;

        public ScoreBuilder(){
        }

        public ScoreBuilder withExcludeFromGpa(final Boolean ex){
            this.excludeFromGpa = ex;
            return this;
        }

        public ScoreBuilder withComment(final String comment){
            this.comment = comment;
            return this;
        }

        public ScoreBuilder withManuallyOverridden(final Boolean manuallyOverridden){
            this.manuallyOverridden = manuallyOverridden;
            return this;
        }

        public ScoreBuilder withLetterGrade(final String letterGrade){
            this.letterGrade = letterGrade;
            return this;
        }

        public ScoreBuilder withScore(final Double score){
            this.score = score;
            return this;
        }

        public ScoreBuilder withTermId(final Long termId){
            this.termId = termId;
            return this;
        }

        public Score build(){
            Score s = new Score();
            s.setComment(comment);
            s.setManuallyOverridden(manuallyOverridden);
            s.setLetterGrade(letterGrade);
            s.setScore(score);
            s.setTermId(termId);
            return s;
        }

        public Score getInstance() {
            return new Score();
        }
    }
}
