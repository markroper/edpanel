package com.scholarscore.etl.powerschool.sync.user;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.ISync;
import com.scholarscore.etl.PowerSchoolSyncResult;
import com.scholarscore.etl.powerschool.api.model.student.PsStudents;
import com.scholarscore.etl.powerschool.api.model.student.PsTableStudent;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.etl.powerschool.sync.SyncBase;
import com.scholarscore.etl.powerschool.sync.associator.StudentAssociator;
import com.scholarscore.models.Address;
import com.scholarscore.models.School;
import com.scholarscore.models.user.EnrollStatus;
import com.scholarscore.models.user.Student;
import org.apache.commons.lang3.tuple.MutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by markroper on 10/26/15.
 */
public class StudentSync extends SyncBase<Student> implements ISync<Student> {
    private final static Logger LOGGER = LoggerFactory.getLogger(StudentSync.class);
    protected IAPIClient edPanel;
    protected IPowerSchoolClient powerSchool;
    protected School school;
    protected StudentAssociator studentAssociator;
    protected Map<Long, MutablePair<String, String>> spedEll;
    protected Map<Long, PsTableStudent> ssidToHiddenTableFields;

    public StudentSync(IAPIClient edPanel,
                       IPowerSchoolClient powerSchool,
                       School s,
                       StudentAssociator studentAssociator,
                       Map<Long, MutablePair<String, String>> spedEll,
                       Map<Long, PsTableStudent> ssidToHiddenTableFields) {
        this.edPanel = edPanel;
        this.powerSchool = powerSchool;
        this.school = s;
        this.studentAssociator = studentAssociator;
        this.spedEll = spedEll;
        this.ssidToHiddenTableFields = ssidToHiddenTableFields;
    }

    @Override
    protected ConcurrentHashMap<Long, Student> resolveAllFromSourceSystem() throws HttpClientException {
        PsStudents response = powerSchool.getStudentsBySchool(Long.valueOf(school.getSourceSystemId()));
        Collection<Student> apiListOfStudents = response.toInternalModel();
        ConcurrentHashMap<Long, Student> source = new ConcurrentHashMap<>();
        Map<Long, Long> dcidToTableId = new HashMap<>();
        for(Map.Entry<Long, MutablePair<String, String>> entry : spedEll.entrySet()) {
            dcidToTableId.put(studentAssociator.findSsidFromTableId(entry.getKey()), entry.getKey());
        }
        for(Student u : apiListOfStudents) {
            MutablePair<String, String> se = spedEll.get(dcidToTableId.get(Long.parseLong(u.getSourceSystemId())));
            if(null != se) {
                //Match's 500 = no disability.  Excel's 00 = no disability
                u.setSped(!"00".equals(se.getLeft()) && !"500".equals(se.getLeft()));
                u.setEll(!"00".equals(se.getRight()));
            }
            PsTableStudent pStud = ssidToHiddenTableFields.get(Long.parseLong(u.getSourceSystemId()));
            if(null != pStud) {
                u.setStateStudentId(pStud.state_studentnumber);
                u.setCurrentGradeLevel(pStud.grade_level);
                //The enrollment status of the student. -2=Inactive, -1=Pre-registered, 0=Currently enrolled, 1=Inactive,
                // 2=Transferred out, 3=Graduated, 4=Imported as Historical, Any other value =Inactive. Indexed.
                switch(pStud.enroll_status) {
                    case -1:
                        u.setEnrollStatus(EnrollStatus.PRE_REGISTERED);
                        break;
                    case 0:
                        u.setEnrollStatus(EnrollStatus.CURRENTLY_ENROLLED);
                        break;
                    case 2:
                        u.setEnrollStatus(EnrollStatus.TRANSFERRED_OUT);
                        break;
                    case 3:
                        u.setEnrollStatus(EnrollStatus.GRADUATED);
                        break;
                    case 4:
                        u.setEnrollStatus(EnrollStatus.HISTORICAL_IMPORT);
                        break;
                    default:
                        u.setEnrollStatus(EnrollStatus.INACTIVE);
                        break;
                }
            }
            u.setCurrentSchoolId(school.getId());
            source.put(Long.valueOf(u.getSourceSystemUserId()), u);
        }
        return source;
    }

    @Override
    protected void handleSourceGetFailure(PowerSchoolSyncResult results) {
        LOGGER.error("Unable to fetch students from PowerSchool for school " + school.getName() +
                " with ID: " + school.getId());
        results.studentSourceGetFailed(Long.valueOf(school.getSourceSystemId()), school.getId());
    }

    @Override
    protected ConcurrentHashMap<Long, Student> resolveFromEdPanel() throws HttpClientException {
        Collection<Student> users = edPanel.getStudents(null);
        ConcurrentHashMap<Long, Student> userMap = new ConcurrentHashMap<>();
        for(Student u: users) {
            String sourceSystemUserId = u.getSourceSystemUserId();
            if(null != sourceSystemUserId) {
                userMap.put(Long.valueOf(sourceSystemUserId), u);
            }
        }
        return userMap;
    }

    @Override
    protected void handleEdPanelGetFailure(PowerSchoolSyncResult results) {
        LOGGER.error("Unable to fetch students from EdPanel for school " + school.getName() +
                " with ID: " + school.getId());
        results.studentEdPanelGetFailed(Long.valueOf(school.getSourceSystemId()), school.getId());
    }

    @Override
    protected void createEdPanelRecord(Student entityToSave, PowerSchoolSyncResult results) {
        Long ssid = Long.valueOf(entityToSave.getSourceSystemId());
        entityToSave.setCurrentSchoolId(school.getId());
        try {
            Student created = edPanel.createStudent(entityToSave);
            studentAssociator.add(ssid, created);
            results.studentCreated(ssid, created.getId());
        } catch (HttpClientException e) {
            results.studentCreateFailed(ssid);
        }
    }

    @Override
    protected void updateEdPanelRecord(Student sourceSystemEntity, Student edPanelEntity, PowerSchoolSyncResult results) {
        Long ssid = Long.valueOf(sourceSystemEntity.getSourceSystemId());
        sourceSystemEntity.setId(edPanelEntity.getId());
        sourceSystemEntity.setCurrentSchoolId(school.getId());
        sourceSystemEntity.setSourceSystemId(edPanelEntity.getSourceSystemId());
        sourceSystemEntity.setUsername(edPanelEntity.getUsername());
        sourceSystemEntity.setEnabled(edPanelEntity.getEnabled());
        edPanelEntity.setPassword(null);
        Address add = sourceSystemEntity.getHomeAddress();
        if(null != add && null != edPanelEntity.getHomeAddress()) {
            add.setId(edPanelEntity.getHomeAddress().getId());
        }
        add = sourceSystemEntity.getMailingAddress();
        if(null != add && edPanelEntity.getMailingAddress() != null) {
            add.setId(edPanelEntity.getMailingAddress().getId());
        }
        if(!edPanelEntity.equals(sourceSystemEntity)) {
            try {
                sourceSystemEntity = (Student)edPanel.replaceUser(sourceSystemEntity);
                results.studentUpdated(ssid, sourceSystemEntity.getId());
            } catch (HttpClientException e) {
                results.studentUpdateFailed(ssid, sourceSystemEntity.getId());
                return;
            }
        }
        studentAssociator.add(ssid, sourceSystemEntity);
    }

    @Override
    protected void deleteEdPanelRecord(Student entityToDelete, PowerSchoolSyncResult results) {
        //Withdraw any students not returned by source system
        if (school.getId().equals(entityToDelete.getCurrentSchoolId())) {
            Long ssid = Long.parseLong(entityToDelete.getSourceSystemId());
            try {
                entityToDelete.setWithdrawalDate(LocalDate.now());
                entityToDelete.setEnrollStatus(EnrollStatus.INACTIVE);
                edPanel.replaceUser(entityToDelete);
                LOGGER.info("Student withdrawn, student ID: " + entityToDelete.getId());
                results.studentUpdated(ssid, entityToDelete.getId());
            } catch (HttpClientException e) {
                results.studentUpdateFailed(ssid, entityToDelete.getId());
            }
            //Note: we never delete users, even if they're removed from the source system.
        }
    }
}
