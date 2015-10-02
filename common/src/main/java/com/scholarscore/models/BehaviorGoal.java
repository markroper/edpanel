package com.scholarscore.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Goal type for goals that are behavior related
 * Created by cwallace on 9/20/2015.
 */
@Entity
@Table(name = HibernateConsts.GOAL_TABLE)
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
@DiscriminatorValue(value = "BEHAVIOR")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class BehaviorGoal extends Goal {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


    private Date startDate;
    private Date endDate;
    private BehaviorCategory behaviorCategory;

    public BehaviorGoal() {
        setGoalType(GoalType.BEHAVIOR);
    }

    public BehaviorGoal(BehaviorGoal goal) {
        super(goal);
        this.setGoalType(GoalType.BEHAVIOR);
        this.startDate = goal.startDate;
        this.endDate = goal.endDate;
        this.behaviorCategory = goal.behaviorCategory;

    }
    @Column(name = HibernateConsts.GOAL_START_DATE, columnDefinition="DATE")
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Column(name = HibernateConsts.GOAL_END_DATE, columnDefinition="DATE")
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }


    @Column(name = HibernateConsts.BEHAVIOR_GOAL_CATEGORY)
    @Enumerated(EnumType.STRING)
    public BehaviorCategory getBehaviorCategory() {
        return behaviorCategory;
    }

    public void setBehaviorCategory(BehaviorCategory behaviorCategory) {
        this.behaviorCategory = behaviorCategory;
    }

    @Override
    public void mergePropertiesIfNull(Goal mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);
        if (mergeFrom instanceof BehaviorGoal) {
            BehaviorGoal mergeFromBehavior = (BehaviorGoal)mergeFrom;
            if (null == this.startDate) {
                this.startDate = mergeFromBehavior.startDate;
            }
            if (null == endDate) {
                this.endDate = mergeFromBehavior.endDate;
            }
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BehaviorGoal that = (BehaviorGoal) o;
        return Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate) &&
                Objects.equals(behaviorCategory, that.behaviorCategory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), startDate, endDate, behaviorCategory);
    }

    @Override
    public String toString() {
        return
                "GOAL " + "\n"
                        + "Id  : " + getId() + "\n"
                        + "Name: " + getName() + "\n"
                        + "BehaviorCategory: " + getBehaviorCategory() +"\n"
                        + "DesiredValue: " + getDesiredValue() +"\n"
                        + "CalculatedValue: " + getCalculatedValue() + "\n"
                        + "Approved: " + getApproved() + "\n"
                        + "GoalType: " + getGoalType() + "\n"
                        + "Student: " + getStudent() + "\n"
                        + "Teacher: " + getTeacher() + "\n"
                        + "StartDate" + dateFormat.format(getStartDate()) + "\n"
                        + "EndDate" + dateFormat.format(getEndDate());
    }
}
