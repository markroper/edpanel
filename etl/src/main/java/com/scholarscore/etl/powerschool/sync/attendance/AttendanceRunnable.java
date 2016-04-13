package com.scholarscore.etl.powerschool.sync.attendance;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.ISync;
import com.scholarscore.etl.PowerSchoolSyncResult;
import com.scholarscore.etl.powerschool.api.model.PsPeriod;
import com.scholarscore.etl.powerschool.api.model.attendance.PsAttendance;
import com.scholarscore.etl.powerschool.api.model.attendance.PsAttendanceCode;
import com.scholarscore.etl.powerschool.api.model.attendance.PsAttendanceCodeWrapper;
import com.scholarscore.etl.powerschool.api.model.attendance.PsAttendanceWrapper;
import com.scholarscore.etl.powerschool.api.model.cycles.PsCycle;
import com.scholarscore.etl.powerschool.api.response.PsResponse;
import com.scholarscore.etl.powerschool.api.response.PsResponseInner;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.etl.powerschool.sync.SyncBase;
import com.scholarscore.models.School;
import com.scholarscore.models.Section;
import com.scholarscore.models.attendance.Attendance;
import com.scholarscore.models.attendance.AttendanceStatus;
import com.scholarscore.models.attendance.AttendanceType;
import com.scholarscore.models.attendance.SchoolDay;
import com.scholarscore.models.user.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by markroper on 11/1/15.
 */
public class AttendanceRunnable extends SyncBase<Attendance> implements Runnable, ISync<Attendance> {
    private final static Logger LOGGER = LoggerFactory.getLogger(AttendanceRunnable.class);
    protected IAPIClient edPanel;
    protected IPowerSchoolClient powerSchool;
    protected School school;
    protected ConcurrentHashMap<LocalDate, SchoolDay> schoolDays;
    protected Student student;
    protected PowerSchoolSyncResult results;
    protected LocalDate syncCutoff;
    protected Long dailyAbsenceTrigger;
    protected Map<Long, PsCycle> schoolCycles;
    protected ConcurrentHashMap<Long, Set<Section>> studentClasses;
    protected ConcurrentHashMap<Long, PsPeriod> periods;
    protected Map<Long, Long> dcidToTableId;

    public AttendanceRunnable(IAPIClient edPanel,
                              IPowerSchoolClient powerSchool,
                              School s,
                              Student student,
                              ConcurrentHashMap<LocalDate, SchoolDay> schoolDays,
                              PowerSchoolSyncResult results,
                              LocalDate syncCutoff,
                              Long dailyAbsenceTrigger,
                              ConcurrentHashMap<Long, PsCycle> schoolCycles,
                              ConcurrentHashMap<Long, Set<Section>> studentClasses,
                              ConcurrentHashMap<Long, PsPeriod> periods,
                              Map<Long, Long> dcidToTableId) {
        this.edPanel = edPanel;
        this.powerSchool = powerSchool;
        this.school = s;
        this.student = student;
        this.schoolDays = schoolDays;
        this.results = results;
        this.syncCutoff = syncCutoff;
        this.dailyAbsenceTrigger = dailyAbsenceTrigger;
        this.schoolCycles = schoolCycles;
        this.studentClasses = studentClasses;
        this.periods = periods;
        this.dcidToTableId = dcidToTableId;
    }
    @Override
    public void run() {
        syncCreateUpdateDelete(results);
    }

    @Override
    protected ConcurrentHashMap<Long, Attendance> resolveAllFromSourceSystem() throws HttpClientException {
        Long sourceStudentId = Long.valueOf(student.getSourceSystemId());
        ConcurrentHashMap<Long, Attendance> result = new ConcurrentHashMap<>();
        long syntheticDcid = -1;
        //First, get the attendance codes.
        PsResponse<PsAttendanceCodeWrapper> codeResponse = powerSchool.getAttendanceCodes();
        Map<Long, AttendanceStatus> codeMap = new HashMap<>();
        for(PsResponseInner<PsAttendanceCodeWrapper> wrap : codeResponse.record) {
            PsAttendanceCode psAttendanceCode = wrap.tables.attendance_code;
            codeMap.put(psAttendanceCode.id, psAttendanceCode.toApiModel());
        }

        PsResponse<PsAttendanceWrapper> response = powerSchool.getStudentAttendance(dcidToTableId.get(sourceStudentId));
        Map<SchoolDay, List<Attendance>> schoolDayToAttendances = new HashMap<>();
        for(PsResponseInner<PsAttendanceWrapper> wrap : response.record) {
            PsAttendance psAttendance = wrap.tables.attendance;
            Attendance a = psAttendance.toApiModel();
            SchoolDay schoolDay = schoolDays.get(psAttendance.att_date);
            if(null == schoolDay) {
                LOGGER.debug("Found an attendance without an associated school day: " + psAttendance.att_date);
                continue;
            }
            a = resolveSectionFk(a, schoolDay, psAttendance);
            a.setSchoolDay(schoolDay);
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
        if(null != dailyAbsenceTrigger) {
            for (Map.Entry<SchoolDay, List<Attendance>> entry : schoolDayToAttendances.entrySet()) {
                boolean hasDaily = false;
                long absences = 0;
                long tardies = 0;
                for (Attendance a : entry.getValue()) {
                    if (a.getType().equals(AttendanceType.DAILY)) {
                        hasDaily = true;
                        break;
                    }
                    if (a.getStatus().equals(AttendanceStatus.ABSENT)) {
                        absences++;
                    }
                    if(a.getStatus().equals(AttendanceStatus.TARDY)) {
                        tardies++;
                    }
                }
                //If the number of absences is equal to the periods in the day minus one for lunch
                //and there is no daily attendance event already created, create one.
                if (!hasDaily) {
                    if (absences >= dailyAbsenceTrigger) {
                        Attendance a = new Attendance();
                        a.setSchoolDay(entry.getKey());
                        a.setStudent(student);
                        a.setStatus(AttendanceStatus.ABSENT);
                        a.setType(AttendanceType.DAILY);
                        a.setSourceSystemId(String.valueOf(syntheticDcid));
                        a.setDescription("EdPanel generated due to daily section absences( " +
                                absences +
                                ") exceeding limit: " + dailyAbsenceTrigger);
                        result.put(syntheticDcid, a);
                        syntheticDcid--;
                    } else if(absences > 0 || tardies > 0){
                        Attendance a = new Attendance();
                        a.setSchoolDay(entry.getKey());
                        a.setStudent(student);
                        a.setStatus(AttendanceStatus.TARDY);
                        a.setType(AttendanceType.DAILY);
                        a.setSourceSystemId(String.valueOf(syntheticDcid));
                        a.setDescription("EdPanel generated due to daily section absences( " +
                                absences +
                                ") exceeding limit: ");
                        result.put(syntheticDcid, a);
                        syntheticDcid--;
                    }
                }
            }
        }
        return result;
    }

    @Override
    protected void handleSourceGetFailure(PowerSchoolSyncResult results) {
        LOGGER.error("Unable to fetch attendance from from PowerSchool for student " + student.getName() +
                " with ID: " + student.getId());
        results.attendanceSourceGetFailed(Long.valueOf(student.getSourceSystemId()), student.getId());
    }

    @Override
    protected ConcurrentHashMap<Long, Attendance> resolveFromEdPanel() throws HttpClientException {
        Attendance[] attendances = edPanel.getAttendance(school.getId(), student.getId());
        ConcurrentHashMap<Long, Attendance> attendanceMap = new ConcurrentHashMap<>();
        for(Attendance c: attendances) {
            String ssid = c.getSourceSystemId();
            if(null != ssid) {
                attendanceMap.put(Long.valueOf(ssid), c);
            }
        }
        return attendanceMap;
    }

    @Override
    protected void handleEdPanelGetFailure(PowerSchoolSyncResult results) {
        LOGGER.error("Unable to fetch attendance from from EdPanel for student " + student.getName() +
                " with ID: " + student.getId());
        results.attendanceEdPanelGetFailed(Long.valueOf(school.getSourceSystemId()), school.getId());
    }

    @Override
    protected void createEdPanelRecord(Attendance entityToSave, PowerSchoolSyncResult results) {
        enqueueForBulkCreate(entityToSave);
    }

    @Override
    protected void updateEdPanelRecord(Attendance sourceSystemEntity, Attendance edPanelEntity, PowerSchoolSyncResult results) {
        Long ssid = Long.valueOf(sourceSystemEntity.getSourceSystemId());
        sourceSystemEntity.setId(edPanelEntity.getId());
        if (edPanelEntity.getStudent().getId().equals(sourceSystemEntity.getStudent().getId())) {
            sourceSystemEntity.setStudent(edPanelEntity.getStudent());
        }
        if (edPanelEntity.getSchoolDay().getId().equals(sourceSystemEntity.getSchoolDay().getId())) {
            sourceSystemEntity.setSchoolDay(edPanelEntity.getSchoolDay());
        }
        if (!edPanelEntity.equals(sourceSystemEntity)) {
            try {
                edPanel.updateAttendance(school.getId(), student.getId(), sourceSystemEntity);
                results.attendanceUpdated(ssid, sourceSystemEntity.getId());
            } catch (HttpClientException e) {
                results.attendanceUpdateFailed(ssid, sourceSystemEntity.getId());
            }
        }
    }

    @Override
    protected void deleteEdPanelRecord(Attendance entityToDelete, PowerSchoolSyncResult results) {
        Long ssid = Long.valueOf(entityToDelete.getSourceSystemId());
        if (entityToDelete.getSchoolDay().getDate().compareTo(syncCutoff) > 0) {
            try {
                edPanel.deleteAttendance(school.getId(), student.getId(), entityToDelete);
                results.attendanceDeleted(ssid, entityToDelete.getId());
            } catch (HttpClientException e) {
                results.attendanceDeleteFailed(ssid, entityToDelete.getId());
            }
        }
    }

    @Override
    protected void createBulkEdPanelRecords(List<Attendance> entitiesToBulkCreate) {
        //Perform the bulk creates!
        try {
            edPanel.createAttendances(school.getId(), student.getId(), entitiesToBulkCreate);
            for (Attendance s : entitiesToBulkCreate) {
                results.attendanceCreated(Long.valueOf(s.getSourceSystemId()), -1);
            }
        } catch (HttpClientException e) {
            for(Attendance s: entitiesToBulkCreate) {
                results.attendanceCreateFailed(Long.valueOf(s.getSourceSystemId()));
            }
        }
    }

    /**
     * Using the period_id, cycle_day, school_day and classes the student is enrolled in we generate
     * the section_fk that the attendance should be associated with.
     * @param a Attendance object
     * @param schoolDay The schoolDay the attendance event occurred on
     * @param psAttendance THe psAttendance Object
     * @return the attendance object with the section_fk set
     */
    private Attendance resolveSectionFk(Attendance a, SchoolDay schoolDay, PsAttendance psAttendance) {
        if (null != schoolDay) {
            PsCycle cycleDay = schoolCycles.get(schoolDay.getCycleId());
            if (null != periods.get(psAttendance.periodid)) {
                Long periodNumber = periods.get(psAttendance.periodid).period_number;
                String letter = null;
                if(null != cycleDay) {
                    letter = cycleDay.letter;
                }
                //Maybe missing some students here?
                Set<Section> sections =  studentClasses.get(student.getId());
                //Need to also make sure teh SchoolDay date are within the term dates
                if (null != sections) {
                    for (Section section : sections) {
                        Map<String, ArrayList<Long>> expression = section.getExpression();
                        if (null != expression && null != letter) {
                            if (null != expression.get(letter)) {
                                for (Long sectionPeriod : expression.get(letter)) {
                                    if (sectionPeriod.equals(periodNumber) &&
                                            (schoolDay.getDate().isAfter(section.getStartDate())
                                                    || schoolDay.getDate().equals(section.getStartDate()))
                                            && (schoolDay.getDate().isBefore(section.getEndDate()))
                                                    || schoolDay.getDate().equals(section.getEndDate())
                                            ){
                                        a.setSection(section);

                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
        return a;
    }
}
