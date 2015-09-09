package com.scholarscore.etl;

import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.deanslist.api.response.BehaviorResponse;
import com.scholarscore.etl.deanslist.client.IDeansListClient;
import com.scholarscore.models.ApiModel;
import com.scholarscore.models.Behavior;
import com.scholarscore.models.Student;
import com.scholarscore.models.Teacher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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

        // grab behaviors from deanslist
        Collection<Behavior> behaviorsToMerge = getBehaviorData();
        System.out.println("got " + behaviorsToMerge.size() + " behavior events from deanslist.");

        // get students from scholarscore -- we need to match names to behavior events
        Collection<Student> existingStudents = scholarScore.getStudents();
        HashMap<String, Student> studentLookup = populateLookup(existingStudents);
        
        System.out.println("got " + existingStudents.size() + " existing students as potential merge targets.");
        // get teachers from scholarscore -- we need to match names to behavior events
        Collection<Teacher> existingTeachers = scholarScore.getTeachers();
        HashMap<String, Teacher> teacherLookup = populateLookup(existingTeachers);
        System.out.println("got " + existingTeachers.size() + " existing teachers as potential merge targets.");
        
        for (Behavior behavior : behaviorsToMerge) {
            // at this point, the only thing populated in the student is their name
            Student student = behavior.getStudent();
            Teacher teacher = behavior.getTeacher();
            System.out.println("Got behavior event (" + behavior.getName() + ")"
                    + " for student named " + (student == null ? "(student null)" : student.getName())
                    + " and teacher named " + (teacher == null ? "(teacher null)" : teacher.getName())
                    + " with point value " + behavior.getPointValue());

            if (student != null && student.getName() != null
                && teacher != null && teacher.getName() != null) {
                Student existingStudent = studentLookup.get(stripAndLowerName(student.getName()));
                Teacher existingTeacher = teacherLookup.get(stripAndLowerName(teacher.getName()));
                if (existingStudent != null && existingTeacher != null) {
                    // student and teacher matched! migrate behavioral event
                    behavior.setStudent(existingStudent);
                    behavior.setTeacher(existingTeacher);
                    System.out.println("About to map Behavior " + behavior.getName()
                            + " to student " + existingStudent.getName()
                            + " and teacher " + existingTeacher.getName());
                    long studentId = existingStudent.getId();
                    scholarScore.createBehavior(studentId, behavior);
                    break;
                }
            } else {
                System.out.println("WARN: Student and/or Teacher was null, skipping behavior event...");
            }
        }

        // TODO Jordan: DeansList ETL in progress
        return new MigrationResult();
    }

    /* Populate a hashmap with a collection of ApiModel objects,
     * using the name of the object as the key. 
     * Multiple objects with the same name are poorly handled at the moment -
     * if more than one object has the same name, the last one wins.
     * TODO: how to handle duplicate names?
     * NOTE: The ApiModel's name is stripped of spaces and lowercased when it is set as the key.
     *
     */
    private <T extends ApiModel> HashMap<String, T> populateLookup(Collection<T> collection) {
        HashMap<String, T> lookup = new HashMap<>();
        for (T entry : collection) {
            String entryName = entry.getName();
            if (entryName != null) {
                lookup.put(stripAndLowerName(entryName), entry);
            }
        }
        return lookup;
    }

    private String stripAndLowerName(String name) {
        if (null == name) { return null; }
        return name.toLowerCase().trim().replaceAll("\\s", "");
    }

    private List<Behavior> getBehaviorData() {
        BehaviorResponse response = deansList.getBehaviorData();
        return new ArrayList<>(response.toInternalModel());
    }
    
}
