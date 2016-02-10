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
    private BehaviorCategory behaviorCategory;

    public BehaviorGoal() {
        super();
        setGoalType(GoalType.BEHAVIOR);
    }

    public BehaviorGoal(BehaviorGoal goal) {
        super(goal);
        this.setGoalType(GoalType.BEHAVIOR);
        this.behaviorCategory = goal.behaviorCategory;

    }

    
    @Override
    public void setGoalType(GoalType goalType) {
        // don't allow the parent class goalType to be set to anything besides BEHAVIOR
        super.setGoalType(GoalType.BEHAVIOR);
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
        return Objects.equals(behaviorCategory, that.behaviorCategory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), behaviorCategory);
    }

    @Override
    public String toString() {
        return
                "GOAL (super:" + super.toString() + ")" + "\n"
                        + "BehaviorCategory: " + getBehaviorCategory();
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
