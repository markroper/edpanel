package com.scholarscore.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 * Abstract class defining common fields and methods that exist across all goals.
 * Created by cwallace on 9/20/2015.
 */
@Entity(name = HibernateConsts.GOAL_TABLE)
@Table(name = HibernateConsts.GOAL_TABLE)
@DiscriminatorColumn(name=HibernateConsts.GOAL_TYPE, discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "goalType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = BehaviorGoal.class, name="BEHAVIOR"),
        @JsonSubTypes.Type(value = AssignmentGoal.class, name = "ASSIGNMENT"),
        @JsonSubTypes.Type(value = CumulativeGradeGoal.class, name = "CUMULATIVE_GRADE")
})
public abstract class Goal extends ApiModel implements IApiModel<Goal>, IGoal {

    private Student student;
    private Teacher teacher;
    private Float desiredValue;
    private Float calculatedValue;
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
    @JoinColumn(name=HibernateConsts.STUDENT_FK, nullable = true)
    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @OneToOne(optional = true)
    @JoinColumn(name=HibernateConsts.TEACHER_FK, nullable = true)
    public Teacher getTeacher() {
        return teacher;
    }

    public void setCalculatedValue(Float value) {
        this.calculatedValue = value;
    }

    @Transient
    public Float getCalculatedValue() {
        return calculatedValue;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    @Column(name = HibernateConsts.DESIRED_GOAL_VALUE)
    public Float getDesiredValue() {
        return desiredValue;
    }

    public void setDesiredValue(Float desiredValue) {
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
        return Objects.equals(student, goal.student) &&
                Objects.equals(teacher, goal.teacher) &&
                Objects.equals(desiredValue, goal.desiredValue) &&
                Objects.equals(calculatedValue, goal.calculatedValue) &&
                Objects.equals(approved, goal.approved) &&
                Objects.equals(goalType, goal.goalType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), student, teacher, desiredValue, calculatedValue, approved, goalType);
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
