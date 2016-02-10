package com.scholarscore.models.goal;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.Section;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.util.Objects;
/**
 * Goal type for goals that are based on performance on attendance over a range of dates
 * Created by cwallace on 9/21/2015.
 */
@Entity
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
@DiscriminatorValue(value = "ATTENDANCE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class AttendanceGoal extends Goal implements CalculatableAttendance{
    //References the sectionId of the section we have an attendance goal in
    private Section section;

    public AttendanceGoal() {
        setGoalType(GoalType.ATTENDANCE);
    }

    public AttendanceGoal(AttendanceGoal goal) {
        super(goal);
        this.setGoalType(GoalType.ATTENDANCE);
        this.section = goal.section;
    }

    @OneToOne(optional = true)
    @JoinColumn(name=HibernateConsts.SECTION_FK, nullable = true)
    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }


    @Override
    public void mergePropertiesIfNull(Goal mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);
        if (mergeFrom instanceof AttendanceGoal) {
            AttendanceGoal mergeFromBehavior = (AttendanceGoal)mergeFrom;
            if (null == section) {
                this.section = mergeFromBehavior.section;
            }
        }

    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AttendanceGoal that = (AttendanceGoal) o;
        return Objects.equals(section, that.section);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), section);
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
                        + "Teacher: " + getStaff() + "\n"
                        + "Section: " + getSection() + "\n"
                        + "StartDate: " + getStartDate() + "\n"
                        + "EndDate: " + getEndDate();
    }
}