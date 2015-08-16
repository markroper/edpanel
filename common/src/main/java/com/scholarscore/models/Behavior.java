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

    // should be linked to actual student/staff by ID
    private String studentName;
    private String staffName;

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

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRoster() {
        return roster;
    }

    public void setRoster(String roster) {
        this.roster = roster;
    }
}
