package com.scholarscore.etl.deanslist.api.model;

/**
 * DeansList data model for a student.
 * 
 * Created by jwinch on 7/23/15.
 */
public class Student {
    
    public String DLSchoolID;
    public String SchoolName;
    public String StudentSchoolID;
    public String SecondaryStudentID;
    public String FirstName;
    public String MiddleName;
    public String LastName;
    public String GradeLevel;
    public String StartDate;
    public String EndDate;
    
    @Override
    public String toString() { 
        return "\n" + "{"
                + "\n" + "\"DLSchoolID\":\"" + DLSchoolID + "\","
                + "\n" + "\"SchoolName\":\"" + SchoolName + "\","
                + "\n" + "\"StudentSchoolID\":\"" + StudentSchoolID + "\","
                + "\n" + "\"SecondaryStudentID\":\"" + SecondaryStudentID + "\","
                + "\n" + "\"FirstName\":\"" + FirstName + "\","
                + "\n" + "\"MiddleName\":\"" + MiddleName + "\","
                + "\n" + "\"LastName\":\"" + LastName + "\","
                + "\n" + "\"GradeLevel\":\"" + GradeLevel + "\","
                + "\n" + "\"StartDate\":\"" + StartDate + "\","
                + "\n" + "\"EndDate\":\"" + EndDate + "\""
                + "}";
    }
    
    
    
}
