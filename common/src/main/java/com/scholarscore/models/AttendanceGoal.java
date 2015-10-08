package com.scholarscore.models;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.attendance.Attendance;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
/**
 * Goal type for goals that are based on performance on attendance over a range of dates
 * Created by cwallace on 9/21/2015.
 */
@Entity
@Table(name = HibernateConsts.GOAL_TABLE)
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
@DiscriminatorValue(value = "ATTENDANCE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class AttendanceGoal extends Goal {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private Long parentId;
    private Date startDate;
    private Date endDate;

    public AttendanceGoal() {
        setGoalType(GoalType.ATTENDANCE);
    }

    public AttendanceGoal(AttendanceGoal goal) {
        super(goal);
        this.setGoalType(GoalType.ATTENDANCE);
        this.parentId = goal.parentId;
        this.startDate = goal.startDate;
        this.endDate = goal.endDate;
    }

    @Column(name = HibernateConsts.PARENT_FK)
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @Column(name = HibernateConsts.GOAL_START_DATE, columnDefinition="DATE")
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Column(name = HibernateConsts.GOAL_END_DATE, columnDefinition="DATE")
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AttendanceGoal that = (AttendanceGoal) o;
        return Objects.equals(parentId, that.parentId) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate);
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
                        + "ParentId: " + getParentId() + "\n"
                        + "StartDate:" + dateFormat.format(getStartDate()) + "\n"
                        + "EndDate:" + dateFormat.format(getEndDate());
    }
}