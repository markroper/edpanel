package com.scholarscore.models.state.ma;

import java.util.Objects;

/**
 * Expresses the different fields available on each scored area of the MCAS (Math, English, Science)
 *
 * Created by markroper on 4/10/16.
 */
public class McasTopicScore {
    protected Boolean alternateExam;
    protected McasComplexity complexity;
    protected McasStatus examStatus;
    protected Double rawScore;
    protected Double scaledScore;
    protected McasPerfLevel performanceLevel;
    protected McasPerfLevel2 performanceLevel2;
    protected Long quartile;

    public Boolean getAlternateExam() {
        return alternateExam;
    }

    public void setAlternateExam(Boolean alternateExam) {
        this.alternateExam = alternateExam;
    }

    public McasComplexity getComplexity() {
        return complexity;
    }

    public void setComplexity(McasComplexity complexity) {
        this.complexity = complexity;
    }

    public McasStatus getExamStatus() {
        return examStatus;
    }

    public void setExamStatus(McasStatus examStatus) {
        this.examStatus = examStatus;
    }

    public Double getRawScore() {
        return rawScore;
    }

    public void setRawScore(Double rawScore) {
        this.rawScore = rawScore;
    }

    public Double getScaledScore() {
        return scaledScore;
    }

    public void setScaledScore(Double scaledScore) {
        this.scaledScore = scaledScore;
    }

    public McasPerfLevel getPerformanceLevel() {
        return performanceLevel;
    }

    public void setPerformanceLevel(McasPerfLevel performanceLevel) {
        this.performanceLevel = performanceLevel;
    }

    public McasPerfLevel2 getPerformanceLevel2() {
        return performanceLevel2;
    }

    public void setPerformanceLevel2(McasPerfLevel2 performanceLevel2) {
        this.performanceLevel2 = performanceLevel2;
    }

    public Long getQuartile() {
        return quartile;
    }

    public void setQuartile(Long quartile) {
        this.quartile = quartile;
    }

    @Override
    public int hashCode() {
        return Objects.hash(alternateExam, complexity, examStatus, rawScore, scaledScore, performanceLevel, performanceLevel2, quartile);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final McasTopicScore other = (McasTopicScore) obj;
        return Objects.equals(this.alternateExam, other.alternateExam)
                && Objects.equals(this.complexity, other.complexity)
                && Objects.equals(this.examStatus, other.examStatus)
                && Objects.equals(this.rawScore, other.rawScore)
                && Objects.equals(this.scaledScore, other.scaledScore)
                && Objects.equals(this.performanceLevel, other.performanceLevel)
                && Objects.equals(this.performanceLevel2, other.performanceLevel2)
                && Objects.equals(this.quartile, other.quartile);
    }
}
