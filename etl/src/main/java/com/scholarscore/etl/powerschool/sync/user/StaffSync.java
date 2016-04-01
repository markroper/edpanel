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
import com.scholarscore.models.user.Person;
import com.scholarscore.models.user.Staff;
import com.scholarscore.models.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by markroper on 10/26/15.
 */
public class StaffSync implements ISync<Person> {
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
    public ConcurrentHashMap<Long, Person> syncCreateUpdateDelete(PowerSchoolSyncResult results) {
        ConcurrentHashMap<Long, Person> sourceStaff = null;
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
        ConcurrentHashMap<Long, Person> ed = null;
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
        Iterator<Map.Entry<Long, Person>> sourceIterator = sourceStaff.entrySet().iterator();
        //Find & perform the inserts and updates, if any
        while(sourceIterator.hasNext()) {
            Map.Entry<Long, Person> entry = sourceIterator.next();
            User sourceUser = entry.getValue();
            //Associate the SSID and source system local id (teacher/admin ID and underlying user ID)
            Long ssid = Long.valueOf(sourceUser.getSourceSystemId());
            Long underlyingUserId = Long.valueOf(((Person) sourceUser).getSourceSystemUserId());
            User edPanelUser = ed.get(entry.getKey());
            if(null == edPanelUser){
                ((Staff) sourceUser).setCurrentSchoolId(school.getId());
                final Staff created;
                try {
                    if(((Staff) sourceUser).getIsAdmin()) {
                        created = edPanel.createAdministrator((Staff)sourceUser);

                    } else if (((Staff)sourceUser).getIsTeacher()) {
                        created = edPanel.createTeacher((Staff)sourceUser);
                    } else {
                        created = edPanel.createTeacher((Staff)sourceUser);
                    }
                } catch (HttpClientException e) {
                    results.staffCreateFailed(ssid);
                    continue;
                }
                sourceUser.setId(created.getId());
                staffAssociator.add(ssid, created);
                results.staffCreated(entry.getKey(), created.getId());
            } else {
                sourceUser.setId(edPanelUser.getId());
                ((Person) sourceUser).setCurrentSchoolId(school.getId());
                sourceUser.setSourceSystemId(edPanelUser.getSourceSystemId());
                sourceUser.setUsername(edPanelUser.getUsername());
                sourceUser.setEnabled(edPanelUser.getEnabled());
                edPanelUser.setPassword(null);
                Address add = edPanelUser.getHomeAddress();
                if(null != add && null != sourceUser.getHomeAddress()) {
                    sourceUser.getHomeAddress().setId(add.getId());
                }
                if(!edPanelUser.equals(sourceUser)) {
                    try {
                        sourceUser = edPanel.replaceUser(sourceUser);
                    } catch (IOException e) {
                        if(null != sourceUser.getId()) {
                            results.staffUpdateFailed(entry.getKey(), sourceUser.getId());
                        }
                        continue;
                    }
                    results.staffUpdated(entry.getKey(), sourceUser.getId());
                }
                staffAssociator.add(ssid, (Staff)sourceUser);
            }
        }
        //Note: we never delete users, even if they're removed from the source system.
        return sourceStaff;
    }

    @SuppressWarnings("unchecked")
    protected ConcurrentHashMap<Long, Person> resolveAllFromSourceSystem() throws HttpClientException {
        PsStaffs response = powerSchool.getStaff(Long.valueOf(school.getSourceSystemId()));
        List<User> apiListOfStaff = response.toInternalModel();
        ConcurrentHashMap<Long, Person> source = new ConcurrentHashMap<>();
        for(User u : apiListOfStaff) {
            source.put(Long.valueOf(((Person) u).getSourceSystemUserId()), (Person)u);
        }
        return source;
    }

    protected ConcurrentHashMap<Long, Person> resolveFromEdPanel() throws HttpClientException {
        Collection<Staff> users = edPanel.getTeachers();
        Collection<Staff> admins = edPanel.getAdministrators();

        ConcurrentHashMap<Long, Person> userMap = new ConcurrentHashMap<>();
        for(Person u: users) {
            Long id = null;
            String systemUserId = u.getSourceSystemUserId();
            if(null != systemUserId) {
                id = Long.valueOf(systemUserId);
                userMap.put(id, u);
            }
        }
        for(Person u: admins) {
            Long id = null;
            String systemUserId = u.getSourceSystemUserId();
            if(null != systemUserId) {
                id = Long.valueOf(systemUserId);
                userMap.put(id, u);
            }
        }
        return userMap;
    }
}
