package com.scholarscore.models.behavior;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.IApiModel;
import com.scholarscore.models.user.Student;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Created by markroper on 4/2/16.
 */
@Entity(name = HibernateConsts.BEHAVIOR_SCORE_TABLE)
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BehaviorScore implements IApiModel<BehaviorScore> {
    protected Long id;
    protected LocalDate date;
    protected Long currentWeeklyScore;
    protected Long currentAnnualScore;
    protected Student student;

    public BehaviorScore() { }

    public BehaviorScore(BehaviorScore score) {
        this.id = score.id;
        this.date = score.date;
        this.currentAnnualScore = score.currentAnnualScore;
        this.currentWeeklyScore = score.currentWeeklyScore;
        if(null != score.student) {
            this.student = new Student(score.student);
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = HibernateConsts.BEHAVIOR_SCORE_ID)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = HibernateConsts.BEHAVIOR_SCORE_DATE)
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Column(name = HibernateConsts.BEHAVIOR_SCORE_WEEKLY_SCORE)
    public Long getCurrentWeeklyScore() {
        return currentWeeklyScore;
    }

    public void setCurrentWeeklyScore(Long currentWeeklyScore) {
        this.currentWeeklyScore = currentWeeklyScore;
    }

    @Column(name = HibernateConsts.BEHAVIOR_SCORE_ANNUAL_SCORE)
    public Long getCurrentAnnualScore() {
        return currentAnnualScore;
    }

    public void setCurrentAnnualScore(Long currentAnnualScore) {
        this.currentAnnualScore = currentAnnualScore;
    }

    @ManyToOne
    @JoinColumn(name=HibernateConsts.STUDENT_FK)
    @Fetch(FetchMode.JOIN)
    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, currentWeeklyScore, currentAnnualScore, student);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final BehaviorScore other = (BehaviorScore) obj;
        return Objects.equals(this.id, other.id)
                && Objects.equals(this.date, other.date)
                && Objects.equals(this.currentWeeklyScore, other.currentWeeklyScore)
                && Objects.equals(this.student, other.student)
                && Objects.equals(this.currentAnnualScore, other.currentAnnualScore);
    }

    @Override
    public String toString() {
        return "BehaviorScore{" +
                "id=" + id +
                ", date=" + date +
                ", currentWeeklyScore=" + currentWeeklyScore +
                ", student=" + student +
                ", currentAnnualScore=" + currentAnnualScore +
                '}';
    }

    @Override
    public void mergePropertiesIfNull(BehaviorScore mergeFrom) {
        if (null == this.id) {
            this.id = mergeFrom.id;
        }
        if (null == this.date) {
            this.date = mergeFrom.date;
        }
        if (null == this.currentAnnualScore) {
            this.currentAnnualScore = mergeFrom.currentAnnualScore;
        }
        if (null == this.currentWeeklyScore) {
            this.currentWeeklyScore = mergeFrom.currentWeeklyScore;
        }
        if (null == this.student && null != mergeFrom.student) {
            this.student = mergeFrom.student;
        }
    }
}
