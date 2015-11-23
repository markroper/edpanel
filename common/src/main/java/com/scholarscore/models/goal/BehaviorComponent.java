package com.scholarscore.models.goal;

import com.scholarscore.models.BehaviorCategory;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Component for complex goal that is for behavior goal.
 * Created by cwallace on 10/14/2015.
 */
public class BehaviorComponent extends GoalComponent implements CalculatableBehavior {
    private BehaviorCategory behaviorCategory;
    private LocalDate startDate;
    private LocalDate endDate;

    public  BehaviorComponent() {
        setComponentType(GoalType.BEHAVIOR);
    }

    public BehaviorCategory getBehaviorCategory() {
        return behaviorCategory;
    }

    public void setBehaviorCategory(BehaviorCategory behaviorCategory) {
        this.behaviorCategory = behaviorCategory;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BehaviorComponent that = (BehaviorComponent) o;
        return Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate) &&
                Objects.equals(behaviorCategory, that.behaviorCategory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), startDate, endDate, behaviorCategory);
    }

    @Override
    public String toString() {
        return
                "GOAL " + "\n"
                        + "BehaviorCategory: " + getBehaviorCategory() +"\n"
                        + "ComponentType:" + getComponentType() + "\n"
                        + "Student: " + getStudent() + "\n"
                        + "Modifier: " + getModifier() + "\n"
                        + "StartDate: " + getStartDate() + "\n"
                        + "EndDate: " + getEndDate();
    }
}
