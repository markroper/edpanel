package com.scholarscore.models.goal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.HibernateConsts;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.util.Objects;

/**
 * Created by cwallace on 2/25/16.
 */
@javax.persistence.Entity
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
@DiscriminatorValue(value = "OPEN")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class OpenGoal extends Goal {

    private String message;

    public OpenGoal() {
        super();
        setGoalType(GoalType.OPEN);
    }

    public OpenGoal(OpenGoal goal) {
        super(goal);
        this.setGoalType(GoalType.OPEN);
        this.message = goal.message;

    }

    @Column(name = HibernateConsts.GOAL_MESSAGE, columnDefinition = "blob")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void setGoalType(GoalType goalType) {
        // don't allow the parent class goalType to be set to anything besides OPEN
        super.setGoalType(GoalType.OPEN);
    }

    @Override
    public void mergePropertiesIfNull(Goal mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);
        if (mergeFrom instanceof OpenGoal) {
            OpenGoal mergeFromOpen = (OpenGoal) mergeFrom;
            if (null == message) {
                this.message = mergeFromOpen.message;
            }
        }

    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(message);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final OpenGoal other = (OpenGoal) obj;
        return Objects.equals(this.message, other.message);
    }

    @Override
    public String toString() {
        return
                "GOAL (super:" + super.toString() + ")" + "\n"
                        + "Message: " + getMessage();
    }
}
