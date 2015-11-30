package com.scholarscore.etl;

import java.util.HashSet;

/**
 * User: jordan
 * Date: 11/25/15
 * Time: 5:52 PM
 */
public class DeansListSyncResult extends BaseSyncResult {

    private int totalBehaviorsInPeriod = 0;
    
    private int behaviorEventsWithoutStudents = 0;
    private int behaviorEventsWithUnmatchedStudents = 0;
    private final HashSet<String> studentsNotMatched = new HashSet<>();

    private int behaviorEventsWithoutTeachers = 0;
    private int behaviorEventsWithUnmatchedTeachers = 0;
    private final HashSet<String> teachersNotMatched = new HashSet<>();

    private int behaviorsAdded = 0;
    private int behaviorsUpdated = 0;

    private int behaviorsMatchedTeacher = 0;
    private int behaviorsMatchedAdmin = 0;

    // both first and last names match
    private int behaviorEventsMatchedStudentLastAndFirst = 0;
    // if only one user has the same last name as the one we're trying to match, we'll match it regardless
    // but if the first names don't match, this will be incremented
    private int behaviorEventsMatchedStudentLastButNotFirst = 0;
    // if more than one user has the same last name and no first names match, the record won't be imported and this will be incremented
    private int behaviorEventsFailedToMatchFirstWithMultipleStudents = 0;

    private int behaviorEventsMatchedTeacherLastAndFirst = 0;
    private int behaviorEventsMatchedTeacherLastButNotFirst = 0;
    private int behaviorEventsFailedToMatchFirstWithMultipleTeachers = 0;
    
    private int behaviorEventsMatchedAdminLastAndFirst = 0;
    private int behaviorEventsMatchedAdminLastButNotFirst = 0;
    private int behaviorEventsFailedToMatchFirstWithMultipleAdmins = 0;
    
    @Override
    public String getResultString() {
        StringBuilder builder = new StringBuilder();
        builder.append("--");
        builder.append("\n");
        builder.append("Total Behavior Events: " + totalBehaviorsInPeriod);
        builder.append("\n");
        builder.append("Behavior Events Added: " + behaviorsAdded);
        builder.append("\n");
        builder.append("Behavior Events Updated: " + behaviorsUpdated);
        builder.append("\n");
        builder.append("Behavior Events Matching Teachers: " + behaviorsMatchedTeacher);
        builder.append("\n");
        builder.append("Behavior Events Matching Admins: " + behaviorsMatchedAdmin);
        builder.append("\n");
        builder.append("Behavior Events Failed (student missing from DL): " + behaviorEventsWithoutStudents);
        builder.append("\n");
        builder.append("Behavior Events Failed (student unmatched in EP): " + behaviorEventsWithUnmatchedStudents);
        builder.append("\n");
        builder.append("--");
        builder.append("\n");
        builder.append("Behavior Events Without Matching EdPanel Teachers/Admins: " + behaviorEventsWithUnmatchedTeachers);
        builder.append("\n");
        builder.append("Behavior Events Without Any Specified Teachers/Admins: " + behaviorEventsWithoutTeachers);
        builder.append("\n");
        builder.append("--");
        builder.append("\n");
        if (teachersNotMatched.size() > 0) {
            builder.append("Unmatched Teachers (" + teachersNotMatched.size() + "): ");
            builder.append("\n");
            for (String unmatchedTeacherName : teachersNotMatched) {
                builder.append("  " + unmatchedTeacherName);
                builder.append("\n");
            }
            builder.append("--");
            builder.append("\n");
        }
        if (studentsNotMatched.size() > 0) {
            builder.append("Unmatched Students (" + studentsNotMatched.size() + "): ");
            builder.append("\n");
            for (String unmatchedStudentName : studentsNotMatched) {
                builder.append("  " + unmatchedStudentName);
                builder.append("\n");
            }
            builder.append("--");
            builder.append("\n");
        }
        return builder.toString();
    }
    
    public void incrementBehaviorWithoutTeacher() { behaviorEventsWithoutTeachers++; }
    public void incrementBehaviorWithoutStudent() { behaviorEventsWithoutStudents++; }
    
    public void incrementUnmatchedStudent(String unmatchedStudentName) { 
        behaviorEventsWithUnmatchedStudents++;
        studentsNotMatched.add(unmatchedStudentName);
    }
    
    public void incrementUnmatchedTeacher(String unmatchedTeacherName) { 
        behaviorEventsWithUnmatchedTeachers++;
        teachersNotMatched.add(unmatchedTeacherName);
    }
    
    public void incrementBehaviorAdded() { behaviorsAdded++; }
    
    public void incrementBehaviorUpdated() { behaviorsUpdated++; }
    
    public void incrementBehaviorMatchedTeacher() { behaviorsMatchedTeacher++; }
    
    public void incrementBehaviorMatchedAdmin() { behaviorsMatchedAdmin++; }
    
    public void setTotalBehaviorsInPeriod(int totalBehaviorsInPeriod) { this.totalBehaviorsInPeriod = totalBehaviorsInPeriod; }

    public void incrementBehaviorEventsMatchedStudentLastAndFirst() { behaviorEventsMatchedStudentLastAndFirst++; }
    
    public void incrementBehaviorEventsMatchedStudentLastButNotFirst() { behaviorEventsMatchedStudentLastButNotFirst++; }
    
    public void incrementBehaviorEventsFailedToMatchFirstWithMultipleStudents() { behaviorEventsFailedToMatchFirstWithMultipleStudents++; }

    public void incrementBehaviorEventsMatchedTeacherLastAndFirst() { behaviorEventsMatchedTeacherLastAndFirst++; }

    public void incrementBehaviorEventsMatchedTeacherLastButNotFirst() { behaviorEventsMatchedTeacherLastButNotFirst++; }

    public void incrementBehaviorEventsFailedToMatchFirstWithMultipleTeachers() { behaviorEventsFailedToMatchFirstWithMultipleTeachers++; }

    public void incrementBehaviorEventsMatchedAdminLastAndFirst() { behaviorEventsMatchedAdminLastAndFirst++; }

    public void incrementBehaviorEventsMatchedAdminLastButNotFirst() { behaviorEventsMatchedAdminLastButNotFirst++; }

    public void incrementBehaviorEventsFailedToMatchFirstWithMultipleAdmins() { behaviorEventsFailedToMatchFirstWithMultipleAdmins++; }
    
}
