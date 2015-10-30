package com.scholarscore.etl.powerschool.sync;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.ISync;
import com.scholarscore.etl.SyncResult;
import com.scholarscore.etl.powerschool.api.model.PsCourses;
import com.scholarscore.etl.powerschool.api.model.attendance.PsAttendance;
import com.scholarscore.etl.powerschool.api.model.attendance.PsAttendanceWrapper;
import com.scholarscore.etl.powerschool.api.response.PsResponse;
import com.scholarscore.etl.powerschool.api.response.PsResponseInner;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.etl.powerschool.sync.associator.StudentAssociator;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.attendance.Attendance;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by markroper on 10/30/15.
 */
public class AttendanceSync implements ISync<Attendance> {
    protected IAPIClient edPanel;
    protected IPowerSchoolClient powerSchool;
    protected School school;
    private StudentAssociator studentAssociator;

    public AttendanceSync(IAPIClient edPanel,
                          IPowerSchoolClient powerSchool,
                          School s,
                          StudentAssociator studentAssociator) {
        this.edPanel = edPanel;
        this.powerSchool = powerSchool;
        this.school = s;
        this.studentAssociator = studentAssociator;
    }
    @Override
    public ConcurrentHashMap<Long, Attendance> syncCreateUpdateDelete(SyncResult results) {
        return null;
    }

    protected ConcurrentHashMap<Long, Attendance> resolveAllFromSourceSystem(Long sourceStudentId) throws HttpClientException {
        ConcurrentHashMap<Long, Attendance> result = new ConcurrentHashMap<>();
        PsResponse<PsAttendanceWrapper> response = powerSchool.getStudentAttendance(sourceStudentId);
        for(PsResponseInner<PsAttendanceWrapper> wrap : response.record) {
            PsAttendance psAttendance = wrap.tables.attendance;
            Attendance a = psAttendance.toApiModel();
            result.put(psAttendance.dcid, a);
        }
        return result;
    }

    protected ConcurrentHashMap<Long, Attendance> resolveFromEdPanel(Long edpanelStudentId) throws HttpClientException {
        Attendance[] attendances = edPanel.getAttendance(school.getId());
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
