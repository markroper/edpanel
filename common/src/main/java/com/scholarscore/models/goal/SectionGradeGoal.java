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
 * Goal representing a cumulative grade goal. The hope is that this can be generalized across a section, term or school year.
 * Fields are presently identical to AssignmentGoal, but the hope is that we could add some more specific functionality
 * within these goals. Example: a "percent done" for cumulative grade progress reflecting how determined the grade for each
 * class is.
 * Created by cwallace on 9/25/2015.
 */
@Entity
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
@DiscriminatorValue(value = "SECTION_GRADE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class SectionGradeGoal extends Goal implements CalculatableSection {

    private Section section;

    @OneToOne(optional = true)
    @JoinColumn(name=HibernateConsts.SECTION_FK, nullable = true)
    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    /**
     * This points to the section_id NOT the student_section_id
     * @return
     */


    public SectionGradeGoal() {
        setGoalType(GoalType.SECTION_GRADE);
    }

    public SectionGradeGoal(SectionGradeGoal goal) {
        super(goal);
        this.setGoalType(GoalType.SECTION_GRADE);
        this.section = goal.section;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SectionGradeGoal that = (SectionGradeGoal) o;
            return Objects.equals(section, that.section);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), section);
    }

    @Override
    public void mergePropertiesIfNull(Goal mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);
        if (mergeFrom instanceof SectionGradeGoal) {
            SectionGradeGoal mergeFromBehavior = (SectionGradeGoal)mergeFrom;
            if (null == this.section) {
                this.section = mergeFromBehavior.section;
            }
        }

    }

    @Override
    public void setGoalType(GoalType goalType) {
        // don't allow the parent class goalType to be set to anything besides BEHAVIOR
        super.setGoalType(GoalType.SECTION_GRADE);
    }
    
    @Override
    public String toString() {
        return
                "GOAL (super:" + super.toString() + ")" + "\n"
                        + "Section: " + getSection();
    }

    /**
     * Each class's Builder holds a copy of each attribute that the parent POJO has. We build up these properties using
     * a pattern of with[Attribute](Attribute attribute) and return the same instance of the Builder so that one can easily
     * chain setting attributes together.
     */
    public static class SectionGradeGoalBuilder extends GoalBuilder<SectionGradeGoalBuilder, SectionGradeGoal>{

        private Section section;

        public SectionGradeGoalBuilder withSection(final Section section){
            this.section = section;
            return this;
        }

        public SectionGradeGoal build(){
            SectionGradeGoal goal = super.build();
            goal.setGoalType(GoalType.SECTION_GRADE);
            goal.setSection(section);
            return goal;
        }

        @Override
        protected SectionGradeGoalBuilder me() {
            return this;
        }

        @Override
        public SectionGradeGoal getInstance() {
            return new SectionGradeGoal();
        }
    }

}


