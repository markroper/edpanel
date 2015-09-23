package com.scholarscore.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

/**
 * Abstract class defining common fields and methods that exist across all goals.
 * Created by cwallace on 9/20/2015.
 */
@Entity(name = HibernateConsts.GOAL_TABLE)
@Table(name = HibernateConsts.GOAL_TABLE)
@DiscriminatorColumn(name="goal_type", discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "goalType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = BehaviorGoal.class, name="BEHAVIOR"),
        @JsonSubTypes.Type(value = AssignmentGoal.class, name = "ASSIGNMENT")
})
public abstract class Goal extends ApiModel implements IApiModel<Goal>, IGoal {

    private transient Student student;
    private transient Teacher teacher;
    private Long desiredValue;
    private Long calculatedValue;
    private Boolean approved;
    private GoalType goalType;

    public Goal() {
        super();
    }

    public Goal(Goal goal) {
        super(goal);
        this.student = goal.student;
        this.teacher = goal.teacher;
        this.desiredValue = goal.desiredValue;
        this.calculatedValue = goal.calculatedValue;
        this.approved = goal.approved;
        this.goalType = goal.goalType;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = HibernateConsts.GOAL_ID)
    public Long getId() {
        return super.getId();
    }

    @Override
    @Column(name = HibernateConsts.GOAL_NAME)
    public String getName() {
        return super.getName();
    }


    @Column(name = HibernateConsts.GOAL_APPROVED)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }



    @OneToOne(optional = true)
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinColumn(name=HibernateConsts.STUDENT_FK, nullable = true)
    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @OneToOne(optional = true)
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinColumn(name=HibernateConsts.TEACHER_FK, nullable = true)
    public Teacher getTeacher() {
        return teacher;
    }

    public void setCalculatedValue(Long value) {
        this.calculatedValue = value;
    }

    @Transient
    public Long getCalculatedValue() {
        return calculatedValue;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    @Column(name = HibernateConsts.DESIRED_GOAL_VALUE)
    public Long getDesiredValue() {
        return desiredValue;
    }

    public void setDesiredValue(Long desiredValue) {
        this.desiredValue = desiredValue;
    }

    public void setGoalType(GoalType goalType) {
        this.goalType = goalType;
    }

    @Column(name = HibernateConsts.GOAL_TYPE, insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    public GoalType getGoalType() {
        return goalType;
    }




    @Override
    public void mergePropertiesIfNull(Goal mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);
        if (null == desiredValue) {
            this.desiredValue = mergeFrom.desiredValue;
        }
        if (null == calculatedValue) {
            this.calculatedValue = mergeFrom.calculatedValue;
        }
        if (null == approved) {
            this.approved = mergeFrom.approved;
        }
        if (null == goalType) {
            this.goalType = mergeFrom.goalType;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Goal goal = (Goal) o;

        if (!student.equals(goal.student)) return false;
        if (!teacher.equals(goal.teacher)) return false;
        if (!desiredValue.equals(goal.desiredValue)) return false;
        if (calculatedValue != null ? !calculatedValue.equals(goal.calculatedValue) : goal.calculatedValue != null)
            return false;
        if (!approved.equals(goal.approved)) return false;
        return goalType == goal.goalType;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + student.hashCode();
        result = 31 * result + teacher.hashCode();
        result = 31 * result + desiredValue.hashCode();
        result = 31 * result + (calculatedValue != null ? calculatedValue.hashCode() : 0);
        result = 31 * result + approved.hashCode();
        result = 31 * result + goalType.hashCode();
        return result;
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
                        + "Teacher: " + getTeacher() + "\n";
    }
}
