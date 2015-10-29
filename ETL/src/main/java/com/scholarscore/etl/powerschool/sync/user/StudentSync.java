package com.scholarscore.etl.powerschool.sync.user;

import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.powerschool.api.model.PsStudents;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.etl.powerschool.sync.ISync;
import com.scholarscore.etl.powerschool.sync.associator.StudentAssociator;
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
    protected StudentAssociator studentAssociator;

    public StudentSync(IAPIClient edPanel,
                       IPowerSchoolClient powerSchool,
                       School s,
                       StudentAssociator studentAssociator) {
        this.edPanel = edPanel;
        this.powerSchool = powerSchool;
        this.school = s;
        this.studentAssociator = studentAssociator;
    }

    @Override
    public ConcurrentHashMap<Long, Student> synchCreateUpdateDelete() {
        Long psSchoolId = new Long(school.getSourceSystemId());
        ConcurrentHashMap<Long, Student> sourceStudents = resolveAllFromSourceSystem();
        ConcurrentHashMap<Long, Student> ed = resolveFromEdPanel();
        Iterator<Map.Entry<Long, Student>> sourceIterator = sourceStudents.entrySet().iterator();
        //Find & perform the inserts and updates, if any
        while(sourceIterator.hasNext()) {
            Map.Entry<Long, Student> entry = sourceIterator.next();
            Student sourceUser = entry.getValue();
            Student edPanelUser = ed.get(entry.getKey());
            //Associate the SSID and source system local id (teacher/admin ID and underlying user ID)
            Long ssid = Long.valueOf(sourceUser.getSourceSystemId());
            Long underlyingUserId = Long.valueOf(((Person) sourceUser).getSourceSystemUserId());
            studentAssociator.associateIds(ssid, underlyingUserId);
            if(null == edPanelUser) {
                edPanelUser = studentAssociator.findByOtherId(underlyingUserId);
            }
            if(null == edPanelUser){
                sourceUser.setCurrentSchoolId(school.getId());
                User created = edPanel.createUser(sourceUser);
                sourceUser.setId(created.getId());
            } else {
                sourceUser.setId(edPanelUser.getId());
                sourceUser.setCurrentSchoolId(school.getId());
                sourceUser.setSourceSystemId(edPanelUser.getSourceSystemId());
                sourceUser.setUsername(edPanelUser.getUsername());
                edPanelUser.setPassword(null);
                Address add = sourceUser.getHomeAddress();
                if(null != add) {
                    add.setId(edPanelUser.getHomeAddress().getId());
                }
                add = sourceUser.getMailingAddress();
                if(null != add) {
                    add.setId(edPanelUser.getMailingAddress().getId());
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

    protected ConcurrentHashMap<Long, Student> resolveFromEdPanel() {
        Collection<Student> users = edPanel.getStudents(null);
        ConcurrentHashMap<Long, Student> userMap = new ConcurrentHashMap<>();
        for(Student u: users) {
            Long id = null;
            String sourceSystemUserId = u.getSourceSystemUserId();
            if(null != sourceSystemUserId) {
                id = Long.valueOf(sourceSystemUserId);
                userMap.put(id, u);
            }
        }
        return userMap;
    }
}
