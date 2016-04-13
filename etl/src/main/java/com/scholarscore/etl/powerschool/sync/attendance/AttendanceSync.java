package com.scholarscore.etl.powerschool.sync.attendance;

import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.EtlEngine;
import com.scholarscore.etl.ISync;
import com.scholarscore.etl.PowerSchoolSyncResult;
import com.scholarscore.etl.powerschool.api.model.PsPeriod;
import com.scholarscore.etl.powerschool.api.model.cycles.PsCycle;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.etl.powerschool.sync.associator.StudentAssociator;
import com.scholarscore.models.School;
import com.scholarscore.models.Section;
import com.scholarscore.models.attendance.Attendance;
import com.scholarscore.models.attendance.SchoolDay;
import com.scholarscore.models.user.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by markroper on 10/30/15.
 */
public class AttendanceSync implements ISync<Attendance> {
    private final static Logger LOGGER = LoggerFactory.getLogger(AttendanceSync.class);
    protected IAPIClient edPanel;
    protected IPowerSchoolClient powerSchool;
    protected School school;
    protected StudentAssociator studentAssociator;
    protected ConcurrentHashMap<LocalDate, SchoolDay> schoolDays;
    protected LocalDate syncCutoff;
    protected Long dailyAbsenceTrigger;
    protected ConcurrentHashMap<Long, PsCycle> schoolCycles;
    protected ConcurrentHashMap<Long, Set<Section>> studentClasses;
    ConcurrentHashMap<Long, PsPeriod> periods;
    public AttendanceSync(IAPIClient edPanel,
                          IPowerSchoolClient powerSchool,
                          School s,
                          StudentAssociator studentAssociator,
                          ConcurrentHashMap<LocalDate, SchoolDay> schoolDays,
                          LocalDate syncCutoff,
                          Long dailyAbsenceTrigger,
                          ConcurrentHashMap<Long,PsCycle> schoolCycles,
                          ConcurrentHashMap<Long, Set<Section>> studentClasses,
                          ConcurrentHashMap<Long, PsPeriod> periods) {
        this.edPanel = edPanel;
        this.powerSchool = powerSchool;
        this.school = s;
        this.studentAssociator = studentAssociator;
        this.schoolDays = schoolDays;
        this.syncCutoff = syncCutoff;
        this.dailyAbsenceTrigger = dailyAbsenceTrigger;
        this.schoolCycles = schoolCycles;
        this.studentClasses = studentClasses;
        this.periods = periods;
    }

    @Override
    public ConcurrentHashMap<Long, Attendance> syncCreateUpdateDelete(PowerSchoolSyncResult results) {
        ConcurrentHashMap<Long, Attendance> response = new ConcurrentHashMap<>();
        ExecutorService executor = Executors.newFixedThreadPool(EtlEngine.THREAD_POOL_SIZE);
        for (Map.Entry<Long, Student> longStudentEntry : studentAssociator.getUsers().entrySet()) {
            Student s = longStudentEntry.getValue();

            Map<Long, Long> dcidToTableId = studentAssociator.getDcidToTableIdMap();
            if (s.getCurrentSchoolId().equals(school.getId())) {
                AttendanceRunnable runnable = new AttendanceRunnable(
                        edPanel, powerSchool, school, s, schoolDays, results, syncCutoff,
                        dailyAbsenceTrigger, schoolCycles, studentClasses, periods, dcidToTableId);
                executor.execute(runnable);
            }
        }
        executor.shutdown();
        //Spin while we wait for all the threads to complete
        try {
            if (!executor.awaitTermination(EtlEngine.TOTAL_TTL_MINUTES, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch(InterruptedException e) {
            LOGGER.error("Executor thread pool interrupted " + e.getMessage());
        }
        return response;
    }
}
