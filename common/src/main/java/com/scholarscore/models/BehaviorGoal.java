package com.scholarscore.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

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

        return Objects.equals(this.startDate, that.startDate)
                && Objects.equals(this.endDate, that.endDate)
                && Objects.equals(this.behaviorCategory, that.behaviorCategory);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + startDate.hashCode();
        result = 31 * result + endDate.hashCode();
        result = 31 * result + behaviorCategory.hashCode();
        return result;
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
