package com.scholarscore.etl.powerschool.sync.user;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.ISync;
import com.scholarscore.etl.PowerSchoolSyncResult;
import com.scholarscore.etl.powerschool.api.model.PsStaffs;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.etl.powerschool.sync.associator.StaffAssociator;
import com.scholarscore.models.Address;
import com.scholarscore.models.School;
import com.scholarscore.models.user.Staff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by markroper on 10/26/15.
 */
public class StaffSync implements ISync<Staff> {
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
    public ConcurrentHashMap<Long, Staff> syncCreateUpdateDelete(PowerSchoolSyncResult results) {
        ConcurrentHashMap<Long, Staff> sourceStaff = null;
        try {
            sourceStaff = resolveAllFromSourceSystem();
        } catch (HttpClientException e) {
            try {
                sourceStaff = resolveAllFromSourceSystem();
            } catch (HttpClientException ex) {
                LOGGER.error("Unable to fetch staff from PowerSchool for school " + school.getName() +
                        " with ID: " + school.getId());
                results.staffSourceGetFailed(Long.valueOf(
                        school.getSourceSystemId()),
                        school.getId());
                return new ConcurrentHashMap<>();
            }
        }
        ConcurrentHashMap<Long, Staff> ed = null;
        try {
            ed = resolveFromEdPanel();
        } catch (HttpClientException e) {
            try {
                ed = resolveFromEdPanel();
            } catch (HttpClientException ex) {
                LOGGER.error("Unable to fetch staff from EdPanel for school " + school.getName() +
                        " with ID: " + school.getId());
                results.staffEdPanelGetFailed(Long.valueOf(
                                school.getSourceSystemId()),
                        school.getId());
                return new ConcurrentHashMap<>();
            }
        }
        //Find & perform the inserts and updates, if any
        for (Map.Entry<Long, Staff> entry : sourceStaff.entrySet()) {
            Staff sourceStaffer = entry.getValue();
            Staff edPanelUser = ed.get(entry.getKey());
            if (null == edPanelUser) {
                Long ssid = Long.valueOf(sourceStaffer.getSourceSystemId());
                sourceStaffer.setCurrentSchoolId(school.getId());
                final Staff created;
                try {
                    if (sourceStaffer.getIsAdmin()) {
                        created = edPanel.createAdministrator(sourceStaffer);
                    } else if (sourceStaffer.getIsTeacher()) {
                        created = edPanel.createTeacher(sourceStaffer);
                    } else {
                        created = edPanel.createTeacher(sourceStaffer);
                    }
                } catch (HttpClientException e) {
                    results.staffCreateFailed(ssid);
                    continue;
                }
                sourceStaffer.setId(created.getId());
                staffAssociator.add(ssid, created);
                results.staffCreated(entry.getKey(), created.getId());
            } else {
                Long ssid = Long.valueOf(sourceStaffer.getSourceSystemId());
                sourceStaffer.setId(edPanelUser.getId());
                sourceStaffer.setCurrentSchoolId(school.getId());
                sourceStaffer.setSourceSystemId(edPanelUser.getSourceSystemId());
                sourceStaffer.setUsername(edPanelUser.getUsername());
                sourceStaffer.setEnabled(edPanelUser.getEnabled());
                edPanelUser.setPassword(null);
                Address add = edPanelUser.getHomeAddress();
                if (null != add && null != sourceStaffer.getHomeAddress()) {
                    sourceStaffer.getHomeAddress().setId(add.getId());
                }
                if (!edPanelUser.equals(sourceStaffer)) {
                    try {
                        sourceStaffer = (Staff)edPanel.replaceUser(sourceStaffer);
                    } catch (HttpClientException e) {
                        if (null != sourceStaffer.getId()) {
                            results.staffUpdateFailed(entry.getKey(), sourceStaffer.getId());
                        }
                        continue;
                    }
                    results.staffUpdated(entry.getKey(), sourceStaffer.getId());
                }
                staffAssociator.add(ssid, sourceStaffer);
            }
        }
        //Note: we never delete users, even if they're removed from the source system.
        return sourceStaff;
    }
    
    protected ConcurrentHashMap<Long, Staff> resolveAllFromSourceSystem() throws HttpClientException {
        PsStaffs response = powerSchool.getStaff(Long.valueOf(school.getSourceSystemId()));
        List<Staff> apiListOfStaff = response.toInternalModel();
        ConcurrentHashMap<Long, Staff> source = new ConcurrentHashMap<>();
        for(Staff u : apiListOfStaff) {
            source.put(Long.valueOf(u.getSourceSystemUserId()), u);
        }
        return source;
    }

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
}
