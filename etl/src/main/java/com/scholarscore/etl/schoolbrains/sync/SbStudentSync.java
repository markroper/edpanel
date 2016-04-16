package com.scholarscore.etl.schoolbrains.sync;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.schoolbrains.client.ISchoolBrainsClient;
import com.scholarscore.models.School;
import com.scholarscore.models.user.EnrollStatus;
import com.scholarscore.models.user.Student;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by markroper on 4/15/16.
 */
public class SbStudentSync extends SchoolBrainsBaseSync<Student> {
    Long edPanelId;
    Map<String, School> schools;

    public SbStudentSync(ISchoolBrainsClient schoolBrains,
                         IAPIClient edPanel,
                         Long edPanelId,
                         Map<String, School> schools) {
        super(schoolBrains, edPanel);
        this.edPanelId = edPanelId;
        this.schools = schools;
    }

    @Override
    protected Student create(Student input) throws HttpClientException {
        return edPanel.createStudent(input);
    }

    @Override
    protected void updateIfNeeded(Student oldVal, Student newVal) {
        newVal.setId(oldVal.getId());
        if(!newVal.equals(oldVal)) {
            try {
                edPanel.updateStudent(newVal.getId(), newVal);
            } catch (HttpClientException e) {
                LOGGER.warn("Unable to update student: " + e.getMessage());
            }
        }
    }

    @Override
    protected void delete(Student oldVal) {
        //Never delete a student, just mark the student as inactive...
        //TODO: handle the case where the student has just changed schools...
        //TODO: handle the case where we know more about the enroll status than just INACTIVE.  E.G. expelled, etc.
        oldVal.setEnrollStatus(EnrollStatus.INACTIVE);
        oldVal.setWithdrawalDate(LocalDate.now());
        try {
            edPanel.updateStudent(oldVal.getId(), oldVal);
        } catch (HttpClientException e) {
            LOGGER.warn("Unable to update student, marking the student as withdrawn");
        }
    }

    @Override
    protected ConcurrentHashMap<String, Student> resolveFromEdPanel() throws HttpClientException {
        Collection<Student> students = edPanel.getStudents(edPanelId);
        ConcurrentHashMap<String, Student> edpanel = new ConcurrentHashMap<>();
        for(Student s: students) {
            edpanel.put(s.getSourceSystemId(), s);
        }
        return edpanel;
    }

    @Override
    protected ConcurrentHashMap<String, Student> resolveSourceSystem() throws HttpClientException {
        List<Student> source = schoolBrains.getStudents();
        ConcurrentHashMap<String, Student> sourceMap = new ConcurrentHashMap<>();

        for(Student s : source) {
            if(edPanelId.equals(schools.get(s.getCurrentSchoolId()).getId())) {
                sourceMap.put(s.getSourceSystemId(), s);
            }
        }
        return sourceMap;
    }
}
