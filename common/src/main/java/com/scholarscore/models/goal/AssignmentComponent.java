package com.scholarscore.models.goal;
import com.scholarscore.models.assignment.StudentAssignment;

import java.util.Objects;

/**
 * Component for complex goal that is for assignments
 * Created by cwallace on 10/15/2015.
 */
public class AssignmentComponent extends GoalComponent implements CalculatableAssignment {

    private StudentAssignment studentAssignment;

    public AssignmentComponent() {
        setComponentType(GoalType.ASSIGNMENT);
    }

    @Override
    public StudentAssignment getStudentAssignment() {
        return studentAssignment;
    }

    @Override
    public void setStudentAssignment(StudentAssignment studentAssignment) {
        this.studentAssignment = studentAssignment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AssignmentComponent that = (AssignmentComponent) o;
        return  Objects.equals(studentAssignment, that.studentAssignment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), studentAssignment);
    }

    @Override
    public String toString() {
        return
                "GOAL " + "\n"
                        + "StudentAssignment: " + getStudentAssignment() +"\n"
                        + "ComponentType:" + getComponentType() + "\n"
                        + "Student: " + getStudent() + "\n"
                        + "Modifier: " + getModifier();
    }
}
