package com.scholarscore.etl;

import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.deanslist.api.response.BehaviorResponse;
import com.scholarscore.etl.deanslist.api.response.StudentResponse;
import com.scholarscore.etl.deanslist.client.IDeansListClient;
import com.scholarscore.models.Behavior;
import com.scholarscore.models.Student;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: jordan
 * Date: 7/28/15
 * Time: 7:14 PM
 */
public class DLETLEngine implements IETLEngine {

    private IDeansListClient deansList;
    private IAPIClient scholarScore;

    public IDeansListClient getDeansList() {
        return deansList;
    }

    public void setDeansList(IDeansListClient deansList) {
        this.deansList = deansList;
    }

    public void setScholarScore(IAPIClient scholarScore) {
        this.scholarScore = scholarScore;
    }

    public IAPIClient getScholarScore() {
        return scholarScore;
    }

    @Override
    public MigrationResult migrateDistrict() {

        Collection<Behavior> existingBehaviors = scholarScore.getBehaviors();

        Collection<Behavior> behaviorsToMerge = getBehaviorData();

        Collection<Student> existingStudents = scholarScore.getStudents();
//        Collection<Student> studentsToMerge = getStudents();

        // TODO Jordan make these terrible loops better
        for (Behavior behavior : behaviorsToMerge) {
            // at this point, the only thing populated in the student is their name
            Student student = behavior.getStudent();
            System.out.println("Got behavior event for student named " + (student == null ? "(student null)" : student.getName() )
                    + " (" + behavior.getName() + ") with point value " + behavior.getPointValue());

            if (student != null) {
                String studentName = student.getName();
                if (studentName != null) {
                    String simpleStudentName = stripAndLowerName(studentName);
                    for (Student existingStudent : existingStudents) {
                        if (simpleStudentName.equalsIgnoreCase(stripAndLowerName(existingStudent.getName()))) {
                            // student name specified in behavior event matches existing student name
                            behavior.setStudent(existingStudent);
                            System.out.println("About to create Behavior " + behavior.getName() + " for student " + studentName + " matched student " + existingStudent.getName());
                            scholarScore.createBehavior(behavior);
                            break;
                        }
                    }
                }
            }
//            behavior.setStudent();
            // replace 
            
            // same for teacher
//            behavior.getTeacher();
        }

        // TODO Jordan: wrap up DeansList ETL migration task
        // just for testing. Students found in both DeansList and local Scholar DB
        // will have their stats updated, others won't.
//        for (Student student : studentsToMerge) {
//            // insert actual behavioral data here
//            student.setFederalRace("Klingon");
//            student.setFederalEthnicity("Latino");
//        }

//        mergeOnName(existingStudents, studentsToMerge);
        return new MigrationResult();
    }

    // TODO Jordan: WIP
    private void mergeOnName(Collection<Student> existingStudents, Collection<Student> studentsToMerge) {

        for (Student studentToMerge : studentsToMerge) {
            int matches = 0;
            Long lastMatchedId = null;
            String mergeName = stripAndLowerName(studentToMerge.getName());
            if (mergeName != null) {
                for (Student existingStudent : existingStudents) {
                    String existingName = stripAndLowerName(existingStudent.getName());
                    if (mergeName.equals(existingName)) {
                        // Match!
                        matches++;
                        lastMatchedId = existingStudent.getId();
                    }
                }
                if (matches < 1) {
                    // no matching student found
                    System.out.println("XX No student found matching incoming name " + mergeName);
                } else if (matches >= 1) {
                    
                    // matching student found
                    System.out.println("!! student found matching incoming name " + mergeName);
                    scholarScore.updateStudent(lastMatchedId, studentToMerge);

                    // TODO: need to handle multiple matches differently
//                } else if (matches > 1) {
//                     // more than one match found, need to refine further
//                    System.out.println("~~ More than one student found matching incoming name " + mergeName);
                }
                
            }
        }
    }
    
    private String stripAndLowerName(String name) {
        if (null == name) { return null; }
        return name.toLowerCase().trim().replaceAll("\\s", "");
    }

    private List<Student> getStudents() {
        StudentResponse response = deansList.getStudents();
        return new ArrayList<>(response.toInternalModel());
    }
    
    private List<Behavior> getBehaviorData() {
        BehaviorResponse response = deansList.getBehaviorData();
        return new ArrayList<>(response.toInternalModel());
    }
    
}
