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
import com.scholarscore.etl.powerschool.api.response.PsResponse;
import com.scholarscore.etl.powerschool.api.response.PsResponseInner;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.models.Cycle;
import com.scholarscore.models.School;
import com.scholarscore.models.Section;
import com.scholarscore.models.attendance.Attendance;
import com.scholarscore.models.attendance.AttendanceStatus;
import com.scholarscore.models.attendance.AttendanceTypes;
import com.scholarscore.models.attendance.SchoolDay;
import com.scholarscore.models.user.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by markroper on 11/1/15.
 */
public class AttendanceRunnable implements Runnable, ISync<Attendance> {
    private final static Logger LOGGER = LoggerFactory.getLogger(AttendanceRunnable.class);
    protected IAPIClient edPanel;
    protected IPowerSchoolClient powerSchool;
    protected School school;
    protected ConcurrentHashMap<LocalDate, SchoolDay> schoolDays;
    protected Student student;
    protected PowerSchoolSyncResult results;
    protected LocalDate syncCutoff;
    protected Long dailyAbsenseTrigger;
    protected Map<Long, Cycle> schoolCycles;
    protected ConcurrentHashMap<Long, Set<Section>> studentClasses;
    protected ConcurrentHashMap<Long, PsPeriod> periods;

    public AttendanceRunnable(IAPIClient edPanel,
                              IPowerSchoolClient powerSchool,
                              School s,
                              Student student,
                              ConcurrentHashMap<LocalDate, SchoolDay> schoolDays,
                              PowerSchoolSyncResult results,
                              LocalDate syncCutoff,
                              Long dailyAbsenseTrigger,
                              ConcurrentHashMap<Long, Cycle> schoolCycles,
                              ConcurrentHashMap<Long, Set<Section>> studentClasses,
                              ConcurrentHashMap<Long, PsPeriod> periods) {
        this.edPanel = edPanel;
        this.powerSchool = powerSchool;
        this.school = s;
        this.student = student;
        this.schoolDays = schoolDays;
        this.results = results;
        this.syncCutoff = syncCutoff;
        this.dailyAbsenseTrigger = dailyAbsenseTrigger;
        this.schoolCycles = schoolCycles;
        this.studentClasses = studentClasses;
        this.periods = periods;
    }
    @Override
    public void run() {
        syncCreateUpdateDelete(results);
    }

    @Override
    public ConcurrentHashMap<Long, Attendance> syncCreateUpdateDelete(PowerSchoolSyncResult results) {
        ConcurrentHashMap<Long, Attendance> source = null;
        ConcurrentHashMap<Long, Attendance> ed = null;
        try {
            source = resolveAllFromSourceSystem(Long.valueOf(student.getSourceSystemId()));
        } catch (HttpClientException e) {
            try {
                source = resolveAllFromSourceSystem(Long.valueOf(student.getSourceSystemId()));
            } catch (HttpClientException ex) {
                LOGGER.error("Unable to fetch attendance from from PowerSchool for student " + student.getName() +
                        " with ID: " + student.getId());
                results.attendanceSourceGetFailed(Long.valueOf(student.getSourceSystemId()), student.getId());
                return new ConcurrentHashMap<>();
            }
        }
        try {
            ed = resolveFromEdPanel(student.getId());
        } catch (HttpClientException e) {
            try {
                ed = resolveFromEdPanel(student.getId());
            } catch (HttpClientException ex) {
                LOGGER.error("Unable to fetch attendance from from EdPanel for student " + student.getName() +
                        " with ID: " + student.getId());
                results.attendanceEdPanelGetFailed(Long.valueOf(school.getSourceSystemId()), school.getId());
                return new ConcurrentHashMap<>();
            }
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
            SchoolDay schoolDay = schoolDays.get(psAttendance.att_date);
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
        if(null != dailyAbsenseTrigger) {
            for (Map.Entry<SchoolDay, List<Attendance>> entry : schoolDayToAttendances.entrySet()) {
                boolean hasDaily = false;
                long absenses = 0;
                long tardies = 0;
                for (Attendance a : entry.getValue()) {
                    if (a.getType().equals(AttendanceTypes.DAILY)) {
                        hasDaily = true;
                        break;
                    }
                    if (a.getStatus().equals(AttendanceStatus.ABSENT)) {
                        absenses++;
                    }
                    if(a.getStatus().equals(AttendanceStatus.TARDY)) {
                        tardies++;
                    }
                }
                //If the number of absenses is equal to the periods in the day minus one for lunch
                //and there is no daily attendance event already created, create one.
                if (!hasDaily) {
                    if (absenses >= dailyAbsenseTrigger) {
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
                    } else if(absenses > 0 || tardies > 0){
                        Attendance a = new Attendance();
                        a.setSchoolDay(entry.getKey());
                        a.setStudent(student);
                        a.setStatus(AttendanceStatus.TARDY);
                        a.setType(AttendanceTypes.DAILY);
                        a.setSourceSystemId(String.valueOf(syntheticDcid));
                        a.setDescription("EdPanel generated due to daily section absenses( " +
                                absenses +
                                ") exceeding limit: ");
                        result.put(syntheticDcid, a);
                        syntheticDcid--;
                    }
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

    /**
     * Using the period_id, cycle_day, school_day and classes the student is enrolled in we generate
     * the section_fk that the attendance should be associated with.
     * @param a Attendance object
     * @param schoolDay The schoolDay the attendance event occurred on
     * @param psAttendance THe psAttendance Object
     * @return the attendance object with the section_fk set
     */
    protected Attendance resolveSectionFk(Attendance a, SchoolDay schoolDay, PsAttendance psAttendance) {
        if (null != schoolDay) {
            Cycle cycleDay = schoolCycles.get(schoolDay.getCycleId());
            if (null != periods.get(psAttendance.periodid)) {
                Long periodNumber = periods.get(psAttendance.periodid).period_number;
                String letter = cycleDay.getLetter();
                //Maybe missing some students here?
                Set<Section> sections =  studentClasses.get(Long.valueOf(student.getSourceSystemId()));
                //Need to also make sure teh SchoolDay date are within the term dates
                if (null != sections) {
                    for (Section section : sections) {
                        Map<String, ArrayList<Long>> expression = section.getExpression();
                        if (null != expression) {
                            if (null != expression.get(letter)) {
                                for (Long sectionPeriod : expression.get(letter)) {
                                    if (sectionPeriod.equals(periodNumber) &&
                                            schoolDay.getDate().isAfter(section.getStartDate())
                                            && schoolDay.getDate().isBefore(section.getEndDate())) {
                                        //THIS IS THE SECTION RESOLVE SECTION_FK TO BE THIS SECTION_ID
                                        a.setSectionFk(section.getId());
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
