package com.scholarscore.etl.powerschool.sync.user;

import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.powerschool.api.model.PsStaffs;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.etl.powerschool.sync.ISync;
import com.scholarscore.etl.powerschool.sync.associator.StaffAssociator;
import com.scholarscore.models.Address;
import com.scholarscore.models.School;
import com.scholarscore.models.user.Administrator;
import com.scholarscore.models.user.Person;
import com.scholarscore.models.user.Teacher;
import com.scholarscore.models.user.User;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by markroper on 10/26/15.
 */
public class StaffSync implements ISync<Person> {
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
    public ConcurrentHashMap<Long, Person> syncCreateUpdateDelete() {
        Long psSchoolId = new Long(school.getSourceSystemId());
        ConcurrentHashMap<Long, Person> sourceStaff = resolveAllFromSourceSystem();
        ConcurrentHashMap<Long, Person> ed = resolveFromEdPanel();
        Iterator<Map.Entry<Long, Person>> sourceIterator = sourceStaff.entrySet().iterator();
        //Find & perform the inserts and updates, if any
        while(sourceIterator.hasNext()) {
            Map.Entry<Long, Person> entry = sourceIterator.next();
            User sourceUser = entry.getValue();
            //Associate the SSID and source system local id (teacher/admin ID and underlying user ID)
            Long ssid = Long.valueOf(sourceUser.getSourceSystemId());
            Long underlyingUserId = Long.valueOf(((Person) sourceUser).getSourceSystemUserId());
            staffAssociator.associateIds(ssid, underlyingUserId);

            User edPanelUser = ed.get(entry.getKey());
            if(null == edPanelUser) {
                edPanelUser = staffAssociator.findByOtherId(underlyingUserId);
            }
            if(null == edPanelUser){
                ((Person) sourceUser).setCurrentSchoolId(school.getId());
                User created = edPanel.createUser(sourceUser);
                sourceUser.setId(created.getId());
            } else {
                sourceUser.setId(edPanelUser.getId());
                ((Person) sourceUser).setCurrentSchoolId(school.getId());
                sourceUser.setSourceSystemId(edPanelUser.getSourceSystemId());
                sourceUser.setUsername(edPanelUser.getUsername());
                edPanelUser.setPassword(null);
                Address add = ((Person)edPanelUser).getHomeAddress();
                if(null != add) {
                    add.setId(null);
                }
                if(!edPanelUser.equals(sourceUser)) {
                    edPanel.replaceUser(sourceUser);
                }
            }
        }
        //Note: we never delete users, even if they're removed from the source system.
        return sourceStaff;
    }

    @SuppressWarnings("unchecked")
    protected ConcurrentHashMap<Long, Person> resolveAllFromSourceSystem() {
        PsStaffs response = powerSchool.getStaff(Long.valueOf(school.getSourceSystemId()));
        List<User> apiListOfStaff = response.toInternalModel();
        ConcurrentHashMap<Long, Person> source = new ConcurrentHashMap<>();
        for(User u : apiListOfStaff) {
            source.put(Long.valueOf(((Person) u).getSourceSystemUserId()), (Person)u);
        }
        return source;
    }

    protected ConcurrentHashMap<Long, Person> resolveFromEdPanel() {
        Collection<Teacher> users = edPanel.getTeachers();
        Collection<Administrator> admins = edPanel.getAdministrators();

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
