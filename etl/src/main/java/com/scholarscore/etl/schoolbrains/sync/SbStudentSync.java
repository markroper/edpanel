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

    public SbStudentSync(ISchoolBrainsClient schoolBrains,
                         IAPIClient edPanel) {
        super(schoolBrains, edPanel);
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
    protected Collection<Student> fetchSourceRecords() throws HttpClientException {
        return schoolBrains.getStudents();
    }

    @Override
    protected Collection<Student> fetchEdPanelRecords() throws HttpClientException {
        return edPanel.getAllStudents();
    }
}
