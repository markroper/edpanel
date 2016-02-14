package com.scholarscore.models.goal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.assignment.StudentAssignment;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.util.Objects;

/**
 * Goal type for goals that are based on performance on a single assignment
 * Created by cwallace on 9/21/2015.
 */
@Entity
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
@DiscriminatorValue(value = "ASSIGNMENT")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class AssignmentGoal extends Goal implements CalculatableAssignment {

    private StudentAssignment studentAssignment;

    @OneToOne(optional = true)
    @JoinColumn(name=HibernateConsts.STUDENT_ASSIGNMENT_FK, nullable = true)
    public StudentAssignment getStudentAssignment() {
        return studentAssignment;
    }

    /**
     * This should be the assignment the goal is associated with
     * @param assignment
     */
    public void setStudentAssignment(StudentAssignment assignment) {
        this.studentAssignment = assignment;
    }

    public AssignmentGoal() {
        setGoalType(GoalType.ASSIGNMENT);
    }

    public AssignmentGoal(AssignmentGoal goal) {
        super(goal);
        this.setGoalType(GoalType.ASSIGNMENT);
        this.studentAssignment = goal.studentAssignment;
    }

    @Override
    public void mergePropertiesIfNull(Goal mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);
        if (mergeFrom instanceof AssignmentGoal) {
            AssignmentGoal mergeFromBehavior = (AssignmentGoal)mergeFrom;
            if (null == this.studentAssignment) {
                this.studentAssignment = mergeFromBehavior.studentAssignment;
            }
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AssignmentGoal that = (AssignmentGoal) o;
        return Objects.equals(studentAssignment, that.studentAssignment);
    }
    
    @Override
    public void setGoalType(GoalType goalType) {
        super.setGoalType(GoalType.ASSIGNMENT);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), studentAssignment);
    }

    @Override
    public String toString() {
        return
                "GOAL super(" + super.toString() +")" + "\n"
                        + "StudentAssignment:" + getStudentAssignment() + "\n";
    }

    /**
     * Each class's Builder holds a copy of each attribute that the parent POJO has. We build up these properties using
     * a pattern of with[Attribute](Attribute attribute) and return the same instance of the Builder so that one can easily
     * chain setting attributes together.
     */
    public static class AssignmentGoalBuilder extends GoalBuilder<AssignmentGoalBuilder, AssignmentGoal>{

        private StudentAssignment studentAssignment;

        public AssignmentGoalBuilder withStudentAsssignment(final StudentAssignment studentAsssignment){
            this.studentAssignment = studentAsssignment;
            return this;
        }

        public AssignmentGoal build(){
            AssignmentGoal goal = super.build();
            goal.setGoalType(GoalType.ASSIGNMENT);
            goal.setStudentAssignment(studentAssignment);
            return goal;
        }

        @Override
        protected AssignmentGoalBuilder me() {
            return this;
        }

        @Override
        public AssignmentGoal getInstance() {
            return new AssignmentGoal();
        }
    }
}
