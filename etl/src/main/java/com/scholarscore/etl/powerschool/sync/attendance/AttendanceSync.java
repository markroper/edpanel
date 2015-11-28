package com.scholarscore.etl.powerschool.sync.attendance;

import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.EtlEngine;
import com.scholarscore.etl.ISync;
import com.scholarscore.etl.PowerSchoolSyncResult;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.etl.powerschool.sync.associator.StudentAssociator;
import com.scholarscore.models.School;
import com.scholarscore.models.attendance.Attendance;
import com.scholarscore.models.attendance.SchoolDay;
import com.scholarscore.models.user.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.Map;
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
    protected Long dailyAbsenseTrigger;
    public AttendanceSync(IAPIClient edPanel,
                          IPowerSchoolClient powerSchool,
                          School s,
                          StudentAssociator studentAssociator,
                          ConcurrentHashMap<LocalDate, SchoolDay> schoolDays,
                          LocalDate syncCutoff,
                          Long dailyAbsenseTrigger) {
        this.edPanel = edPanel;
        this.powerSchool = powerSchool;
        this.school = s;
        this.studentAssociator = studentAssociator;
        this.schoolDays = schoolDays;
        this.syncCutoff = syncCutoff;
        this.dailyAbsenseTrigger = dailyAbsenseTrigger;
    }

    @Override
    public ConcurrentHashMap<Long, Attendance> syncCreateUpdateDelete(PowerSchoolSyncResult results) {
        ConcurrentHashMap<Long, Attendance> response = new ConcurrentHashMap<>();
        Iterator<Map.Entry<Long, Student>> studentIterator = studentAssociator.getStudents().entrySet().iterator();
        ExecutorService executor = Executors.newFixedThreadPool(EtlEngine.THREAD_POOL_SIZE);
        while(studentIterator.hasNext()) {
            Student s = studentIterator.next().getValue();
            if(s.getCurrentSchoolId().equals(school.getId())) {
                AttendanceRunnable runnable = new AttendanceRunnable(
                        edPanel, powerSchool, school, s, schoolDays, results, syncCutoff, dailyAbsenseTrigger);
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
