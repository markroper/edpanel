package com.scholarscore.etl.kickboard;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.IEtlEngine;
import com.scholarscore.etl.SyncResult;
import com.scholarscore.etl.powerschool.sync.associator.StaffAssociator;
import com.scholarscore.etl.powerschool.sync.associator.StudentAssociator;
import com.scholarscore.etl.runner.EtlSettings;
import com.scholarscore.models.Behavior;
import com.scholarscore.models.behavior.BehaviorScore;
import com.scholarscore.models.user.Person;
import com.scholarscore.models.user.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.Month;
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
    private static final Integer CHUNK_SIZE = 2000;
    private LocalDate CUTOFF;
    private KickboardSyncResult result = new KickboardSyncResult();

    @Override
    public SyncResult syncDistrict(EtlSettings settings) {
        syncPoints();
        syncBehavior();

        //Get the most recent July 1 in the past, thats our cutoff date.
        LocalDate now = LocalDate.now();
        int currYear = now.getYear();
        if(now.getMonth().getValue() <= Month.JULY.getValue() ) {
            currYear = currYear - 1;
        }
        CUTOFF = LocalDate.of(currYear, 7, 1);
        kickboardClient.close();
        return result;
    }

    private void syncBehavior() {
        List<KickboardBehavior> kbBehaviors =  kickboardClient.getBehaviorData(CHUNK_SIZE);
        //Source system behavior ID to behavior
        Map<String, Behavior> edPanelBehaviors = new HashMap<>();
        Set<Long> studentsWithResolvedBehaviors = new HashSet<>();
        int page = 1;
        while(kbBehaviors != null) {
            LOGGER.debug("PAGE NUMBER: " + page);
            page++;
            kbBehaviors = kickboardClient.getBehaviorData(CHUNK_SIZE);
            List<Behavior> sourceBehaviors = convertToEdPanelBehaviors(kbBehaviors);
            if(null == sourceBehaviors) {
                continue;
            }
            List<Behavior> behaviorsToCreate = new ArrayList<>();
            for(Behavior source: sourceBehaviors) {
                //If the current behavior from Kickboard's user's data hasn't been pulled from EdPanel, pull it.
                if(!studentsWithResolvedBehaviors.contains(source.getStudent().getId())) {
                    try {
                        Collection<Behavior> studentsBehaviors =
                                scholarScore.getBehaviors(source.getStudent().getId(), CUTOFF);
                        studentsWithResolvedBehaviors.add(source.getStudent().getId());
                        for(Behavior b : studentsBehaviors) {
                            edPanelBehaviors.put(b.getRemoteBehaviorId(), b);
                        }
                    } catch (HttpClientException e) {
                        LOGGER.error("Unable to resolve EdPanel behaviors for student with ID: " +
                                source.getStudent().getId());
                    }
                }
                //If the behavior doesn't exist in edpanel, create it, otherwise update it
                if(!edPanelBehaviors.containsKey(source.getRemoteBehaviorId())) {
                    //create the behavior
                    behaviorsToCreate.add(source);
                } else {
                    //update the behavior
                    updateBehavior(edPanelBehaviors.get(source.getRemoteBehaviorId()), source);
                    //Then remove it from the set so we know what to delete when we're done...
                    edPanelBehaviors.remove(source.getRemoteBehaviorId());
                }
            }
            createBehaviors(behaviorsToCreate);
        }
        // Now that we've handled the entire giant file, go ahead and delete from EdPanel any entries left in the
        // edpanel collection that were not updated and removed above.
        for(Map.Entry<String, Behavior> entry: edPanelBehaviors.entrySet()) {
            try {
                Behavior b = entry.getValue();
                scholarScore.deleteBehaviorBySourceId(b.getStudent().getId(), b.getRemoteBehaviorId());
                result.addDeleted(1);
            } catch (HttpClientException e) {
                result.addFailedDeleted(1);
                LOGGER.warn("Unable to delete the behavior event within EdPanel with SSID: " +
                        entry.getValue().getRemoteBehaviorId());
            }
        }
    }

    private void syncPoints() {
        List<BehaviorScore> sourceScores = kickboardClient.getBehaviorScore(CHUNK_SIZE);
        Map<Long, Set<LocalDate>> studentIdToScoreDateSet = new HashMap<>();
        Map<Long, Set<BehaviorScore>> studentIdToScoreSet = new HashMap<>();
        Set<Long> studentsWithResolvedScores = new HashSet<>();
        HashMap<String, Student> sourceSystemUserIdToStudent = new HashMap<>();
        for(Map.Entry<Long, Student> entry: studentAssociator.getUsers().entrySet()) {
            sourceSystemUserIdToStudent.put(entry.getValue().getSourceSystemUserId(), entry.getValue());
        }
        while(null != sourceScores) {
            List<BehaviorScore> scoresToCreate = new ArrayList<>();
            for(BehaviorScore score: sourceScores) {
                //Resolve the correct student and set it on the source:
                Student stud = sourceSystemUserIdToStudent.get(score.getStudent().getSourceSystemUserId());
                if(null == stud) {
                    LOGGER.info("Unable to resolve the student within EdPanel for: " + score.toString());
                    continue;
                }
                score.setStudent(stud);
                //If the current behavior from Kickboard's user's data hasn't been pulled from EdPanel, pull it.
                if(!studentsWithResolvedScores.contains(stud.getId())) {
                    try {
                        Collection<BehaviorScore> edPanelScores =
                                scholarScore.getBehaviorScores(stud.getId(), CUTOFF);
                        studentsWithResolvedScores.add(stud.getId());
                        for(BehaviorScore b : edPanelScores) {
                            Long studentId = b.getStudent().getId();
                            if(!studentIdToScoreDateSet.containsKey(studentId)) {
                                studentIdToScoreDateSet.put(studentId, new HashSet<>());
                                studentIdToScoreSet.put(studentId, new HashSet<>());
                            }
                            studentIdToScoreDateSet.get(studentId).add(b.getDate());
                            studentIdToScoreSet.get(studentId).add(b);
                        }
                    } catch (HttpClientException e) {
                        LOGGER.error("Unable to resolve EdPanel weekly points for student with ID: " +
                                score.getStudent().getId());
                    }
                    //If the behavior doesn't exist in edpanel, create it, otherwise update it
                    if(!studentIdToScoreDateSet.containsKey(stud.getId()) ||
                            !studentIdToScoreDateSet.get(stud.getId()).contains(score.getDate())) {
                        //create the behavior
                        scoresToCreate.add(score);
                    } else {
                        BehaviorScore oldScore = null;
                        Set<BehaviorScore> scoresForStudent = studentIdToScoreSet.get(stud.getId());
                        for(BehaviorScore s: scoresForStudent) {
                            if(s.getDate().equals(score.getDate())) {
                                oldScore = s;
                                break;
                            }
                        }
                        //update the behavior
                        updateBehaviorScore(oldScore, score);
                        //Then remove it from the set so we know what to delete when we're done...
                        studentIdToScoreSet.get(stud.getId()).remove(oldScore);
                        studentIdToScoreDateSet.get(stud.getId()).remove(score.getDate());
                    }
                }
            }
            createBehaviorScores(scoresToCreate);
            sourceScores = kickboardClient.getBehaviorScore(CHUNK_SIZE);
        }
        for(Map.Entry<Long, Set<LocalDate>> entry: studentIdToScoreDateSet.entrySet()) {
            try {
                for(LocalDate d: entry.getValue()) {
                    scholarScore.deleteBehaviorScore(entry.getKey(), d);
                }
            } catch (HttpClientException e) {
                LOGGER.warn("Unable to delete the behavior score within EdPanel for student ID: " +
                        entry.getKey() + " for reason: " + e.getMessage());
            }
        }
    }

    public void updateBehaviorScore(BehaviorScore oldBehavior, BehaviorScore newBehavior) {
        newBehavior.setId(oldBehavior.getId());
        if(!newBehavior.equals(oldBehavior)) {
            try {
                scholarScore.updateBehaviorScore(newBehavior.getStudent().getId(), newBehavior.getDate(), newBehavior);
            } catch (HttpClientException e) {
                LOGGER.warn("Unable to update the behavior score within EdPanel: " + newBehavior.toString());
            }
        }
    }

    public void updateBehavior(Behavior oldBehavior, Behavior newBehavior) {
        newBehavior.setId(oldBehavior.getId());
        if(!newBehavior.equals(oldBehavior)) {
            try {
                scholarScore.updateBehavior(newBehavior.getStudent().getId(), newBehavior.getId(), newBehavior);
                result.addUpdated(1);
            } catch (HttpClientException e) {
                result.addFailedToUpdate(1);
                LOGGER.warn("Unable to update the behavior within EdPanel: " + newBehavior.toString());
            }
        }
    }
    public void createBehaviorScores(List<BehaviorScore> b) {
        if(null != b && b.size() > 0) {
            try {
                scholarScore.createBehaviorScores(b);
            } catch (HttpClientException e) {
                LOGGER.warn("Failed to create behavior scores within EdPanel: " + b.toString());
            }
        }
    }

    public void createBehaviors(List<Behavior> b) {
        if(null != b && b.size() > 0) {
            try {
                scholarScore.createBehaviors(b);
                result.addCreated(b.size());
            } catch (HttpClientException e) {
                result.addFailedToCreate(b.size());
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
