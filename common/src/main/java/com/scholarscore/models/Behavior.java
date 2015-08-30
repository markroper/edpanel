package com.scholarscore.models;

import com.fasterxml.jackson.annotation.JsonInclude;

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
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Behavior extends ApiModel implements IApiModel<Behavior> {
    
    // "name" in parent class maps to 'behavior'
    @Size(min=1, max=256)
    private String remoteStudentId;    // currently always deanslist DLSAID
    private Date behaviorDate;
    @Size(min=1, max=256)
    private String behaviorCategory;
    @Size(min=1, max=256)
    private String pointValue;
    @Size(min=1, max=256)
    private String roster; // the class the behavior event occurred within
    private transient Student student;
    private transient Teacher teacher;
    
    public Behavior() { }
    
    public Behavior(Behavior behavior) {
        super(behavior);
        // "name" in parent class maps to 'behavior'
        this.remoteStudentId = behavior.remoteStudentId;    // currently always deanslist DLSAID
        this.behaviorDate = behavior.behaviorDate;
        this.behaviorCategory = behavior.behaviorCategory;
        this.pointValue = behavior.pointValue;
        this.roster = behavior.roster; // the class the behavior event occurred within

        this.student = behavior.student;
        this.teacher = behavior.teacher;
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

    public String getRemoteStudentId() {
        return remoteStudentId;
    }

    public void setRemoteStudentId(String remoteStudentId) {
        this.remoteStudentId = remoteStudentId;
    }

    public Date getBehaviorDate() {
        return behaviorDate;
    }

    public void setBehaviorDate(Date behaviorDate) {
        this.behaviorDate = behaviorDate;
    }

    public String getBehaviorCategory() {
        return behaviorCategory;
    }

    public void setBehaviorCategory(String behaviorCategory) {
        this.behaviorCategory = behaviorCategory;
    }

    public String getPointValue() {
        return pointValue;
    }

    public void setPointValue(String pointValue) {
        this.pointValue = pointValue;
    }

    public String getRoster() {
        return roster;
    }

    public void setRoster(String roster) {
        this.roster = roster;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

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
        return Objects.equals(this.remoteStudentId, other.remoteStudentId)
                && Objects.equals(this.behaviorDate, other.behaviorDate)
                && Objects.equals(this.behaviorCategory, other.behaviorCategory)
                && Objects.equals(this.pointValue, other.pointValue)
                && Objects.equals(this.roster, other.roster)
                && Objects.equals(this.student, other.student)
                && Objects.equals(this.teacher, other.teacher);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(remoteStudentId, behaviorDate, behaviorCategory, pointValue, roster, student, teacher);
    }
    
    @Override
    public String toString() { 
        return
                "BEHAVIOR " + "\n"
                + "Id  : " + getId() + "\n"
                + "Name: " + getName() + "\n"
                + "RemoteStudentId: " + getRemoteStudentId() + "\n"
                + "BehaviorDate: " + getBehaviorDate() + "\n"
                + "BehaviorCategory: " + getBehaviorCategory() + "\n"
                + "Point Value: " + getPointValue() + "\n"
                + "Roster: " + getRoster() + "\n"
                + "Student: " + getStudent() + "\n"
                + "Teacher: " + getTeacher() + "\n";
    }
}
