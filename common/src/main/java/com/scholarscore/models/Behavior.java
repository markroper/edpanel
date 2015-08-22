package com.scholarscore.models;

/**
 * User: jordan
 * Date: 8/8/15
 * Time: 3:21 PM
 */
public class Behavior extends ApiModel implements IApiModel<Behavior> {
    
    // "name" in parent class maps to 'behavior'
    private String remoteSystemEventId;    // currently always deanslist DLSAID
    // TODO Jordan should this be date?
    private String behaviorDate;
    private String behaviorCategory;
    private String pointValue;
    private String roster; // the class the behavior event occurred within

    // TODO Jordan need to hook up transient Student and Teacher here
    private transient Student student;
    private transient Teacher teacher;
    
    public Behavior() { }
    
    public Behavior(Behavior behavior) {
        super(behavior);
        // "name" in parent class maps to 'behavior'
        this.remoteSystemEventId = behavior.remoteSystemEventId;    // currently always deanslist DLSAID
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

        throw new UnsupportedOperationException("not implemented yet");
    }

    public String getRemoteSystemEventId() {
        return remoteSystemEventId;
    }

    public void setRemoteSystemEventId(String remoteSystemEventId) {
        this.remoteSystemEventId = remoteSystemEventId;
    }

    public String getBehaviorDate() {
        return behaviorDate;
    }

    public void setBehaviorDate(String behaviorDate) {
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
}
