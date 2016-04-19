package com.scholarscore.etl.powerschool.sync.user;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.ISync;
import com.scholarscore.etl.PowerSchoolSyncResult;
import com.scholarscore.etl.powerschool.api.model.PsStaffs;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.etl.powerschool.sync.SyncBase;
import com.scholarscore.etl.powerschool.sync.associator.StaffAssociator;
import com.scholarscore.models.Address;
import com.scholarscore.models.School;
import com.scholarscore.models.user.Staff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by markroper on 10/26/15.
 */
public class StaffSync extends SyncBase<Staff> implements ISync<Staff> {
    private final static Logger LOGGER = LoggerFactory.getLogger(StaffSync.class);
    protected IAPIClient edPanel;
    protected IPowerSchoolClient powerSchool;
    protected School school;
    protected StaffAssociator staffAssociator;

    public StaffSync(IAPIClient edPanel,
                     IPowerSchoolClient powerSchool,
                     School s,
                     StaffAssociator staffAssociator) {
        this.edPanel = edPanel;
        this.powerSchool = powerSchool;
        this.school = s;
        this.staffAssociator = staffAssociator;
    }
    
    @Override
    protected ConcurrentHashMap<Long, Staff> resolveAllFromSourceSystem() throws HttpClientException {
        PsStaffs response = powerSchool.getStaff(Long.valueOf(school.getSourceSystemId()));
        List<Staff> apiListOfStaff = response.toInternalModel();
        ConcurrentHashMap<Long, Staff> source = new ConcurrentHashMap<>();
        for(Staff u : apiListOfStaff) {
            source.put(Long.valueOf(u.getSourceSystemUserId()), u);
        }
        return source;
    }

    @Override
    protected void handleSourceGetFailure(PowerSchoolSyncResult results) {
        LOGGER.error("Unable to fetch staff from PowerSchool for school " + school.getName() +
                " with ID: " + school.getId());
        results.staffSourceGetFailed(Long.valueOf(
                        school.getSourceSystemId()),
                school.getId());
    }

    @Override
    protected ConcurrentHashMap<Long, Staff> resolveFromEdPanel() throws HttpClientException {
        Collection<Staff> teachers = edPanel.getTeachers();
        Collection<Staff> admins = edPanel.getAdministrators();

        ConcurrentHashMap<Long, Staff> userMap = new ConcurrentHashMap<>();
        for(Staff u: teachers) {
            String systemUserId = u.getSourceSystemUserId();
            if(null != systemUserId) {
                userMap.put(Long.valueOf(systemUserId), u);
            }
        }
        for(Staff u: admins) {
            String systemUserId = u.getSourceSystemUserId();
            if(null != systemUserId) {
                userMap.put(Long.valueOf(systemUserId), u);
            }
        }
        return userMap;
    }

    @Override
    protected void handleEdPanelGetFailure(PowerSchoolSyncResult results) {
        LOGGER.error("Unable to fetch staff from EdPanel for school " + school.getName() +
                " with ID: " + school.getId());
        results.staffEdPanelGetFailed(Long.valueOf(
                        school.getSourceSystemId()),
                school.getId());
    }

    @Override
    protected void createEdPanelRecord(Staff entityToSave, PowerSchoolSyncResult results) {
        Long ssid = Long.valueOf(entityToSave.getSourceSystemId());
        entityToSave.setCurrentSchoolId(school.getId());
        final Staff created;
        try {
            if (entityToSave.getIsAdmin()) {
                created = edPanel.createAdministrator(entityToSave);
            } else if (entityToSave.getIsTeacher()) {
                created = edPanel.createTeacher(entityToSave);
            } else {
                created = edPanel.createTeacher(entityToSave);
            }
            entityToSave.setId(created.getId());
            staffAssociator.add(ssid, created);
            results.staffCreated(ssid, created.getId());
        } catch (HttpClientException e) {
            results.staffCreateFailed(ssid);
        }
    }

    @Override
    protected void updateEdPanelRecord(Staff sourceSystemEntity, Staff edPanelEntity, PowerSchoolSyncResult results) {
        Long ssid = Long.valueOf(sourceSystemEntity.getSourceSystemId());
        sourceSystemEntity.setId(edPanelEntity.getId());
        sourceSystemEntity.setCurrentSchoolId(school.getId());
        sourceSystemEntity.setSourceSystemId(edPanelEntity.getSourceSystemId());
        sourceSystemEntity.setUsername(edPanelEntity.getUsername());
        sourceSystemEntity.setEnabled(edPanelEntity.getEnabled());
        edPanelEntity.setPassword(null);
        Address add = edPanelEntity.getHomeAddress();
        if (null != add && null != sourceSystemEntity.getHomeAddress()) {
            sourceSystemEntity.getHomeAddress().setId(add.getId());
        }
        if (!edPanelEntity.equals(sourceSystemEntity)) {
            try {
                sourceSystemEntity = (Staff)edPanel.replaceUser(sourceSystemEntity);
            } catch (HttpClientException e) {
                Long sourceSystemEntityId = -1L;
                if (null != sourceSystemEntity.getId()) { sourceSystemEntityId = sourceSystemEntity.getId(); }
                results.staffUpdateFailed(ssid, sourceSystemEntityId);
                return;
            }
            results.staffUpdated(ssid, sourceSystemEntity.getId());
        } else {
            results.staffUntouched(ssid, sourceSystemEntity.getId());
        }
        staffAssociator.add(ssid, sourceSystemEntity);
    }

    @Override
    protected void deleteEdPanelRecord(Staff entityToDelete, PowerSchoolSyncResult results) {
        //Note: we never delete users, even if they're removed from the source system.
        LOGGER.debug("ETL attempting to delete teacher with EdPanel Id " + entityToDelete.getId() + ", but StaffSync never removes teachers!");
    }
}
