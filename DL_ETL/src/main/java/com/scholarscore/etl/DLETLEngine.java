package com.scholarscore.etl;

import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.deanslist.api.response.BehaviorResponse;
import com.scholarscore.etl.deanslist.client.IDeansListClient;
import com.scholarscore.models.ApiModel;
import com.scholarscore.models.Behavior;
import com.scholarscore.models.Student;
import com.scholarscore.models.Teacher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    final static Logger logger = LoggerFactory.getLogger(DLETLEngine.class);
    
    // wired up by spring
    private IDeansListClient deansList;
    private IAPIClient scholarScore;

    // created and used locally - not handled by spring
    private HashMap<String, Student> studentLookup; // key: studentName, value: student
    private HashMap<String, Teacher> teacherLookup; // key: teacherName, value: teacher

    // key: scholarScoreStudentID, value: *(nested, next)*
    // key: deansListBehaviorId, value: behavior object
    private HashMap<Long, HashMap<String, Behavior>> existingBehaviorLookup;

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

        // get students from scholarscore -- we need to match names to behavior events
        Collection<Student> existingStudents = scholarScore.getStudents();
        studentLookup = populateLookup(existingStudents);

        // get teachers from scholarscore -- we need to match names to behavior events
        Collection<Teacher> existingTeachers = scholarScore.getTeachers();
        teacherLookup = populateLookup(existingTeachers);

        logger.info("got " + behaviorsToMerge.size() + " behavior events from deanslist.");
        for (Student student : existingStudents) { logger.info("Got scholarScore student: " + student); }
        logger.info("got " + existingStudents.size() + " existing students as potential merge targets.");
        logger.info("got " + existingTeachers.size() + " existing teachers as potential merge targets.");

        existingBehaviorLookup = new HashMap<>();
        
        for (Behavior behavior : behaviorsToMerge) {
            handleBehavior(behavior);
        }

        // TODO Jordan: What to return, if anything, in migration result?
        return new MigrationResult();
    }
    
    private void handleBehavior(Behavior behavior) {
        // at this point, the only thing populated in the student (from deanslist) is their name
        Student student = behavior.getStudent();
        Teacher teacher = behavior.getTeacher();

        logger.info("Got behavior event (" + behavior.getName() + ")"
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
                logger.info("About to map Behavior " + behavior.getName()
                        + " to student " + existingStudent.getName()
                        + " and teacher " + existingTeacher.getName());
                long studentId = existingStudent.getId();

                // check if this student's behavioral lookup has already been done
                HashMap<String, Behavior> studentBehaviorEvents = existingBehaviorLookup.get(studentId);
                if (studentBehaviorEvents == null) {
                    // call scholarscore API to get this student's behavioral records, save in cache
                    // (we need to search for the behavioral event within these results)
                    Collection<Behavior> studentBehaviors = scholarScore.getBehaviors(studentId);
                    
                    HashMap<String, Behavior> studentBehaviorHashMap = populateLookup(studentBehaviors);
                    existingBehaviorLookup.put(studentId, studentBehaviorHashMap);
                    studentBehaviorEvents = studentBehaviorHashMap;
                }
                
                // this student's behaviors are now available in all cases, so try to find this behavioral event
                Behavior scholarScoreBehavior = studentBehaviorEvents.get(behavior.getRemoteBehaviorId());
                if (scholarScoreBehavior == null) {
                    // behavior not found, add it via API...
                    Behavior createdBehavior = scholarScore.createBehavior(studentId, behavior);
                    // ... and save in cache
                    studentBehaviorEvents.put(createdBehavior.getRemoteBehaviorId(), createdBehavior);
                } else {
                    // behavior exists already in scholarscore (with id scholarScoreBehaviorId), update it
                    Long behaviorId = scholarScoreBehavior.getId();
                    scholarScore.updateBehavior(studentId, behaviorId, behavior);
                }

            }
        } else {
            logger.warn("WARN: Student and/or Teacher was null, skipping behavior event...");
        }
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
