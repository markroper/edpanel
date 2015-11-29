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
        builder.append("Behavior Events Failed (student missing from DL): " + behaviorEventsWithoutStudents);
        builder.append("\n");
        builder.append("Behavior Events Failed (student unmatched in EP): " + behaviorEventsWithUnmatchedStudents);
        builder.append("\n");
        builder.append("--");
        builder.append("\n");
        builder.append("Behavior Events Without Matching EdPanel Teachers: " + behaviorEventsWithUnmatchedTeachers);
        builder.append("\n");
        builder.append("Behavior Events Without Teachers: " + behaviorEventsWithoutTeachers);
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
    
    public void setTotalBehaviorsInPeriod(int totalBehaviorsInPeriod) { this.totalBehaviorsInPeriod = totalBehaviorsInPeriod; }
    
}
