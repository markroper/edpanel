package com.scholarscore.etl.powerschool.sync;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.PowerSchoolSyncResult;
import com.scholarscore.etl.powerschool.api.model.student.PsStudents;
import com.scholarscore.etl.powerschool.api.response.PsStudentResponse;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.etl.powerschool.sync.associator.StudentAssociator;
import com.scholarscore.models.user.Student;

import java.util.Collection;

/**
 * Static utility used to ad-hoc migrate a student missed in the initial attempt to migrate all students in a district
 *
 * Created by markroper on 10/28/15.
 */
public class MissingStudentMigrator {

    /**
     * Sadly, it is possible for a student not returned by the PowerSchool API /schools/:id/students
     * to end up enrolled in a Section. In these cases, we need to go fetch the student and create
     * him/her/it/them ad hoc in edpanel in order to enroll them in the section.  Returns null if the
     * user cannot be retrieved from PowerSchool.
     * @param schoolId
     * @param powerSchoolStudentId
     */
    public static Student resolveMissingStudent(
            Long schoolId,
            Long powerSchoolStudentId,
            IPowerSchoolClient powerSchool,
            IAPIClient edPanel,
            StudentAssociator studentAssociator,
            PowerSchoolSyncResult results) {
        PsStudentResponse powerStudent = null;

        try {
            powerStudent = powerSchool.getStudentById(powerSchoolStudentId);
        } catch(HttpClientException e) {
            results.studentCreateFailed(powerSchoolStudentId);
            return null;
        }
        PsStudents students = new PsStudents();
        students.add(powerStudent.student);
        Collection<Student> studs = students.toInternalModel();
        for(Student edpanelStudent : studs) {
            edpanelStudent.setCurrentSchoolId(schoolId);
            Student resolvedStudent = null;
            try {
                resolvedStudent = edPanel.getStudent(
                        Long.valueOf(edpanelStudent.getSourceSystemId()));
            } catch (HttpClientException e) {
                //NO OP
            }
            try {
                if(null == resolvedStudent) {
                    resolvedStudent = edPanel.createStudent(edpanelStudent);
                    if(null != resolvedStudent) {
                        results.studentCreated(
                                Long.valueOf(resolvedStudent.getSourceSystemId()), resolvedStudent.getId());
                    }
                }
                Long otherId = Long.valueOf(resolvedStudent.getSourceSystemUserId());
                Long ssid = Long.valueOf(resolvedStudent.getSourceSystemId());
                studentAssociator.add(ssid, resolvedStudent);
            } catch(NumberFormatException | HttpClientException | NullPointerException e) {
                results.studentCreateFailed(Long.valueOf(edpanelStudent.getSourceSystemId()));
            }
            return resolvedStudent;
        }
        return null;
    }
}
