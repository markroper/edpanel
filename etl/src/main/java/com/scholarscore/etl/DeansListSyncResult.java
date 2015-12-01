package com.scholarscore.etl;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
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
    private int behaviorEventsWithUnmatchedAssigners = 0;
    private final HashSet<String> assignersNotMatched = new HashSet<>();

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
    
    private HashSet<Pair<String, String>> studentsFuzzyMatched = new HashSet<>();
    private HashSet<Pair<String, String>> teachersFuzzyMatched = new HashSet<>();
    private HashSet<Pair<String, String>> adminsFuzzyMatched = new HashSet<>();
    
    private HashSet<String> studentsNotMatchedBecauseMultipleLastName = new HashSet<>();
    // right now this is kinda shitty because these collections are separate...
    // really, since any assigner is generally a teacher OR an admin but not necessarily both,
    // we are only worried about names that appear in BOTH of these sets
    // (at least until the dl is upgraded again to combine these lists into one, for whatever that entails)
//    private HashSet<String> teachersNotMatchedBecauseMultipleLastName = new HashSet<>();
//    private HashSet<String> adminsNotMatchedBecauseMultipleLastName = new HashSet<>();
    
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
        builder.append("Behavior Events Without Matching EdPanel Teachers/Admins: " + behaviorEventsWithUnmatchedAssigners);
        builder.append("\n");
        builder.append("Behavior Events Without Any Specified Teachers/Admins: " + behaviorEventsWithoutTeachers);
        builder.append("\n");
        builder.append("--");
        builder.append("\n");
        if (assignersNotMatched.size() > 0) {
            builder.append("Unmatched Teachers (" + assignersNotMatched.size() + "): ");
            builder.append("\n");
            for (String unmatchedTeacherName : assignersNotMatched) {
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
    
    public void incrementUnmatchedAssigner(String unmatchedAssignerName) { 
        behaviorEventsWithUnmatchedAssigners++;
        assignersNotMatched.add(unmatchedAssignerName);
    }
    
    public void incrementBehaviorAdded() { behaviorsAdded++; }
    
    public void incrementBehaviorUpdated() { behaviorsUpdated++; }
    
    public void incrementBehaviorMatchedTeacher() { behaviorsMatchedTeacher++; }
    
    public void incrementBehaviorMatchedAdmin() { behaviorsMatchedAdmin++; }
    
    public void setTotalBehaviorsInPeriod(int totalBehaviorsInPeriod) { this.totalBehaviorsInPeriod = totalBehaviorsInPeriod; }

    public void incrementBehaviorEventsMatchedStudentLastAndFirst() { behaviorEventsMatchedStudentLastAndFirst++; }
    
    public void incrementBehaviorEventsMatchedStudentLastButNotFirst(String mappedFrom, String mappedTo) {
        behaviorEventsMatchedStudentLastButNotFirst++; 
        studentsFuzzyMatched.add(Pair.of(mappedFrom, mappedTo));
    }
    
    public void incrementBehaviorEventsFailedToMatchFirstWithMultipleStudents(String failedToMatch) { 
        behaviorEventsFailedToMatchFirstWithMultipleStudents++; 
        studentsNotMatchedBecauseMultipleLastName.add(failedToMatch);
    }

    public void incrementBehaviorEventsMatchedTeacherLastAndFirst() { behaviorEventsMatchedTeacherLastAndFirst++; }

    public void incrementBehaviorEventsMatchedTeacherLastButNotFirst(String mappedFrom, String mappedTo) {
        behaviorEventsMatchedTeacherLastButNotFirst++; 
        teachersFuzzyMatched.add(Pair.of(mappedFrom, mappedTo));
    }

    public void incrementBehaviorEventsFailedToMatchFirstWithMultipleTeachers() { behaviorEventsFailedToMatchFirstWithMultipleTeachers++; }

    public void incrementBehaviorEventsMatchedAdminLastAndFirst() { behaviorEventsMatchedAdminLastAndFirst++; }

    public void incrementBehaviorEventsMatchedAdminLastButNotFirst(String mappedFrom, String mappedTo) { 
        behaviorEventsMatchedAdminLastButNotFirst++; 
        adminsFuzzyMatched.add(Pair.of(mappedFrom, mappedTo));
    }

    public void incrementBehaviorEventsFailedToMatchFirstWithMultipleAdmins() { behaviorEventsFailedToMatchFirstWithMultipleAdmins++; }
    
}
