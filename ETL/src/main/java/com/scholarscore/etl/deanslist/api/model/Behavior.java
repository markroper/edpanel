package com.scholarscore.etl.deanslist.api.model;

/**
 * User: jordan
 * Date: 8/8/15
 * Time: 2:58 PM
 */
public class Behavior {

    public String DLOrganizationID;
    public String DLSchoolID;
    public String SchoolName;
    public String DLStudentID;
    public String StudentSchoolID;
    public String SecondaryStudentID;
    public String StudentFirstName;
    public String StudentMiddleName;
    public String StudentLastName;
    public String DLSAID;
    public String BehaviorDate;
    public String Behavior;
    public String BehaviorCategory;
    public String PointValue;
    public String DLUserID;
    public String StaffSchoolID;
    public String StaffTitle;
    public String StaffFirstName;
    public String StaffMiddleName;
    public String StaffLastName;
    public String Roster;
    public String SourceType;
    public String SourceID;
    public String SourceProcedure;

    @Override
    public String toString() {
        return "DLOrganizationID: " + DLOrganizationID
        + "\n" + "DLSchoolID: " + DLSchoolID
        + "\n" + "SchoolName: " + SchoolName
        + "\n" + "DLStudentID: " + DLStudentID
        + "\n" + "StudentSchoolID: " + StudentSchoolID
        + "\n" + "SecondaryStudentID: " + SecondaryStudentID
        + "\n" + "StudentFirstName: " + StudentFirstName
        + "\n" + "StudentMiddleName: " + StudentMiddleName
        + "\n" + "StudentLastName: " + StudentLastName
        + "\n" + "DLSAID: " + DLSAID
        + "\n" + "BehaviorDate: " + BehaviorDate
        + "\n" + "Behavior: " + Behavior
        + "\n" + "BehaviorCategory: " + BehaviorCategory
        + "\n" + "PointValue: " + PointValue
        + "\n" + "DLUserID: " + DLUserID
        + "\n" + "StaffSchoolID: " + StaffSchoolID
        + "\n" + "StaffTitle: " + StaffTitle
        + "\n" + "StaffFirstName: " + StaffFirstName
        + "\n" + "StaffMiddleName: " + StaffMiddleName
        + "\n" + "StaffLastName: " + StaffLastName
        + "\n" + "Roster: " + Roster
        + "\n" + "SourceType: " + SourceType
        + "\n" + "SourceID: " + SourceID
        + "\n" + "SourceProcedure: " + SourceProcedure;


    }
    
}
