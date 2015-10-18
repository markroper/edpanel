package com.scholarscore.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.Teacher;

import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import java.util.Date;
import java.util.Objects;

/**
 * Represents a student behavior event such as a merit, demerit, detention, suspension and so on.
 * 
 * User: jordan
 * Date: 8/8/15
 * Time: 3:21 PM
 */
@Entity(name = HibernateConsts.BEHAVIOR_TABLE)
@Table(name = HibernateConsts.BEHAVIOR_TABLE)
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Behavior extends ApiModel implements IApiModel<Behavior> {
    
    // "name" in parent class maps to 'behavior'
    @Size(min=1, max=64)
    private String remoteSystem;
    @Size(min=1, max=64)
    private String remoteBehaviorId;
    @Size(min=1, max=256)
    private String remoteStudentId;    // currently always deanslist DLSAID
    private Date behaviorDate;
    private BehaviorCategory behaviorCategory;
    @Size(min=1, max=256)
    private String pointValue;
    @Size(min=1, max=256)
    private String roster; // the class the behavior event occurred within
    private Student student;
    private Teacher teacher;
    
    public Behavior() { }
    
    public Behavior(Behavior behavior) {
        super(behavior);
        // "name" in parent class maps to 'behavior'
        this.remoteSystem = behavior.remoteSystem;
        this.remoteBehaviorId = behavior.remoteBehaviorId;
        this.remoteStudentId = behavior.remoteStudentId;    // currently always deanslist DLSAID
        this.behaviorDate = behavior.behaviorDate;
        this.behaviorCategory = behavior.behaviorCategory;
        this.pointValue = behavior.pointValue;
        this.roster = behavior.roster; // the class the behavior event occurred within

        this.student = behavior.student;
        this.teacher = behavior.teacher;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = HibernateConsts.BEHAVIOR_ID)
    public Long getId() {
        return super.getId();
    }

    @Override
    @Column(name = HibernateConsts.BEHAVIOR_NAME)
    public String getName() {
        return super.getName();
    }


    @Override
    public void mergePropertiesIfNull(Behavior mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);
        if (null == this.remoteStudentId) {
            this.remoteStudentId = mergeFrom.remoteStudentId;
        }
        if (null == behaviorDate) {
            this.behaviorDate = mergeFrom.behaviorDate;
        }
        if (null == behaviorCategory) {
            this.behaviorCategory = mergeFrom.behaviorCategory;
        }
        if (null == pointValue) {
            this.pointValue = mergeFrom.pointValue;
        }
        if (null == roster) {
            this.roster = mergeFrom.roster;
        }
    }

    @Column(name = HibernateConsts.BEHAVIOR_REMOTE_STUDENT_ID)
    public String getRemoteStudentId() {
        return remoteStudentId;
    }

    public void setRemoteStudentId(String remoteStudentId) {
        this.remoteStudentId = remoteStudentId;
    }

    @Column(name = HibernateConsts.BEHAVIOR_REMOTE_BEHAVIOR_ID)
    public String getRemoteBehaviorId() {
        return remoteBehaviorId;
    }

    public void setRemoteBehaviorId(String remoteBehaviorId) {
        this.remoteBehaviorId = remoteBehaviorId;
    }

    @Column(name = HibernateConsts.BEHAVIOR_REMOTE_SYSTEM)
    public String getRemoteSystem() {
        return remoteSystem;
    }

    public void setRemoteSystem(String remoteSystem) {
        this.remoteSystem = remoteSystem;
    }
    
    @Column(name = HibernateConsts.BEHAVIOR_DATE)
    public Date getBehaviorDate() {
        return behaviorDate;
    }

    public void setBehaviorDate(Date behaviorDate) {
        this.behaviorDate = behaviorDate;
    }

    @Column(name = HibernateConsts.BEHAVIOR_CATEGORY)
    @Enumerated(EnumType.STRING)
    public BehaviorCategory getBehaviorCategory() {
        return behaviorCategory;
    }

    public void setBehaviorCategory(BehaviorCategory behaviorCategory) {
        this.behaviorCategory = behaviorCategory;
    }

    @Column(name = HibernateConsts.BEHAVIOR_POINT_VALUE)
    public String getPointValue() {
        return pointValue;
    }

    public void setPointValue(String pointValue) {
        this.pointValue = pointValue;
    }

    @Column(name = HibernateConsts.BEHAVIOR_ROSTER)
    public String getRoster() {
        return roster;
    }

    public void setRoster(String roster) {
        this.roster = roster;
    }

    @OneToOne(optional = true)
    @Cascade(CascadeType.SAVE_UPDATE)
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

    @Override
    public boolean equals(Object obj) {
        if(!super.equals(obj)) {
            return false;
        }
        final Behavior other = (Behavior) obj;
        return Objects.equals(this.remoteSystem, other.remoteSystem)
                && Objects.equals(this.remoteBehaviorId, other.remoteBehaviorId)
                && Objects.equals(this.remoteStudentId, other.remoteStudentId)
                && Objects.equals(this.behaviorDate, other.behaviorDate)
                && Objects.equals(this.behaviorCategory, other.behaviorCategory)
                && Objects.equals(this.pointValue, other.pointValue)
                && Objects.equals(this.roster, other.roster)
                && Objects.equals(this.student, other.student)
                && Objects.equals(this.teacher, other.teacher);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + 
                Objects.hash(remoteSystem, remoteBehaviorId, remoteStudentId, behaviorDate,
                        behaviorCategory, pointValue, roster, student, teacher);
    }
    
    @Override
    public String toString() { 
        return
                "BEHAVIOR " + "\n"
                + "Id  : " + getId() + "\n"
                + "Name: " + getName() + "\n"
                + "RemoteSystem: " + getRemoteSystem() +"\n"
                + "RemoteBehaviorId: " + getRemoteBehaviorId() +"\n"
                + "RemoteStudentId: " + getRemoteStudentId() + "\n"
                + "BehaviorDate: " + getBehaviorDate() + "\n"
                + "BehaviorCategory: " + getBehaviorCategory() + "\n"
                + "Point Value: " + getPointValue() + "\n"
                + "Roster: " + getRoster() + "\n"
                + "Student: " + getStudent() + "\n"
                + "Teacher: " + getTeacher() + "\n";
    }

    /**
     * Each class's Builder holds a copy of each attribute that the parent POJO has. We build up these properties using
     * a pattern of with[Attribute](Attribute attribute) and return the same instance of the Builder so that one can easily
     * chain setting attributes together.
     */
    public static class BehaviorBuilder extends ApiModelBuilder<BehaviorBuilder, Behavior>{
        private String remoteSystem;
        private String remoteBehaviorId;
        private String remoteStudentId;
        private Date behaviorDate;
        private BehaviorCategory behaviorCategory;
        private String pointValue;
        private String roster;
        private Student student;
        private Teacher teacher;

        public BehaviorBuilder withRemoteSystem(final String remoteSystem){
            this.remoteSystem = remoteSystem;
            return this;
        }

        public BehaviorBuilder withRemoteBehaviorId(final String remoteBehaviorId){
            this.remoteBehaviorId = remoteBehaviorId;
            return this;
        }

        public BehaviorBuilder withRemoteStudentId(final String remoteStudentId){
            this.remoteStudentId = remoteStudentId;
            return this;
        }

        public BehaviorBuilder withBehaviorDate(final Date behaviorDate){
            this.behaviorDate = behaviorDate;
            return this;
        }

        public BehaviorBuilder withBehaviorCategory(final BehaviorCategory behaviorCategory){
            this.behaviorCategory = behaviorCategory;
            return this;
        }

        public BehaviorBuilder withPointValue(final String pointValue){
            this.pointValue = pointValue;
            return this;
        }

        public BehaviorBuilder withRoster(final String roster){
            this.roster = roster;
            return this;
        }

        public BehaviorBuilder withStudent(final Student student){
            this.student = student;
            return this;
        }

        public BehaviorBuilder withTeacher(final Teacher teacher){
            this.teacher = teacher;
            return this;
        }

        public Behavior build(){
            Behavior behavior = super.build();
            behavior.setRemoteSystem(remoteSystem);
            behavior.setRemoteBehaviorId(remoteBehaviorId);
            behavior.setRemoteStudentId(remoteStudentId);
            behavior.setBehaviorDate(behaviorDate);
            behavior.setBehaviorCategory(behaviorCategory);
            behavior.setPointValue(pointValue);
            behavior.setRoster(roster);
            behavior.setStudent(student);
            behavior.setTeacher(teacher);
            return behavior;
        }

        @Override
        protected BehaviorBuilder me() {
            return this;
        }

        @Override
        public Behavior getInstance() {
            return new Behavior();
        }

    }
}
