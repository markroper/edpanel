package com.scholarscore.etl;

import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.deanslist.api.response.StudentResponse;
import com.scholarscore.etl.deanslist.client.IDeansListClient;
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

        Collection<Student> existingStudents = scholarScore.getStudents();
        Collection<Student> studentsToMerge = getStudents();

        // TODO Jordan: just for testing. Students found in both DeansList and local Scholar DB 
        // will have their stats updated, others won't.
        for (Student student : studentsToMerge) {
            student.setFederalRace("Klingon");
            student.setFederalEthnicity("Latino");
        }

        mergeOnName(existingStudents, studentsToMerge);

        return new MigrationResult();
    }

    // TODO: WIP
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
    
}
