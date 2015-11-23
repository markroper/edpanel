package com.scholarscore.models.goal;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Component for a complex goal that measures attendance in a particular class
 * Created by cwallace on 10/15/2015.
 */
public class AttendanceComponent extends GoalComponent implements CalculatableAttendance {
    private LocalDate startDate;
    private LocalDate endDate;
    private Long parentId;

    public AttendanceComponent() {
        setComponentType(GoalType.ATTENDANCE);
    }

    @Override
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    @Override
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
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
        AttendanceComponent that = (AttendanceComponent) o;
        return Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate) &&
                Objects.equals(parentId, that.parentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), startDate, endDate, parentId);
    }

    @Override
    public String toString() {
        return
                "GOAL " + "\n"
                        + "ParentId: " + getParentId() +"\n"
                        + "ComponentType:" + getComponentType() + "\n"
                        + "Student: " + getStudent() + "\n"
                        + "Modifier: " + getModifier() + "\n"
                        + "StartDate: " + getStartDate() + "\n"
                        + "EndDate: " + getEndDate();
    }
}
