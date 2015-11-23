package com.scholarscore.models.goal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.BehaviorCategory;
import com.scholarscore.models.HibernateConsts;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Goal type for goals that are behavior related
 * Created by cwallace on 9/20/2015.
 */
@javax.persistence.Entity
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
@DiscriminatorValue(value = "BEHAVIOR")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class BehaviorGoal extends Goal implements CalculatableBehavior {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private LocalDate startDate;
    private LocalDate endDate;
    private BehaviorCategory behaviorCategory;

    public BehaviorGoal() {
        super();
        setGoalType(GoalType.BEHAVIOR);
    }

    public BehaviorGoal(BehaviorGoal goal) {
        super(goal);
        this.setGoalType(GoalType.BEHAVIOR);
        this.startDate = goal.startDate;
        this.endDate = goal.endDate;
        this.behaviorCategory = goal.behaviorCategory;

    }

    @Column(name = HibernateConsts.GOAL_START_DATE, columnDefinition="DATE")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    @Column(name = HibernateConsts.GOAL_END_DATE, columnDefinition = "DATE")
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }


    @Column(name = HibernateConsts.BEHAVIOR_GOAL_CATEGORY)
    @Enumerated(EnumType.STRING)
    public BehaviorCategory getBehaviorCategory() {
        return behaviorCategory;
    }

    public void setBehaviorCategory(BehaviorCategory behaviorCategory) {
        this.behaviorCategory = behaviorCategory;
    }

    @Override
    public void mergePropertiesIfNull(Goal mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);
        if (mergeFrom instanceof BehaviorGoal) {
            BehaviorGoal mergeFromBehavior = (BehaviorGoal) mergeFrom;
            if (null == this.startDate) {
                this.startDate = mergeFromBehavior.startDate;
            }
            if (null == endDate) {
                this.endDate = mergeFromBehavior.endDate;
            }
            if (null == behaviorCategory) {
                this.behaviorCategory = mergeFromBehavior.behaviorCategory;
            }
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BehaviorGoal that = (BehaviorGoal) o;
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
                        + "Id  : " + getId() + "\n"
                        + "Name: " + getName() + "\n"
                        + "BehaviorCategory: " + getBehaviorCategory() + "\n"
                        + "DesiredValue: " + getDesiredValue() + "\n"
                        + "CalculatedValue: " + getCalculatedValue() + "\n"
                        + "Approved: " + getApproved() + "\n"
                        + "GoalType: " + getGoalType() + "\n"
                        + "Student: " + getStudent() + "\n"
                        + "Teacher: " + getTeacher() + "\n"
                        + "StartDate: " + getStartDate() + "\n"
                        + "EndDate: " + getEndDate();
    }

    /**
     * Each class's Builder holds a copy of each attribute that the parent POJO has. We build up these properties using
     * a pattern of with[Attribute](Attribute attribute) and return the same instance of the Builder so that one can easily
     * chain setting attributes together.
     */
    public static class BehaviorGoalBuilder extends GoalBuilder<BehaviorGoalBuilder, BehaviorGoal> {

        private LocalDate startDate;
        private LocalDate endDate;
        private BehaviorCategory behaviorCategory;

        public BehaviorGoalBuilder withStartDate(final LocalDate startDate){
            this.startDate = startDate;
            return this;
        }

        public BehaviorGoalBuilder withEndDate(final LocalDate endDate){
            this.endDate = endDate;
            return this;
        }

        public BehaviorGoalBuilder withBehaviorCategory(final BehaviorCategory behaviorCategory){
            this.behaviorCategory = behaviorCategory;
            return this;
        }

        public BehaviorGoal build(){
            BehaviorGoal goal = super.build();
            goal.setGoalType(GoalType.BEHAVIOR);
            goal.setStartDate(startDate);
            goal.setEndDate(endDate);
            goal.setBehaviorCategory(behaviorCategory);
            return goal;
        }

        @Override
        protected BehaviorGoalBuilder me() {
            return this;
        }

        @Override
        public BehaviorGoal getInstance() {
            return new BehaviorGoal();
        }
    }
}
