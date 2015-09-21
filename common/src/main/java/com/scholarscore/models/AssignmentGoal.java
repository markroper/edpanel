package com.scholarscore.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;

/**
 * Created by cwallace on 9/21/2015.
 */
@Entity
@Table(name = HibernateConsts.GOAL_TABLE)
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
@DiscriminatorValue(value = "ASSIGNMENT")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class AssignmentGoal extends Goal {

    private Long parentId;

    @Column(name = HibernateConsts.PARENT_FK)
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public AssignmentGoal() {
        setGoalType(GoalType.ASSIGNMENT);
    }

    public AssignmentGoal(AssignmentGoal goal) {
        super(goal);
        this.setGoalType(GoalType.ASSIGNMENT);
        this.parentId = goal.parentId;
    }
}
