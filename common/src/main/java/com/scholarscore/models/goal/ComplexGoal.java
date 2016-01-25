package com.scholarscore.models.goal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.HibernateConsts;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.util.Objects;

/**
 * Goal that can be made of an arbitrary number of components that
 * each have their own modifier allowing for variable waiting
 * Created by cwallace on 10/14/2015.
 */

@Entity
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
@DiscriminatorValue(value = "COMPLEX")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class ComplexGoal extends Goal {

    private GoalAggregate goalAggregate;

    public ComplexGoal() {
        this.setGoalType(GoalType.COMPLEX);
    }

    public ComplexGoal(ComplexGoal goal) {
        super(goal);
        this.goalAggregate = goal.goalAggregate;
    }


    @Column(name = HibernateConsts.GOAL_AGGREGATE,columnDefinition="blob")
    public GoalAggregate getGoalAggregate() {
        return goalAggregate;
    }

    public void setGoalAggregate(GoalAggregate goalAggregate) {
        this.goalAggregate = goalAggregate;
    }

    @Override
    public void mergePropertiesIfNull(Goal mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);
        if (mergeFrom instanceof ComplexGoal) {
            ComplexGoal mergeFromGoal = (ComplexGoal)mergeFrom;
            if (null == this.goalAggregate) {
                this.goalAggregate = mergeFromGoal.goalAggregate;
            }
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ComplexGoal that = (ComplexGoal) o;
        return Objects.equals(goalAggregate, that.goalAggregate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), goalAggregate);
    }

    @Override
    public String toString() {
        return
                "GOAL " + "\n"
                        + "Id  : " + getId() + "\n"
                        + "Name: " + getName() + "\n"
                        + "DesiredValue: " + getDesiredValue() +"\n"
                        + "CalculatedValue: " + getCalculatedValue() + "\n"
                        + "Approved: " + getApproved() + "\n"
                        + "GoalType: " + getGoalType() + "\n"
                        + "Student: " + getStudent() + "\n"
                        + "Teacher: " + getStaff() + "\n"
                        + "GoalAggregate: " + getGoalAggregate();
    }
}
