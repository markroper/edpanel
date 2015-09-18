package com.scholarscore.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Created by cwallace on 9/17/2015.
 */

@Entity(name = HibernateConsts.GOAL_TABLE)
@Table(name = HibernateConsts.GOAL_TABLE)
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Goal extends ApiModel implements IApiModel<Goal> {
    private Long parent_id;
    private transient Student student;
    private transient Teacher teacher;
    private Long desired_value;

    private Long calculated_value;
    private Boolean approved;
    private GoalType goal_type;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = HibernateConsts.GOAL_ID)
    public Long getId() {
        return super.getId();
    }

    @Column(name = HibernateConsts.GOAL_APPROVED)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    @Column(name = HibernateConsts.PARENT_FK)
    public Long getParent_id() {
        return parent_id;
    }

    public void setParent_id(Long parent_id) {
        this.parent_id = parent_id;
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
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name=HibernateConsts.TEACHER_FK, nullable = true)
    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    @Column(name = HibernateConsts.DESIRED_GOAL_VALUE)
    public Long getDesired_value() {
        return desired_value;
    }

    public void setDesired_value(Long desired_value) {
        this.desired_value = desired_value;
    }

    @Transient
    public Long getCalculated_value() {
        return calculated_value;
    }

    public void setCalculated_value(Long calculated_value) {
        this.calculated_value = calculated_value;
    }

    @Column(name = HibernateConsts.GOAL_TYPE)
    @Enumerated(EnumType.STRING)
    public GoalType getGoal_type() {
        return goal_type;
    }

    public void setGoal_type(GoalType goal_type) {
        this.goal_type = goal_type;
    }

    @Override
    public void mergePropertiesIfNull(Goal mergeFrom) {

    }
}
