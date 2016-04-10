package com.scholarscore.models.state.ma;

import com.scholarscore.models.HibernateConsts;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

/**
 * Expresses the different fields available on each scored area of the MCAS (Math, English, Science)
 *
 * Created by markroper on 4/10/16.
 */
@Entity(name = HibernateConsts.MA_MCAS_TOPIC_TABLE)
public class McasTopicScore {
    protected Long id;
    protected Boolean alternateExam;
    protected McasComplexity complexity;
    protected McasStatus examStatus;
    protected Double rawScore;
    protected Double scaledScore;
    protected McasPerfLevel performanceLevel;
    protected McasPerfLevel2 performanceLevel2;
    protected Long quartile;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = HibernateConsts.MA_MCAS_TOPIC_ID)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = HibernateConsts.ALT_TEST)
    public Boolean getAlternateExam() {
        return alternateExam;
    }

    public void setAlternateExam(Boolean alternateExam) {
        this.alternateExam = alternateExam;
    }

    @Column(name = HibernateConsts.COMPLEXITY)
    @Enumerated(EnumType.STRING)
    public McasComplexity getComplexity() {
        return complexity;
    }

    public void setComplexity(McasComplexity complexity) {
        this.complexity = complexity;
    }

    @Column(name = HibernateConsts.EXAM_STATUS)
    @Enumerated(EnumType.STRING)
    public McasStatus getExamStatus() {
        return examStatus;
    }

    public void setExamStatus(McasStatus examStatus) {
        this.examStatus = examStatus;
    }

    @Column(name = HibernateConsts.RAW_SCORE)
    public Double getRawScore() {
        return rawScore;
    }

    public void setRawScore(Double rawScore) {
        this.rawScore = rawScore;
    }

    @Column(name = HibernateConsts.SCALED_SCORE)
    public Double getScaledScore() {
        return scaledScore;
    }

    public void setScaledScore(Double scaledScore) {
        this.scaledScore = scaledScore;
    }

    @Column(name = HibernateConsts.PERF)
    @Enumerated(EnumType.STRING)
    public McasPerfLevel getPerformanceLevel() {
        return performanceLevel;
    }

    public void setPerformanceLevel(McasPerfLevel performanceLevel) {
        this.performanceLevel = performanceLevel;
    }

    @Column(name = HibernateConsts.PERF_2)
    @Enumerated(EnumType.STRING)
    public McasPerfLevel2 getPerformanceLevel2() {
        return performanceLevel2;
    }

    public void setPerformanceLevel2(McasPerfLevel2 performanceLevel2) {
        this.performanceLevel2 = performanceLevel2;
    }

    @Column(name = HibernateConsts.QUARTILE)
    public Long getQuartile() {
        return quartile;
    }

    public void setQuartile(Long quartile) {
        this.quartile = quartile;
    }

    @Override
    public int hashCode() {
        return Objects.hash(alternateExam, complexity, examStatus, rawScore, scaledScore,
                performanceLevel, performanceLevel2, quartile, id);
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
                && Objects.equals(this.id, other.id)
                && Objects.equals(this.performanceLevel, other.performanceLevel)
                && Objects.equals(this.performanceLevel2, other.performanceLevel2)
                && Objects.equals(this.quartile, other.quartile);
    }
}
