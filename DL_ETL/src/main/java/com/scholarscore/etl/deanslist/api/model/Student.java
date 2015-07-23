package com.scholarscore.etl.deanslist.api.model;

/**
 * DeansList data model for a student.
 * 
 * Created by jwinch on 7/23/15.
 */
public class Student {
    
    String DLSchoolID;
    String SchoolName;
    String StudentSchoolID;
    String SecondaryStudentID;
    String FirstName;
    String MiddleName;
    String LastName;
    String GradeLevel;
    String StartDate;
    String EndDate;
    
    @Override
    public String toString() { 
        return "{" 
                + "\"DLSchoolID\":\"" + DLSchoolID + "\","
                + "\"SchoolName\":\"" + SchoolName + "\","
                + "\"StudentSchoolID\":\"" + StudentSchoolID + "\","
                + "\"SecondaryStudentID\":\"" + SecondaryStudentID + "\","
                + "\"FirstName\":\"" + FirstName + "\","
                + "\"MiddleName\":\"" + MiddleName + "\","
                + "\"LastName\":\"" + LastName + "\","
                + "\"GradeLevel\":\"" + GradeLevel + "\","
                + "\"StartDate\":\"" + StartDate + "\","
                + "\"EndDate\":\"" + EndDate + "\""
                + "}";
    }
    
}
