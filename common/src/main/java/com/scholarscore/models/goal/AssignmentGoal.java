package com.scholarscore.models.goal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.Section;
import com.scholarscore.models.assignment.StudentAssignment;

import javax.persistence.Column;
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

    //In the event the assignment is not in powerschool yet, let the student enter a string name for what it is
    private String assignmentText;

    //In the event that an assignment has not yet been created, we need to still specify what section it
    //should be on so we can find it later.
    private Section section;

    @Column(name = HibernateConsts.GOAL_ASSIGNMENT_NAME)
    public String getAssignmentText() {
        return assignmentText;
    }

    public void setAssignmentText(String assignmentText) {
        this.assignmentText = assignmentText;
    }

    @OneToOne(optional = true)
    @JoinColumn(name=HibernateConsts.SECTION_FK, nullable = true)
    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

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
        super();
    }

    public AssignmentGoal(AssignmentGoal goal) {
        super(goal);
        this.studentAssignment = goal.studentAssignment;
        this.section = goal.section;
        this.assignmentText = goal.assignmentText;
    }

    @Override
    public void mergePropertiesIfNull(Goal mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);
        if (mergeFrom instanceof AssignmentGoal) {
            AssignmentGoal mergeFromAssignment = (AssignmentGoal)mergeFrom;
            if (null == this.studentAssignment) {
                this.studentAssignment = mergeFromAssignment.studentAssignment;
            }
            if (null == this.section) {
                this.section = mergeFromAssignment.section;
            }

            if (null == this.assignmentText) {
                this.assignmentText = mergeFromAssignment.assignmentText;
            }
        }
    }


    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(studentAssignment, assignmentText, section);
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
        final AssignmentGoal other = (AssignmentGoal) obj;
        return Objects.equals(this.studentAssignment, other.studentAssignment)
                && Objects.equals(this.assignmentText, other.assignmentText)
                && Objects.equals(this.section, other.section);
    }

    @Override
    protected GoalType goalType() {
        return GoalType.ASSIGNMENT;
    }

    @Override
    public String toString() {
        return
                "GOAL super(" + super.toString() +")" + "\n"
                        + "StudentAssignment:" + getStudentAssignment() + "\n"
                        + "Section:" + getSection() + "\n"
                        + "AssignmentText:" + getAssignmentText() + "\n";
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
