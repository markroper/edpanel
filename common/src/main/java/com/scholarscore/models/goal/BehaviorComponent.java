package com.scholarscore.models.goal;

import com.scholarscore.models.BehaviorCategory;
import com.scholarscore.models.user.Student;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * Created by cwallace on 10/14/2015.
 */
public class BehaviorComponent implements GoalComponent, CalculatableBehavior {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


    private BehaviorCategory behaviorCategory;
    private Date startDate;
    private Date endDate;
    private Student student;

    public BehaviorCategory getBehaviorCategory() {
        return behaviorCategory;
    }

    public void setBehaviorCategory(BehaviorCategory behaviorCategory) {
        this.behaviorCategory = behaviorCategory;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public Student getStudent() {
        return student;
    }

    @Override
    public void setStudent(Student student) {
        this.student = student;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BehaviorComponent that = (BehaviorComponent) o;
        return Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate) &&
                Objects.equals(behaviorCategory, that.behaviorCategory) &&
                Objects.equals(student, that.student);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), startDate, endDate, behaviorCategory, student);
    }

    @Override
    public String toString() {
        return
                "GOAL " + "\n"
                        + "BehaviorCategory: " + getBehaviorCategory() +"\n"
                        + "Student: " + getStudent() + "\n"
                        + "StartDate: " + dateFormat.format(getStartDate()) + "\n"
                        + "EndDate: " + dateFormat.format(getEndDate());
    }
}
