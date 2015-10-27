package com.scholarscore.etl.powerschool.sync;

import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.powerschool.api.model.PsStudents;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.models.Address;
import com.scholarscore.models.School;
import com.scholarscore.models.user.Person;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.User;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by markroper on 10/26/15.
 */
public class StudentSync implements ISync<Student> {
    protected IAPIClient edPanel;
    protected IPowerSchoolClient powerSchool;
    protected School school;
    protected ConcurrentHashMap<Long, Student> createdStudents;
    protected ConcurrentHashMap<Long, Long> ssidToLocalId;

    public StudentSync(IAPIClient edPanel,
                       IPowerSchoolClient powerSchool,
                       School s,
                       ConcurrentHashMap<Long, Student> createdStudents,
                       ConcurrentHashMap<Long, Long> ssidToLocalId) {
        this.edPanel = edPanel;
        this.powerSchool = powerSchool;
        this.school = s;
        this.createdStudents = createdStudents;
        this.ssidToLocalId = ssidToLocalId;
    }

    @Override
    public ConcurrentHashMap<Long, Student> synchCreateUpdateDelete() {
        Long psSchoolId = new Long(school.getSourceSystemId());
        ConcurrentHashMap<Long, Student> sourceStudents = resolveAllFromSourceSystem();
        ConcurrentHashMap<Long, User> ed = resolveFromEdPanel();
        Iterator<Map.Entry<Long, Student>> sourceIterator = sourceStudents.entrySet().iterator();
        //Find & perform the inserts and updates, if any
        while(sourceIterator.hasNext()) {
            Map.Entry<Long, Student> entry = sourceIterator.next();
            User sourceUser = entry.getValue();
            User edPanelUser = ed.get(entry.getKey());
            //Associate the SSID and source system local id (teacher/admin ID and underlying user ID)
            Long ssid = Long.valueOf(sourceUser.getSourceSystemId());
            Long underlyingUserId = Long.valueOf(((Person) sourceUser).getSourceSystemUserId());
            ssidToLocalId.put(ssid, underlyingUserId);

            if(null == edPanelUser) {
                edPanelUser = createdStudents.get(underlyingUserId);
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
                add = ((Student)edPanelUser).getMailingAddress();
                if(null != add) {
                    add.setId(null);
                }
                if(!edPanelUser.equals(sourceUser)) {
                    edPanel.replaceUser(sourceUser);
                }
            }
        }
        //Note: we never delete users, even if they're removed from the source system.
        return sourceStudents;
    }

    protected ConcurrentHashMap<Long, Student> resolveAllFromSourceSystem() {
        PsStudents response = powerSchool.getStudentsBySchool(Long.valueOf(school.getSourceSystemId()));
        Collection<Student> apiListOfStaff = response.toInternalModel();
        ConcurrentHashMap<Long, Student> source = new ConcurrentHashMap<>();
        for(Student u : apiListOfStaff) {
            u.setCurrentSchoolId(school.getId());
            source.put(Long.valueOf(u.getSourceSystemUserId()), u);
        }
        return source;
    }

    protected ConcurrentHashMap<Long, User> resolveFromEdPanel() {
        Collection<Student> users = edPanel.getStudents(null);
        ConcurrentHashMap<Long, User> userMap = new ConcurrentHashMap<>();
        for(User u: users) {
            Long id = null;
            String sourceSystemUserId = ((Person)u).getSourceSystemUserId();
            if(null != sourceSystemUserId) {
                id = Long.valueOf(sourceSystemUserId);
                userMap.put(id, u);
            }
        }
        return userMap;
    }
}
