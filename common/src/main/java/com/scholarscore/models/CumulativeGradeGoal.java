package com.scholarscore.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.util.Objects;

/**
 * Goal representing a cumulative grade goal. The hope is that this can be generalized across a section, term or school year.
 * Fields are presently identical to AssignmentGoal, but the hope is that we could add some more specific functionality
 * within these goals. Example: a "percent done" for cumulative grade progress reflecting how determined the grade for each
 * class is.
 * Created by cwallace on 9/25/2015.
 */
@Entity
@Table(name = HibernateConsts.GOAL_TABLE)
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
@DiscriminatorValue(value = "CUMULATIVE_GRADE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class CumulativeGradeGoal extends Goal {

    private Long parentId;

    /**
     * This points to the student_section_grade_id
     * @return
     */
    @Column(name = HibernateConsts.PARENT_FK)
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public CumulativeGradeGoal() {
        setGoalType(GoalType.CUMULATIVE_GRADE);
    }

    public CumulativeGradeGoal(CumulativeGradeGoal goal) {
        super(goal);
        this.setGoalType(GoalType.CUMULATIVE_GRADE);
        this.parentId = goal.parentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CumulativeGradeGoal that = (CumulativeGradeGoal) o;
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

    /**
     * Each class's Builder holds a copy of each attribute that the parent POJO has. We build up these properties using
     * a pattern of with[Attribute](Attribute attribute) and return the same instance of the Builder so that one can easily
     * chain setting attributes together.
     */
    public class CumulativeGradeGoalBuilder extends GoalBuilder<CumulativeGradeGoal>{

        private Long parentId;

        public CumulativeGradeGoalBuilder withParentId(final Long parentId){
            this.parentId = parentId;
            return this;
        }

        public CumulativeGradeGoal build(){
            CumulativeGradeGoal goal = super.build();
            goal.setParentId(parentId);
            return goal;
        }

        @Override
        public CumulativeGradeGoal getInstance() {
            return new CumulativeGradeGoal();
        }
    }
}
