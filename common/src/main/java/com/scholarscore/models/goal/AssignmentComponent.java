package com.scholarscore.models.goal;
import java.util.Objects;

/**
 * Component for complex goal that is for assignments
 * Created by cwallace on 10/15/2015.
 */
public class AssignmentComponent extends GoalComponent implements CalculatableAssignment {

    private Long parentId;

    public AssignmentComponent() {
        setComponentType(GoalType.ASSIGNMENT);
    }


    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getParentId() {
        return parentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AssignmentComponent that = (AssignmentComponent) o;
        return  Objects.equals(parentId, that.parentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), parentId);
    }

    @Override
    public String toString() {
        return
                "GOAL " + "\n"
                        + "ParentId: " + getParentId() +"\n"
                        + "ComponentType:" + getComponentType() + "\n"
                        + "Student: " + getStudent() + "\n"
                        + "Modifier: " + getModifier();
    }
}
