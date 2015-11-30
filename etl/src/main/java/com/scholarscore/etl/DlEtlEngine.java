    package com.scholarscore.etl;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.deanslist.api.response.BehaviorResponse;
import com.scholarscore.etl.deanslist.client.IDeansListClient;
import com.scholarscore.models.ApiModel;
import com.scholarscore.models.Behavior;
import com.scholarscore.models.user.Administrator;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.Teacher;
import com.scholarscore.models.user.User;
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
    private HashMap<String, Administrator> adminLookup; // key: adminName, value: admin

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
            LOGGER.error("Encountered a problem trying to get all students...");
            e.printStackTrace();
            return null;
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

        // get administrators from scholarscore -- these users also assign behavioral events to students
        Collection<Administrator> existingAdministrators = null;
        try {
            existingAdministrators = scholarScore.getAdministrators();
        } catch (HttpClientException e) {
            e.printStackTrace();
        }
        adminLookup = populateLookup(existingAdministrators);

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
        result.setTotalBehaviorsInPeriod(behaviorsToMerge.size());
        
        for (Behavior behavior : behaviorsToMerge) {
            handleBehavior(behavior, result);
        }

        return result;
    }

    private void handleBehavior(Behavior behavior, DeansListSyncResult result) {
        
        // at this point, the only thing populated in the student (from deanslist) is their name
        Student student = behavior.getStudent();
        User assigner = behavior.getAssigner();

        LOGGER.debug("Got behavior event (" + behavior.getName() + ")"
                + " for student named " + (student == null ? "(student null)" : student.getName())
                + " and assigner named " + (assigner == null ? "(assigner null)" : assigner.getName())
                + " with point value " + behavior.getPointValue());

        if (student != null && student.getName() != null) { 
            Student existingStudent = studentLookup.get(stripAndLowerMatchableName(student.getName()));
            if (existingStudent != null) { 
                // student matched! migrate behavioral event
                behavior.setStudent(existingStudent);

                // don't require teacher but populate it if present
                if (assigner != null && assigner.getName() != null) {
                    Teacher existingTeacher = teacherLookup.get(stripAndLowerMatchableName(assigner.getName()));
                    
                    if (existingTeacher != null) {
                        behavior.setAssigner(existingTeacher);
                        result.incrementBehaviorMatchedTeacher();
                    } else {
                       // 'teacher' may be a misnomer - it may also be an administrator 
                        Administrator existingAdmin = adminLookup.get(stripAndLowerMatchableName(assigner.getName()));
                        
                        if (existingAdmin != null) {
                            behavior.setAssigner(existingAdmin);
                            result.incrementBehaviorMatchedAdmin();
                        } else {
                            // null out the teacher that cannot be associated or we will get an error when submitting
                            // (we would need to create this teacher, and DL sync only creates behavior events)
                            behavior.setAssigner(null);
                            result.incrementUnmatchedTeacher(assigner.getName());
                        }
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
                LOGGER.error("WARN: Unable to match to student specified by deanslist: " + student.getName());
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
                lookup.put(stripAndLowerMatchableName(entryName), entry);
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

    // TODO Jordan: temporary hack to get students matching. Today nina's school puts "Nmh" or a middle initial
    // for a student's middle name in a lot of the deanslist behavioral records. Should try to match on 
    // first+middle+last if possible, then fall back to matching first+last if necessary. Should refactor DB to store 
    // first+middle+last separately first.
    private String stripAndLowerMatchableName(String name) {
        if (null == name) { return null; }

        String matchableName = null;
        
        String[] nameWords = name.trim().split("\\s+");
        if (nameWords.length == 0) { matchableName = ""; }                   // no name
        if (nameWords.length == 1) { matchableName = nameWords[0]; }         // one name only
        if (nameWords.length == 2) { matchableName = nameWords[0] + " " + nameWords[1]; }    // first and last
        if (nameWords.length == 3) { matchableName = nameWords[0] + " " + nameWords[2]; }    // first, IGNORE MIDDLE, last
        if (nameWords.length > 3) { matchableName = nameWords[0] + " " + nameWords[nameWords.length - 1]; }  // just guessing...
        return stripAndLowerName(matchableName);
    }
    
    private String stripAndLowerLastName(String name) {
        if (null == name) { return null; }
        String[] nameWords = name.trim().split("\\s+");
        if (nameWords.length > 0) {
            return stripAndLowerName(nameWords[nameWords.length-1]);
        } else {
            return "";
        }
    }

    private List<Behavior> getBehaviorData() {
        BehaviorResponse response = deansList.getBehaviorData();
        return new ArrayList<>(response.toInternalModel());
    }
    
}
