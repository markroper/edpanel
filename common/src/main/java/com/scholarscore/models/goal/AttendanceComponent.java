package com.scholarscore.models.goal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * Component for a complex goal that measures attendance in a particular class
 * Created by cwallace on 10/15/2015.
 */
public class AttendanceComponent extends GoalComponent implements CalculatableAttendance {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private Date startDate;
    private Date endDate;
    private Long parentId;

    public AttendanceComponent() {
        setComponentType(GoalType.ATTENDANCE);
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
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
                        + "StartDate: " + dateFormat.format(getStartDate()) + "\n"
                        + "EndDate: " + dateFormat.format(getEndDate());
    }
}
