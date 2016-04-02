package com.scholarscore.etl.kickboard;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.IEtlEngine;
import com.scholarscore.etl.SyncResult;
import com.scholarscore.etl.powerschool.sync.associator.StaffAssociator;
import com.scholarscore.etl.powerschool.sync.associator.StudentAssociator;
import com.scholarscore.etl.runner.EtlSettings;
import com.scholarscore.models.Behavior;
import com.scholarscore.models.user.Person;
import com.scholarscore.models.user.Student;
import org.apache.commons.lang3.tuple.MutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by markroper on 4/1/16.
 */
public class KickboardEtl implements IEtlEngine {
    private final static Logger LOGGER = LoggerFactory.getLogger(KickboardEtl.class);
    //Spring injected
    private KickboardClient kickboardClient;
    private IAPIClient scholarScore;
    private StudentAssociator studentAssociator;
    private StaffAssociator staffAssociator;
    private Boolean enabled;
    private static final Integer CHUNK_SIZE = 1000;
    private LocalDate CUTOFF = LocalDate.of(2015, 7, 1);

    @Override
    public SyncResult syncDistrict(EtlSettings settings) {

        List<KickboardBehavior> kbBehaviors =  kickboardClient.getBehaviorData(CHUNK_SIZE);
        //Student ID to a paid of sourceSystemBehaviorId to boolean
        //Boolean is true if the ssid has been encountered in the source system data, otherwise false
        //At the end of the run, we delete all entries that false because they exist in edpanel but not the source system
        Map<Long, Set<MutablePair<String, Boolean>>> studentToAllExistingSystemIds = new HashMap<>();
        int page = 1;
        while(kbBehaviors != null) {
            LOGGER.debug("PAGE NUMBER: " + page);
            page++;
            HashMap<String, Behavior> ssidToBehavior = new HashMap<>();
            Set<Long> studentBehaviorsFetched = new HashSet<>();
            kbBehaviors = kickboardClient.getBehaviorData(CHUNK_SIZE);
            List<Behavior> edpanelBehaviors = convertToEdPanelBehaviors(kbBehaviors);

            List<Behavior> behaviorsToCreate = new ArrayList<>();
            for(Behavior source: edpanelBehaviors) {
                //##########
                //Make sure the current behavior from KickBoard gets put in the map and marked as 'seen' in the source system
                // if the event was previously put in the map as unseen, remove the unseen entry.
                if(!studentToAllExistingSystemIds.containsKey(source.getStudent().getId())) {
                    studentToAllExistingSystemIds.put(source.getStudent().getId(), new HashSet<>());
                }
                Set<MutablePair<String, Boolean>> behaviors =
                        studentToAllExistingSystemIds.get(source.getStudent().getId());
                MutablePair<String, Boolean> unseen =
                        new MutablePair<>(source.getRemoteBehaviorId(), new Boolean(false));
                if(behaviors.contains(unseen)) {
                    behaviors.remove(unseen);
                }
                //##########
                behaviors.add(new MutablePair<>(source.getRemoteBehaviorId(), new Boolean(true)));
                if(ssidToBehavior.containsKey(source.getRemoteBehaviorId())) {
                    //UPDATE
                    updateBehavior(ssidToBehavior.get(source.getRemoteBehaviorId()), source);
                } else {
                    if(studentBehaviorsFetched.contains(source.getStudent().getId())) {
                        //CREATE
                        behaviorsToCreate.add(source);
                    } else {
                        try {
                            Collection<Behavior> studentsBehaviors = scholarScore.getBehaviors(source.getStudent().getId(), CUTOFF);
                            if(null != studentsBehaviors) {
                                for(Behavior b: studentsBehaviors) {
                                    //##########
                                    //Add the behaviors from EdPanel as unseen in the source system if they have not yet been seen
                                    if(!studentToAllExistingSystemIds.containsKey(source.getStudent().getId())) {
                                        studentToAllExistingSystemIds.put(source.getStudent().getId(), new HashSet<>());
                                    }
                                    behaviors = studentToAllExistingSystemIds.get(source.getStudent().getId());
                                    MutablePair<String, Boolean> seen =
                                            new MutablePair<>(source.getRemoteBehaviorId(), new Boolean(true));
                                    if(!behaviors.contains(seen)) {
                                        behaviors.add(new MutablePair<>(source.getRemoteBehaviorId(), new Boolean(false)));
                                    }
                                    //###########
                                    ssidToBehavior.put(b.getRemoteBehaviorId(), b);
                                }
                                if(ssidToBehavior.containsKey(source.getRemoteBehaviorId())) {
                                    //UPDATE
                                    updateBehavior(ssidToBehavior.get(source.getRemoteBehaviorId()), source);
                                } else {
                                    //CREATE
                                    behaviorsToCreate.add(source);
                                }
                            }
                        } catch (HttpClientException e) {
                            LOGGER.warn("Unable to resolve EdPanel behaviors for student with ID: " +
                                source.getStudent().getId());
                        }
                    }
                }
            }
            createBehaviors(behaviorsToCreate);

        }
        //Now that we've handled the entire giant file, go ahead and remove any entries from EdPanel
        //That we never encountered in the source system data set.
        for(Map.Entry<Long, Set<MutablePair<String, Boolean>>> entry: studentToAllExistingSystemIds.entrySet()) {
            for(MutablePair<String, Boolean> seenAndUnseen: entry.getValue()) {
                if(!seenAndUnseen.getRight()) {
                    //delete the behavior, it was not in the source system set
                    try {
                        scholarScore.deleteBehaviorBySourceId(entry.getKey(), seenAndUnseen.getLeft());
                    } catch (HttpClientException e) {
                        LOGGER.warn("Unable to delete the behavior event within EdPanel with SSID: " +
                                seenAndUnseen.getLeft());
                    }
                }
            }
        }
        kickboardClient.close();
        //TODO:return a SyncResult
        return null;
    }
    public void updateBehavior(Behavior oldBehavior, Behavior newBehavior) {
        newBehavior.setId(oldBehavior.getId());
        if(!newBehavior.equals(oldBehavior)) {
            try {
                scholarScore.updateBehavior(newBehavior.getStudent().getId(), newBehavior.getId(), newBehavior);
            } catch (HttpClientException e) {
                LOGGER.warn("Unable to update the behavior within EdPanel: " + newBehavior.toString());
            }
        }
    }

    public void createBehaviors(List<Behavior> b) {
        if(null != b && b.size() > 0) {
            try {
                scholarScore.createBehaviors(b);
            } catch (HttpClientException e) {
                LOGGER.warn("Failed to create behaviors within EdPanel: " + b.toString());
            }
        }
    }

    public List<Behavior> convertToEdPanelBehaviors(List<KickboardBehavior> sourceBehaviors) {
        if(null == sourceBehaviors) {
            return null;
        }
        HashMap<Long, Student> sourceSystemUserIdToStudent = new HashMap<>();
        for(Map.Entry<Long, Student> entry: studentAssociator.getUsers().entrySet()) {
            sourceSystemUserIdToStudent.put(Long.valueOf(entry.getValue().getSourceSystemUserId()), entry.getValue());
        }
        HashMap<String, List<Person>> firstNameToStaff = new HashMap<>();
        HashMap<String, List<Person>> lastNameToStaff = new HashMap<>();
        for(Map.Entry<Long, Person> entry: staffAssociator.getUsers().entrySet()) {
            Person p = entry.getValue();
            String[] names = p.getName().split(" ");
            if(null != names && names.length > 1) {
                if(!firstNameToStaff.containsKey(names[0])) {
                    firstNameToStaff.put(names[0], new ArrayList<>());
                }
                firstNameToStaff.get(names[0]).add(p);
                if(!lastNameToStaff.containsKey(names[names.length - 1])) {
                    lastNameToStaff.put(names[names.length - 1], new ArrayList<>());
                }
                lastNameToStaff.get(names[names.length - 1]).add(p);
            }
        }
        List<Behavior> edpanelBehaviors = new ArrayList<>();
        for(KickboardBehavior kB: sourceBehaviors) {
            Behavior b = kB.toApiModel(sourceSystemUserIdToStudent, firstNameToStaff, lastNameToStaff);
            if(null != b) {
                edpanelBehaviors.add(b);
            }
        }
        return edpanelBehaviors;
    }

    @Override
    public SyncResult syncDistrict() {
        return syncDistrict(new EtlSettings());
    }

    public KickboardClient getKickboardClient() {
        return kickboardClient;
    }

    public void setKickboardClient(KickboardClient kickboardClient) {
        this.kickboardClient = kickboardClient;
    }

    public StudentAssociator getStudentAssociator() {
        return studentAssociator;
    }

    public void setStudentAssociator(StudentAssociator studentAssociator) {
        this.studentAssociator = studentAssociator;
    }

    public IAPIClient getScholarScore() {
        return scholarScore;
    }

    public void setScholarScore(IAPIClient scholarScore) {
        this.scholarScore = scholarScore;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public StaffAssociator getStaffAssociator() {
        return staffAssociator;
    }

    public void setStaffAssociator(StaffAssociator staffAssociator) {
        this.staffAssociator = staffAssociator;
    }
}
