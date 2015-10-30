package com.scholarscore.etl.powerschool.sync;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.ISync;
import com.scholarscore.etl.SyncResult;
import com.scholarscore.etl.powerschool.api.model.attendance.PsAttendance;
import com.scholarscore.etl.powerschool.api.model.attendance.PsAttendanceWrapper;
import com.scholarscore.etl.powerschool.api.response.PsResponse;
import com.scholarscore.etl.powerschool.api.response.PsResponseInner;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.etl.powerschool.sync.associator.StudentAssociator;
import com.scholarscore.models.School;
import com.scholarscore.models.attendance.Attendance;
import com.scholarscore.models.attendance.SchoolDay;
import com.scholarscore.models.user.Student;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by markroper on 10/30/15.
 */
public class AttendanceSync implements ISync<Attendance> {
    protected IAPIClient edPanel;
    protected IPowerSchoolClient powerSchool;
    protected School school;
    protected StudentAssociator studentAssociator;
    protected ConcurrentHashMap<Long, SchoolDay> schoolDays;

    public AttendanceSync(IAPIClient edPanel,
                          IPowerSchoolClient powerSchool,
                          School s,
                          StudentAssociator studentAssociator,
                          ConcurrentHashMap<Long, SchoolDay> schoolDays) {
        this.edPanel = edPanel;
        this.powerSchool = powerSchool;
        this.school = s;
        this.studentAssociator = studentAssociator;
        this.schoolDays = schoolDays;
    }
    @Override
    public ConcurrentHashMap<Long, Attendance> syncCreateUpdateDelete(SyncResult results) {
        ConcurrentHashMap<Long, Attendance> response = new ConcurrentHashMap<>();
        Iterator<Map.Entry<Long, Student>> studentIterator = studentAssociator.getStudents().entrySet().iterator();
        while(studentIterator.hasNext()) {
            //TODO: thread me for speed? Move this loop up to ETLEngine and make this puppy a runnable?
            response.putAll(syncCreateUpdateDeleteOneStudent(studentIterator.next().getValue(), results));
        }
        return response;
    }

    protected ConcurrentHashMap<Long, Attendance> syncCreateUpdateDeleteOneStudent(Student student, SyncResult results) {
        ConcurrentHashMap<Long, Attendance> source = null;
        ConcurrentHashMap<Long, Attendance> ed = null;
        try {
            source = resolveAllFromSourceSystem(Long.valueOf(student.getSourceSystemId()));
        } catch (HttpClientException e) {
            results.attendanceSourceGetFailed(Long.valueOf(student.getSourceSystemId()), student.getId());
            return new ConcurrentHashMap<>();
        }
        try {
            ed = resolveFromEdPanel(student.getId());
        } catch (HttpClientException e) {
            results.attendanceEdPanelGetFailed(Long.valueOf(school.getSourceSystemId()), school.getId());
            return new ConcurrentHashMap<>();
        }
        Iterator<Map.Entry<Long, Attendance>> sourceIterator = source.entrySet().iterator();
        //Find & perform the inserts and updates, if any
        while(sourceIterator.hasNext()) {
            Map.Entry<Long, Attendance> entry = sourceIterator.next();
            Attendance sourceAttendance = entry.getValue();
            Attendance edPanelAttendance = ed.get(entry.getKey());
            if(null == edPanelAttendance){
                Attendance created = null;
                try {
                    created = edPanel.createAttendance(school.getId(), student.getId(), sourceAttendance);
                } catch (HttpClientException e) {
                    results.attendanceCreateFailed(entry.getKey());
                    continue;
                }
                sourceAttendance.setId(created.getId());
                results.attendanceCreated(entry.getKey(), sourceAttendance.getId());
            } else {
                sourceAttendance.setId(edPanelAttendance.getId());
                if(!edPanelAttendance.equals(sourceAttendance)) {
                    try {
                        edPanel.updateAttendance(school.getId(), student.getId(), sourceAttendance);
                    } catch (IOException e) {
                        results.attendanceUpdateFailed(entry.getKey(), sourceAttendance.getId());
                        continue;
                    }
                    results.attendanceUpdated(entry.getKey(), sourceAttendance.getId());
                }
            }
        }
        //Delete anything IN EdPanel that is NOT in source system
        Iterator<Map.Entry<Long, Attendance>> edpanelIterator = ed.entrySet().iterator();
        while(edpanelIterator.hasNext()) {
            Map.Entry<Long, Attendance> entry = edpanelIterator.next();
            if(!source.containsKey(entry.getKey())) {
                try {
                    edPanel.deleteAttendance(school.getId(), student.getId(), entry.getValue());
                } catch (HttpClientException e) {
                    results.attendanceDeleteFailed(entry.getKey(), entry.getValue().getId());
                    continue;
                }
                results.attendanceDeleted(entry.getKey(), entry.getValue().getId());
            }
        }
        return source;
    }

    protected ConcurrentHashMap<Long, Attendance> resolveAllFromSourceSystem(Long sourceStudentId) throws HttpClientException {
        ConcurrentHashMap<Long, Attendance> result = new ConcurrentHashMap<>();
        PsResponse<PsAttendanceWrapper> response = powerSchool.getStudentAttendance(sourceStudentId);
        for(PsResponseInner<PsAttendanceWrapper> wrap : response.record) {
            PsAttendance psAttendance = wrap.tables.attendance;
            Attendance a = psAttendance.toApiModel();
            a.setSchoolDay(schoolDays.get(a.getSchoolDay().getId()));
            a.setStudent(studentAssociator.findBySourceSystemId(sourceStudentId));
            result.put(psAttendance.dcid, a);
        }
        return result;
    }

    protected ConcurrentHashMap<Long, Attendance> resolveFromEdPanel(Long edpanelStudentId) throws HttpClientException {
        Attendance[] attendances = edPanel.getAttendance(school.getId(), edpanelStudentId);
        ConcurrentHashMap<Long, Attendance> attendanceMap = new ConcurrentHashMap<>();
        for(Attendance c: attendances) {
            Long id = null;
            String ssid = c.getSourceSystemId();
            if(null != ssid) {
                id = Long.valueOf(ssid);
                attendanceMap.put(id, c);
            }
        }
        return attendanceMap;
    }
}
