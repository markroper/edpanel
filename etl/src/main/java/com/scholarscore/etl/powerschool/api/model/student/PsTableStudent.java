package com.scholarscore.etl.powerschool.api.model.student;

/**
 * This is a dumb name but there are two different 'model' versions of student that can 
 * come back from powerschool depending on if we hit the "old" or "new" endpoints, and 
 * we need to hit both of them.
 * 
 * User: jordan
 * Date: 3/23/16
 * Time: 3:31 PM
 */
public class PsTableStudent {
    public long id;        // this ID is *only* available from the low-level (i.e. 'table') API call for students
    public long dcid;      // this (dc)ID is what other views call simply "id", which is incredibly confusing
    public String state_studentnumber;
    public long grade_level;
    //The enrollment status of the student. -2=Inactive, -1=Pre-registered, 0=Currently enrolled, 1=Inactive,
    // 2=Transferred out, 3=Graduated, 4=Imported as Historical, Any other value =Inactive. Indexed.
    public int enroll_status;
    public long classOf;
}
