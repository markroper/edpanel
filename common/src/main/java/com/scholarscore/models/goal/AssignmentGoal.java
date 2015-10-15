package com.scholarscore.models.goal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.HibernateConsts;

import javax.persistence.*;
import java.util.Objects;

/**
 * Goal type for goals that are based on performance on a single assignment
 * Created by cwallace on 9/21/2015.
 */
@Entity
@Table(name = HibernateConsts.GOAL_TABLE)
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
@DiscriminatorValue(value = "ASSIGNMENT")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class AssignmentGoal extends Goal implements CalculatableAssignment {

    private Long parentId;

    @Column(name = HibernateConsts.PARENT_FK)
    public Long getParentId() {
        return parentId;
    }

    /**
     * This should identify the composite ID of the student assignmnet we wish to set a goal for
     * @param parentId
     */
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AssignmentGoal that = (AssignmentGoal) o;
        return Objects.equals(parentId, that.parentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), parentId);
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
                        + "Teacher: " + getTeacher() + "\n"
                        + "ParentId: " + getParentId();
    }
}
