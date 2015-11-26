    package com.scholarscore.etl;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.deanslist.api.response.BehaviorResponse;
import com.scholarscore.etl.deanslist.client.IDeansListClient;
import com.scholarscore.models.ApiModel;
import com.scholarscore.models.Behavior;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.Teacher;
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
public class DlEtlEngine implements IEtlEngine {

    private final static Logger LOGGER = LoggerFactory.getLogger(DlEtlEngine.class);
    
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
    public SyncResult syncDistrict() {

        // grab behaviors from deanslist
        Collection<Behavior> behaviorsToMerge = getBehaviorData();

        // get students from scholarscore -- we need to match names to behavior events
        Collection<Student> existingStudents = null;
        try {
            existingStudents = scholarScore.getStudents(null);
        } catch (HttpClientException e) {
            e.printStackTrace();
        }
        studentLookup = populateLookup(existingStudents);

        // get teachers from scholarscore -- we need to match names to behavior events
        Collection<Teacher> existingTeachers = null;
        try {
            existingTeachers = scholarScore.getTeachers();
        } catch (HttpClientException e) {
            e.printStackTrace();
        }
        teacherLookup = populateLookup(existingTeachers);

        LOGGER.debug("got " + behaviorsToMerge.size() + " behavior events from deanslist.");
        if (existingStudents != null) {
            for (Student student : existingStudents) {
                LOGGER.debug("Got scholarScore student: " + student);
            }
            LOGGER.debug("got " + existingStudents.size() + " existing students as potential merge targets.");
            LOGGER.debug("got " + existingTeachers.size() + " existing teachers as potential merge targets.");
        }

        existingBehaviorLookup = new HashMap<>();
        
        DeansListSyncResult result = new DeansListSyncResult();
        
        for (Behavior behavior : behaviorsToMerge) {
            handleBehavior(behavior, result);
        }

        return result;
    }

    private void handleBehavior(Behavior behavior, DeansListSyncResult result) {
        
        // at this point, the only thing populated in the student (from deanslist) is their name
        Student student = behavior.getStudent();
        Teacher teacher = behavior.getTeacher();

        LOGGER.debug("Got behavior event (" + behavior.getName() + ")"
                + " for student named " + (student == null ? "(student null)" : student.getName())
                + " and teacher named " + (teacher == null ? "(teacher null)" : teacher.getName())
                + " with point value " + behavior.getPointValue());

        if (student != null && student.getName() != null) { 
            Student existingStudent = studentLookup.get(stripAndLowerName(student.getName()));
            if (existingStudent != null) { 
                // student matched! migrate behavioral event
                behavior.setStudent(existingStudent);

                // don't require teacher but populate it if present
                if (teacher != null && teacher.getName() != null) {
                    Teacher existingTeacher = teacherLookup.get(stripAndLowerName(teacher.getName()));
                    
                    if (existingTeacher != null) {
                        behavior.setTeacher(existingTeacher);
                    } else {
                        // null out the teacher that cannot be associated or we will get an error when submitting
                        // (we would need to create this teacher, and DL sync only creates behavior events)
                        behavior.setTeacher(null);
                        result.incrementUnmatchedTeacher(teacher.getName());
                    }
                } else {
                    // deanslist did not contain teacher information with this record
                    result.incrementBehaviorWithoutTeacher();
                }

                LOGGER.debug("About to map Behavior " + behavior.getName()
                        + " to student " + existingStudent.getName());
                long studentId = existingStudent.getId();

                // check if this student's behavioral lookup has already been done
                HashMap<String, Behavior> studentBehaviorEvents = existingBehaviorLookup.get(studentId);
                if (studentBehaviorEvents == null) {
                    // call scholarscore API to get this student's behavioral records, save in cache
                    // (we need to search for the behavioral event within these results)
                    Collection<Behavior> studentBehaviors = null;
                    try {
                        studentBehaviors = scholarScore.getBehaviors(studentId);
                    } catch (HttpClientException e) {
                        e.printStackTrace();
                    }

                    HashMap<String, Behavior> studentBehaviorHashMap = populateBehaviorLookup(studentBehaviors);
                    existingBehaviorLookup.put(studentId, studentBehaviorHashMap);
                    studentBehaviorEvents = studentBehaviorHashMap;
                }
                
                // this student's behaviors are now available in all cases, so try to find this behavioral event
                Behavior scholarScoreBehavior = studentBehaviorEvents.get(behavior.getRemoteBehaviorId());
                if (scholarScoreBehavior == null) {
                    // behavior not found, add it via API...
                    Behavior createdBehavior = null;
                    try {
                        createdBehavior = scholarScore.createBehavior(studentId, behavior);
                    } catch (HttpClientException e) {
                        e.printStackTrace();
                    }
                    // ... and save in cache
                    if (createdBehavior != null) {  // this should always be true...
                        studentBehaviorEvents.put(createdBehavior.getRemoteBehaviorId(), createdBehavior);
                        result.incrementBehaviorAdded();
                    }
                } else {
                    // behavior exists already in scholarscore (with id scholarScoreBehaviorId), update it
                    Long behaviorId = scholarScoreBehavior.getId();
                    result.incrementBehaviorUpdated();
                    try {
                        scholarScore.updateBehavior(studentId, behaviorId, behavior);
                    } catch (HttpClientException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                LOGGER.warn("WARN: Deanslist specified a unknown student: " + student.getName());
                result.incrementUnmatchedStudent(student.getName());
            }
        } else {
            LOGGER.warn("WARN: Student was null, skipping this behavior event...");
            result.incrementBehaviorWithoutStudent();
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

    private HashMap<String, Behavior> populateBehaviorLookup(Collection<Behavior> behaviors) {
        HashMap<String, Behavior> lookup = new HashMap<>();
        for (Behavior entry : behaviors) {
            String entryName = entry.getRemoteBehaviorId();
            if (entryName != null) {
                lookup.put(entryName, entry);
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
