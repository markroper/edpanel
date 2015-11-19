package com.scholarscore.etl.powerschool.sync.attendance;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.ISync;
import com.scholarscore.etl.SyncResult;
import com.scholarscore.etl.powerschool.api.model.attendance.PsAttendance;
import com.scholarscore.etl.powerschool.api.model.attendance.PsAttendanceCode;
import com.scholarscore.etl.powerschool.api.model.attendance.PsAttendanceCodeWrapper;
import com.scholarscore.etl.powerschool.api.model.attendance.PsAttendanceWrapper;
import com.scholarscore.etl.powerschool.api.response.PsResponse;
import com.scholarscore.etl.powerschool.api.response.PsResponseInner;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.models.School;
import com.scholarscore.models.attendance.Attendance;
import com.scholarscore.models.attendance.AttendanceStatus;
import com.scholarscore.models.attendance.AttendanceTypes;
import com.scholarscore.models.attendance.SchoolDay;
import com.scholarscore.models.user.Student;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by markroper on 11/1/15.
 */
public class AttendanceRunnable implements Runnable, ISync<Attendance> {
    protected IAPIClient edPanel;
    protected IPowerSchoolClient powerSchool;
    protected School school;
    protected ConcurrentHashMap<Date, SchoolDay> schoolDays;
    protected Student student;
    protected SyncResult results;
    protected Date syncCutoff;
    protected Long dailyAbsenseTrigger;

    public AttendanceRunnable(IAPIClient edPanel,
                          IPowerSchoolClient powerSchool,
                          School s,
                          Student student,
                          ConcurrentHashMap<Date, SchoolDay> schoolDays,
                          SyncResult results,
                          Date syncCutoff,
                          Long dailyAbsenseTrigger) {
        this.edPanel = edPanel;
        this.powerSchool = powerSchool;
        this.school = s;
        this.student = student;
        this.schoolDays = schoolDays;
        this.results = results;
        this.syncCutoff = syncCutoff;
        this.dailyAbsenseTrigger = dailyAbsenseTrigger;
    }
    @Override
    public void run() {
        syncCreateUpdateDelete(results);
    }

    @Override
    public ConcurrentHashMap<Long, Attendance> syncCreateUpdateDelete(SyncResult results) {
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
        List<Attendance> attendanceToCreate = new ArrayList<>();
        //Find & perform the inserts and updates, if any
        while(sourceIterator.hasNext()) {
            Map.Entry<Long, Attendance> entry = sourceIterator.next();
            Attendance sourceAttendance = entry.getValue();
            Attendance edPanelAttendance = ed.get(entry.getKey());
            if(null == edPanelAttendance){
                attendanceToCreate.add(sourceAttendance);
                results.attendanceCreated(Long.valueOf(sourceAttendance.getSourceSystemId()), -1L);
            } else {
                sourceAttendance.setId(edPanelAttendance.getId());
                if(edPanelAttendance.getStudent().getId().equals(sourceAttendance.getStudent().getId())) {
                    sourceAttendance.setStudent(edPanelAttendance.getStudent());
                }
                if(edPanelAttendance.getSchoolDay().getId().equals(sourceAttendance.getSchoolDay().getId())) {
                    sourceAttendance.setSchoolDay(edPanelAttendance.getSchoolDay());
                }
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
        //Perform the bulk creates!
        try {
            edPanel.createAttendance(school.getId(), student.getId(), attendanceToCreate);
        } catch (HttpClientException e) {
            for(Attendance s: attendanceToCreate) {
                results.attendanceCreateFailed(Long.valueOf(s.getSourceSystemId()));
            }
        }

        //Delete anything IN EdPanel that is NOT in source system
        Iterator<Map.Entry<Long, Attendance>> edpanelIterator = ed.entrySet().iterator();
        while(edpanelIterator.hasNext()) {
            Map.Entry<Long, Attendance> entry = edpanelIterator.next();
            if(!source.containsKey(entry.getKey()) &&
                    entry.getValue().getSchoolDay().getDate().compareTo(syncCutoff) > 0) {
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
        long syntheticDcid = -1;
        //First, get the attendance codes.
        PsResponse<PsAttendanceCodeWrapper> codeResponse = powerSchool.getAttendanceCodes();
        Map<Long, AttendanceStatus> codeMap = new HashMap<>();
        for(PsResponseInner<PsAttendanceCodeWrapper> wrap : codeResponse.record) {
            PsAttendanceCode psAttendanceCode = wrap.tables.attendance_code;
            codeMap.put(psAttendanceCode.id, psAttendanceCode.toApiModel());
        }

        PsResponse<PsAttendanceWrapper> response = powerSchool.getStudentAttendance(sourceStudentId);
        Map<SchoolDay, List<Attendance>> schoolDayToAttendances = new HashMap<>();
        for(PsResponseInner<PsAttendanceWrapper> wrap : response.record) {
            PsAttendance psAttendance = wrap.tables.attendance;
            Attendance a = psAttendance.toApiModel();
            a.setSchoolDay(schoolDays.get(psAttendance.att_date));
            a.setStudent(student);
            a.setStatus(codeMap.get(psAttendance.attendance_codeid));
            if(!schoolDayToAttendances.containsKey(a.getSchoolDay())) {
                schoolDayToAttendances.put(a.getSchoolDay(), new ArrayList<>());
            }
            schoolDayToAttendances.get(a.getSchoolDay()).add(a);
            result.put(psAttendance.dcid, a);
        }
        //Ok, for schools that track only section level attendance and figure out daily attendance from that,
        //we need to see if the student missed every period and then create a daily attendance event if they did...
        if(null != dailyAbsenseTrigger) {
            for (Map.Entry<SchoolDay, List<Attendance>> entry : schoolDayToAttendances.entrySet()) {
                boolean hasDaily = false;
                long absenses = 0;
                for (Attendance a : entry.getValue()) {
                    if (a.getType().equals(AttendanceTypes.DAILY)) {
                        hasDaily = true;
                        break;
                    }
                    if (a.getStatus().equals(AttendanceStatus.ABSENT)) {
                        absenses++;
                    }
                }
                //If the number of absenses is equal to the periods in the day minus one for lunch
                //and there is no daily attendance event already created, create one.
                if (!hasDaily && absenses >= dailyAbsenseTrigger) {
                    Attendance a = new Attendance();
                    a.setSchoolDay(entry.getKey());
                    a.setStudent(student);
                    a.setStatus(AttendanceStatus.ABSENT);
                    a.setType(AttendanceTypes.DAILY);
                    a.setSourceSystemId(String.valueOf(syntheticDcid));
                    a.setDescription("EdPanel generated due to daily section absenses( " +
                            absenses +
                            ") exceeding limit: " + dailyAbsenseTrigger);
                    result.put(syntheticDcid, a);
                    syntheticDcid--;
                }
            }
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
