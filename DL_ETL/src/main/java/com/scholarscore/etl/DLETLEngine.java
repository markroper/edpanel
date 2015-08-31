package com.scholarscore.etl;

import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.deanslist.api.response.BehaviorResponse;
import com.scholarscore.etl.deanslist.client.IDeansListClient;
import com.scholarscore.models.Behavior;
import com.scholarscore.models.Student;
import com.scholarscore.models.Teacher;

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

        // grab behaviors from deanslist
        Collection<Behavior> behaviorsToMerge = getBehaviorData();
        System.out.println("got " + behaviorsToMerge.size() + " behavior events from deanslist.");

        // get students from scholarscore -- we need to match names to behavior events
        Collection<Student> existingStudents = scholarScore.getStudents();
        System.out.println("got " + existingStudents.size() + " existing students as potential merge targets.");
        // get teachers from scholarscore -- we need to match names to behavior events
        Collection<Teacher> existingTeachers = scholarScore.getTeachers();
        System.out.println("got " + existingTeachers.size() + " existing teachers as potential merge targets.");
        
        // TODO Jordan make this terrible nesting less terrible. don't search the same existing students
        // and teachers multiple times -- hashmapify this before merge
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
                String studentName = stripAndLowerName(student.getName());
                String teacherName = stripAndLowerName(teacher.getName());
                for (Student existingStudent : existingStudents) {
                    if (studentName.equalsIgnoreCase(stripAndLowerName(existingStudent.getName()))) {
                        // student found, now we need to locate teacher
                        for (Teacher existingTeacher : existingTeachers) {
                            if (teacherName.equalsIgnoreCase(stripAndLowerName(existingTeacher.getName()))) {
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
                        }
                    }
                }
            } else {
                System.out.println("WARN: Student and/or Teacher was null, skipping behavior event...");
            }
        }

        // TODO Jordan: DeansList ETL in progress
        return new MigrationResult();
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
