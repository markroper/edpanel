package com.scholarscore.etl.powerschool.sync.user;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.ISync;
import com.scholarscore.etl.PowerSchoolSyncResult;
import com.scholarscore.etl.powerschool.api.model.student.PsStudents;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.etl.powerschool.sync.associator.StudentAssociator;
import com.scholarscore.models.Address;
import com.scholarscore.models.School;
import com.scholarscore.models.user.Person;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.User;
import org.apache.commons.lang3.tuple.MutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by markroper on 10/26/15.
 */
public class StudentSync implements ISync<Student> {
    private final static Logger LOGGER = LoggerFactory.getLogger(StudentSync.class);
    protected IAPIClient edPanel;
    protected IPowerSchoolClient powerSchool;
    protected School school;
    protected StudentAssociator studentAssociator;
    protected Map<Long, MutablePair<String, String>> spedEll;

    public StudentSync(IAPIClient edPanel,
                       IPowerSchoolClient powerSchool,
                       School s,
                       StudentAssociator studentAssociator,
                       Map<Long, MutablePair<String, String>> spedEll) {
        this.edPanel = edPanel;
        this.powerSchool = powerSchool;
        this.school = s;
        this.studentAssociator = studentAssociator;
        this.spedEll = spedEll;
    }

    @Override
    public ConcurrentHashMap<Long, Student> syncCreateUpdateDelete(PowerSchoolSyncResult results) {
        Long psSchoolId = new Long(school.getSourceSystemId());
        ConcurrentHashMap<Long, Student> sourceStudents = null;
        try {
            sourceStudents = resolveAllFromSourceSystem();
        } catch (HttpClientException e) {
            try {
                sourceStudents = resolveAllFromSourceSystem();
            } catch (HttpClientException ex) {
                LOGGER.error("Unable to fetch students from PowerSchool for school " + school.getName() +
                        " with ID: " + school.getId());
                results.studentSourceGetFailed(Long.valueOf(school.getSourceSystemId()), school.getId());
                return new ConcurrentHashMap<>();
            }
        }
        ConcurrentHashMap<Long, Student> ed = null;
        try {
            ed = resolveFromEdPanel();
        } catch (HttpClientException e) {
            try {
                ed = resolveFromEdPanel();
            } catch (HttpClientException ex) {
                LOGGER.error("Unable to fetch students from EdPanel for school " + school.getName() +
                        " with ID: " + school.getId());
                results.studentEdPanelGetFailed(Long.valueOf(school.getSourceSystemId()), school.getId());
                return new ConcurrentHashMap<>();
            }
        }
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
                User created = null;
                try {
                    created = edPanel.createStudent(sourceUser);
                } catch (HttpClientException e) {
                    results.studentCreateFailed(entry.getKey());
                    continue;
                }
                sourceUser.setId(created.getId());
                results.studentCreated(entry.getKey(), created.getId());
            } else {
                sourceUser.setId(edPanelUser.getId());
                sourceUser.setCurrentSchoolId(school.getId());
                sourceUser.setSourceSystemId(edPanelUser.getSourceSystemId());
                sourceUser.setUsername(edPanelUser.getUsername());
                sourceUser.setEnabled(edPanelUser.getEnabled());
                edPanelUser.setPassword(null);
                Address add = sourceUser.getHomeAddress();
                if(null != add && null != edPanelUser.getHomeAddress()) {
                    add.setId(edPanelUser.getHomeAddress().getId());
                }
                add = sourceUser.getMailingAddress();
                if(null != add && edPanelUser.getMailingAddress() != null) {
                    add.setId(edPanelUser.getMailingAddress().getId());
                }
                if(!edPanelUser.equals(sourceUser)) {
                    try {
                        edPanel.replaceUser(sourceUser);
                    } catch (IOException e) {
                        results.studentUpdateFailed(entry.getKey(), sourceUser.getId());
                        continue;
                    }
                    results.studentUpdated(entry.getKey(), sourceUser.getId());
                }
            }
        }

        //Withdraw any students not returned by source system
        Iterator<Map.Entry<Long, Student>> edpanelIterator = ed.entrySet().iterator();
        while(edpanelIterator.hasNext()) {
            Map.Entry<Long, Student> entry = edpanelIterator.next();
            if(school.getId().equals(entry.getValue().getCurrentSchoolId()) &&
                    !sourceStudents.containsKey(entry.getKey())) {
                try {
                    entry.getValue().setWithdrawalDate(LocalDate.now());
                    edPanel.replaceUser(entry.getValue());
                } catch (IOException e) {
                    results.studentUpdateFailed(entry.getKey(), entry.getValue().getId());
                    continue;
                }
                LOGGER.info("Student withdrawn, student ID: " + entry.getValue().getId());
                results.studentUpdated(entry.getKey(), entry.getValue().getId());
            }
        }

        //Note: we never delete users, even if they're removed from the source system.
        return sourceStudents;
    }

    protected ConcurrentHashMap<Long, Student> resolveAllFromSourceSystem() throws HttpClientException {
        PsStudents response = powerSchool.getStudentsBySchool(Long.valueOf(school.getSourceSystemId()));
        Collection<Student> apiListOfStaff = response.toInternalModel();
        ConcurrentHashMap<Long, Student> source = new ConcurrentHashMap<>();
        for(Student u : apiListOfStaff) {
            MutablePair<String, String> se = spedEll.get(Long.parseLong(u.getSourceSystemId()));
            if(null != se) {
                u.setSped(!"00".equals(se.getLeft()));
                u.setEll(!"00".equals(se.getRight()));
            }
            u.setCurrentSchoolId(school.getId());
            source.put(Long.valueOf(u.getSourceSystemUserId()), u);
        }
        return source;
    }

    protected ConcurrentHashMap<Long, Student> resolveFromEdPanel() throws HttpClientException {
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
