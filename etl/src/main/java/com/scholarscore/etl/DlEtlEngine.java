    package com.scholarscore.etl;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.deanslist.api.response.BehaviorResponse;
import com.scholarscore.etl.deanslist.client.IDeansListClient;
import com.scholarscore.etl.runner.EtlSettings;
import com.scholarscore.models.ApiModel;
import com.scholarscore.models.Behavior;
import com.scholarscore.models.user.Staff;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
    
    // key: studentLastName, value: hashmap of students w/ that last name (key: student first name, value: student)
    private HashMap<String, HashMap<String, Student>> studentLastNameLookup;
    private HashMap<String, HashMap<String, Staff>> teacherLastNameLookup;
    private HashMap<String, HashMap<String, Staff>> adminLastNameLookup;
     
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
    public SyncResult syncDistrict(EtlSettings settings) {

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
        studentLastNameLookup = populateNestedNameLookup(existingStudents);

        // get teachers from scholarscore -- we need to match names to behavior events
        Collection<Staff> existingTeachers = null;
        try {
            existingTeachers = scholarScore.getTeachers();
        } catch (HttpClientException e) {
            e.printStackTrace();
        }
        teacherLastNameLookup = populateNestedNameLookup(existingTeachers);

        // get administrators from scholarscore -- these users also assign behavioral events to students
        Collection<Staff> existingAdministrators = null;
        try {
            existingAdministrators = scholarScore.getAdministrators();
        } catch (HttpClientException e) {
            e.printStackTrace();
        }
        adminLastNameLookup = populateNestedNameLookup(existingAdministrators);

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

    private <T extends User> T findUserByName(String name, HashMap<String, HashMap<String, T>> nestedNameLookup, DeansListSyncResult result) {
        if (name == null) { return null; }
        
        String userToFindLastName = stripAndLowerMatchableName(name, KeyType.LAST_NAME);
        HashMap<String, T> usersWithThisLastName = nestedNameLookup.get(userToFindLastName);

        if (usersWithThisLastName != null) {
            
            String userToFindFirstName = stripAndLowerMatchableName(name, KeyType.FIRST_NAME);
            T existingUser = null;
            
            if (usersWithThisLastName.size() == 1) {
                // exactly one user with this last name -- take it regardless of first name, but warn if no match
                existingUser = usersWithThisLastName.get(usersWithThisLastName.keySet().iterator().next());
                // now a sanity check because this is a fuzzy matching choice
                String existingUserFirstName = stripAndLowerMatchableName(existingUser.getName(), KeyType.FIRST_NAME);
                if (existingUserFirstName == null ||
                        !existingUserFirstName.equals(userToFindFirstName)) {
                    String deanslistName = userToFindFirstName + " " + userToFindLastName;
                    String existingName = existingUserFirstName + " " + userToFindLastName;
                    if (existingUser instanceof Student) {
                        result.incrementBehaviorEventsMatchedStudentLastButNotFirst(deanslistName, existingName);
                    //TODO I'm not super sure what this is used for, will this get messed up? -Chris
                    } else if (existingUser instanceof Staff) {
                        if (((Staff) existingUser).getIsTeacher()) {
                            result.incrementBehaviorEventsMatchedTeacherLastButNotFirst(deanslistName, existingName);
                        } else if (((Staff) existingUser).getIsAdmin()) {
                            result.incrementBehaviorEventsMatchedAdminLastButNotFirst(deanslistName, existingName);
                        }
                    }
                } else {
                    if (existingUser instanceof Student) {
                        result.incrementBehaviorEventsMatchedStudentLastAndFirst();
                    } else if (existingUser instanceof Staff) {
                        if (((Staff) existingUser).getIsTeacher()) {
                            result.incrementBehaviorEventsMatchedTeacherLastAndFirst();
                    } else if (((Staff) existingUser).getIsTeacher()) {
                        result.incrementBehaviorEventsMatchedAdminLastAndFirst();
                    }
                    }

                }
            } else if (usersWithThisLastName.size() > 1) {
                // more than one person with this last name -- match on first name too.
                existingUser = usersWithThisLastName.get(userToFindFirstName);
                if (existingUser == null) {
                    // most than one person with this last name and no first name match, must use lev distance for best guess
                    int bestLevDistanceSoFar = Integer.MAX_VALUE;
                    HashSet<T> usersWithBestLevDistance = new HashSet<>();
                    for (Map.Entry<String, T> entry : usersWithThisLastName.entrySet()) {
                        T user = entry.getValue();
                        String candidateFirstName = stripAndLowerMatchableName(user.getName(), KeyType.FIRST_NAME);
                        int curLevDistance = StringUtils.getLevenshteinDistance(candidateFirstName, userToFindFirstName);
                        if (curLevDistance < bestLevDistanceSoFar) {
                            bestLevDistanceSoFar = curLevDistance;
                            // blow away the set, this is a new low
                            usersWithBestLevDistance = new HashSet<>();
                            usersWithBestLevDistance.add(user);
                        } else if (curLevDistance == bestLevDistanceSoFar) {
                            // keep the set, these are all candidates with the same lev distance
                            usersWithBestLevDistance.add(user);
                        }
                    }
                    if (usersWithBestLevDistance.size() > 1) {
                        // still too many users, even with lev matching
                        String error = "More than one user with matching last name " + userToFindLastName
                                + " and with ambiguous match on first name (" + userToFindFirstName + ") with lev distance " + bestLevDistanceSoFar + ", candidate first names: ";
                        for (User user : usersWithBestLevDistance) {
                            error += stripAndLowerMatchableName(user.getName(), KeyType.FIRST_NAME) + " ";
                        }
                        LOGGER.error(error);
                    } else if (usersWithBestLevDistance.size() == 1) {
                        // one user has a better score than any other with lev matching, use it
                        T user = usersWithBestLevDistance.iterator().next();
                        String candidateName = stripAndLowerMatchableName(user.getName(), KeyType.FIRST_NAME_AND_LAST_NAME);
                        result.usersLevMatched.add(Pair.of(candidateName, userToFindFirstName + " " + userToFindLastName));
                        return user;
                    } else {
                        LOGGER.error("Programmer error -- usersWithBestLevDistance should never be empty");
                    }
                } else {
                    // we have matched the student firstname lastname which is good enough for now! rejoice!
                    return existingUser;
                }

            } else {
                LOGGER.error("ERROR: empty hashmap exists for lastname " + userToFindLastName
                        + ", which should never happen. The DlEtlEngine isn't populating it correctly.");
            }
            return existingUser;
        }
        return null;
    } 
    
    @Override
    public SyncResult syncDistrict() {
        return syncDistrict(new EtlSettings());
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

            Student existingStudent = findUserByName(student.getName(), studentLastNameLookup, result);
            if (existingStudent != null) { 
                // student matched! migrate behavioral event
                behavior.setStudent(existingStudent);

                // don't require teacher but populate it if present
                if (assigner != null && assigner.getName() != null) {
                    Staff existingTeacher = findUserByName(assigner.getName(), teacherLastNameLookup, result);
                    if (existingTeacher != null) {
                        behavior.setAssigner(existingTeacher);
                        result.incrementBehaviorMatchedTeacher();
                    } else {
                        Staff existingAdmin = findUserByName(assigner.getName(), adminLastNameLookup, result);
                        if (existingAdmin != null) {
                            behavior.setAssigner(existingAdmin);
                            result.incrementBehaviorMatchedAdmin();
                        } else {
                            // null out the teacher that cannot be associated or we will get an error when submitting
                            // (we would need to create this teacher, and DL sync only creates behavior events)
                            behavior.setAssigner(null);
                            result.incrementUnmatchedAssigner(assigner.getName());
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
                        LOGGER.info("HttpClientException caught within DLETL Engine trying to create behavior with remote behavior ID " + behavior.getRemoteBehaviorId());
                    }
                    // ... and save in cache
                    if (createdBehavior != null) { 
                        studentBehaviorEvents.put(createdBehavior.getRemoteBehaviorId(), createdBehavior);
                        result.incrementBehaviorAdded();
                    } else {
                        LOGGER.warn("Unable to create behavior with remote behavior ID " + behavior.getRemoteBehaviorId());
                        result.incrementBehaviorFailed();
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
                LOGGER.info("Unable to match to student specified by deanslist: " + student.getName());
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
                lookup.put(stripAndLowerMatchableName(entryName,KeyType.FIRST_NAME_AND_LAST_NAME), entry);
            }
        }
        return lookup;
    }

    private <T extends ApiModel> HashMap<String, HashMap<String, T>> populateNestedNameLookup(Collection<T> collection) {
        HashMap<String, HashMap<String, T>> lastNameLookup = new HashMap<>();
        for (T entry : collection) {
            String entryName = entry.getName();
            if (entryName != null) {
                String lastNameKeyString = stripAndLowerMatchableName(entryName,KeyType.LAST_NAME);
                HashMap<String, T> firstNameLookupForOneLastName = lastNameLookup.get(lastNameKeyString);

                // - if there is no existing hashmap for this person's last name...
                //     - create a map for students with this last name
                //     - add the map of students with this last name to map of all last names, using last name as key
                // - then, either way...
                // - add this current student to the map of students with this last name, using first name as key (although warn if they already exist)


                if (firstNameLookupForOneLastName == null) {
                    // hashmap for this last name doesn't exist, so create it
                    firstNameLookupForOneLastName = new HashMap<String, T>();
                    // 
                    lastNameLookup.put(stripAndLowerMatchableName(entryName, KeyType.LAST_NAME), firstNameLookupForOneLastName);
                } else {
                    // first name map for this specific last name already exists... any action needed?
                }
                String firstNameKeyString = stripAndLowerMatchableName(entryName, KeyType.FIRST_NAME);
                if (firstNameLookupForOneLastName.get(firstNameKeyString) != null) {
                    //
                    LOGGER.error("ERROR - trying to populate lookup but can't add duplicate student with name "
                            + firstNameKeyString + " " + lastNameKeyString);
                    LOGGER.error("NOTE -- may need to implement matching on middle name, if provided");
                } else {
                    firstNameLookupForOneLastName.put(firstNameKeyString, entry);
                }
            } else {
                // warning? -- no name, cannot save in hashmap
                LOGGER.warn("WARN - populatedNestedNameLookup cannot get name for entry, ignoring...");
            }
        }
        return lastNameLookup;
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

    private enum KeyType { 
        FIRST_NAME, // key is first word in name
        LAST_NAME, // key is last word in name
        FIRST_NAME_AND_LAST_NAME,   // key is first word and last word in name
        WHOLE_NAME  // key is entire name, whatever may be in it
    }
    
    // TODO: hack to get students matching until first/middle/last can be fetched seperately. Today nina's school puts "Nmh" or a middle initial
    // for a student's middle name in a lot of the deanslist behavioral records. This method takes in a raw
    // student name (which can contain first, middle, last, spaces, etc) and returns a lowercased version 
    // with whitespace characters stripped. Depending on the KeyType, the returned string may be further transformed,
    // e.g. stripping away the middle name.
    //
    // Should probably refactor DB to store 
    // first+middle+last separately before cleaning this up.
    private String stripAndLowerMatchableName(String name, KeyType keyType) {
        if (null == name) { return null; }

        String matchableName = null;
        
        // may need a set of these replace characters. For now, it appears that deanslist has begun stripping this out
        // of student (last? all?) names, while powerschool does not - leading to a failure to match. 
        // Since we don't know where the dashes should be in the Deanslist data, just strip them out and see if the dashless
        // strings match.
        name = name.replace("-", "");
        String[] nameWords = name.trim().split("\\s+");
        if (nameWords.length == 0) { matchableName = ""; }                   // no name, keytype doesn't matter
        if (nameWords.length >= 1) {
            switch (keyType) {
                case FIRST_NAME:
                    matchableName = nameWords[0];
                    break;
                case LAST_NAME:
                    matchableName = nameWords[nameWords.length - 1];
                    break;
                case FIRST_NAME_AND_LAST_NAME:
                    if (nameWords.length >= 2) {
                        matchableName = nameWords[0] + " " + nameWords[nameWords.length - 1];
                    } else {
                        // first and last name requested but only one name found. use it.
                        matchableName = nameWords[0];
                    }
                    break;
                case WHOLE_NAME:
                    matchableName = name;
                    break;
                default:
                    throw new RuntimeException("unrecognized KeyType in stripAndLowerMatchable name");
            }
        }
        return stripAndLowerName(matchableName);
    }
    
    private List<Behavior> getBehaviorData() {
        BehaviorResponse response = deansList.getBehaviorData();
        return new ArrayList<>(response.toInternalModel());
    }
    
}
