package com.scholarscore.models.goal;

import java.util.Objects;

/**
 * Component with a constant value. Used in case you want a student
 * to start with an arbitrary amount of weekly points
 * Created by cwallace on 10/15/2015.
 */
public class CumulativeGradeComponent extends GoalComponent implements CalculatableCumulative{

    private Long parentId;

    public CumulativeGradeComponent() {
        setComponentType(GoalType.CUMULATIVE_GRADE);
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CumulativeGradeComponent that = (CumulativeGradeComponent) o;
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
                        + "ParentId: " + getParentId() +"\n"
                        + "ComponentType:" + getComponentType() + "\n"
                        + "Student: " + getStudent() + "\n"
                        + "Modifier: " + getModifier();
    }
}
