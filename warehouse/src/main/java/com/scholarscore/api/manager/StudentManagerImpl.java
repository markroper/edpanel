package com.scholarscore.api.manager;

import com.scholarscore.api.persistence.StudentPersistence;
import com.scholarscore.api.persistence.StudentPrepScorePersistence;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.PrepScore;
import com.scholarscore.models.assignment.AssignmentType;
import com.scholarscore.models.assignment.StudentAssignment;
import com.scholarscore.models.notification.group.FilteredStudents;
import com.scholarscore.models.ui.ScoreAsOfWeek;
import com.scholarscore.models.user.Student;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by cwallace on 9/16/2015.
 */
public class StudentManagerImpl implements StudentManager {
    private static final int CREATE_RETRY_MAX = 10;
    StudentPersistence studentPersistence;
    StudentPrepScorePersistence studentPrepScorePersistence;

    OrchestrationManager pm;

    private static final String STUDENT = "student";

    public void setStudentPersistence(StudentPersistence studentPersistence) {
        this.studentPersistence = studentPersistence;
    }

    public void setStudentPrepScorePersistence(StudentPrepScorePersistence studentPrepScorePersistence) {
        this.studentPrepScorePersistence = studentPrepScorePersistence;
    }

    public void setPm(OrchestrationManager pm) {
        this.pm = pm;
    }

    //Student
    @Override
    public ServiceResponse<Long> createStudent(Student student) {
        String initUsername = student.getUsername();
        boolean retry = true;
        int suffix = 0;
        while(retry && suffix < CREATE_RETRY_MAX) {
            retry = false;
            try {
                return new ServiceResponse<>(studentPersistence.createStudent(student));
            } catch (Throwable e) {
                suffix++;
                retry = true;
                if (null == initUsername) {
                    initUsername = student.getUsername();
                }
                student.setUsername(initUsername + suffix);
            }
        }
        return null;
     }

    @Override
    public StatusCode studentExists(long studentId) {
        Student stud = studentPersistence.select(studentId);
        if(null == stud) {
            return StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{STUDENT, studentId});
        }
        return StatusCodes.getStatusCode(StatusCodeType.OK);
    }

    @Override
    public ServiceResponse<Long> deleteStudent(long studentId) {
        StatusCode code = studentExists(studentId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        studentPersistence.delete(studentId);
        return new ServiceResponse<>((Long) null);
    }

    @Override
    public ServiceResponse<Collection<Student>> getAllStudents(Long schoolId) {
        return new ServiceResponse<>(
                studentPersistence.selectAll(schoolId, true));
    }

    @Override
    public ServiceResponse<Collection<Student>> getStudents(Long schoolId, FilteredStudents students) {
        return new ServiceResponse<>(studentPersistence.selectAll(schoolId, students, true));
    }

    @Override
    public ServiceResponse<Student> getStudent(long studentId) {
        StatusCode code = studentExists(studentId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        return new ServiceResponse<>(studentPersistence.select(studentId));
    }

    @Override
    public ServiceResponse<Student> getStudentBySourceSystemId(Long ssid) {
        return new ServiceResponse<>(studentPersistence.selectBySsid(ssid));
    }

    @Override
    public ServiceResponse<Long> replaceStudent(long studentId, Student student) {
        StatusCode code = studentExists(studentId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        studentPersistence.replaceStudent(studentId, student);
        return new ServiceResponse<>(studentId);
    }

    @Override
    public ServiceResponse<Long> updateStudent(long studentId, Student student) {
        StatusCode code = studentExists(studentId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        student.setId(studentId);
        student.mergePropertiesIfNull(studentPersistence.select(studentId));
        replaceStudent(studentId, student);
        return new ServiceResponse<>(studentId);
    }

    @Override
    public ServiceResponse<List<PrepScore>> getStudentPrepScore(Long[] studentIds, Date startDate, Date endDate) {
        return new ServiceResponse<>(studentPrepScorePersistence.selectStudentPrepScore(studentIds, startDate, endDate));
    }

    public ServiceResponse<List<ScoreAsOfWeek>> getStudentHomeworkRatesPerSection(Long studentId,  Long sectionId) {
        ServiceResponse<Collection<StudentAssignment>> studAssResp =
                pm.getStudentAssignmentManager().getOneSectionOneStudentsAssignments(
                studentId, sectionId);
        return determineWeeklyScore(studAssResp);

    }

    @Override
    public ServiceResponse<List<ScoreAsOfWeek>> getStudentHomeworkRates(Long studentId, LocalDate startDate, LocalDate endDate) {
        ServiceResponse<Collection<StudentAssignment>> studAssResp =
                pm.getStudentAssignmentManager().getAllStudentAssignmentsBetweenDates(studentId, startDate, endDate);
        return determineWeeklyScore(studAssResp);
    }
    private static Double calculateHwCompletionRate(List<StudentAssignment> studentAssignments) {
        Integer numerator = studentAssignments.size();
        for(StudentAssignment a: studentAssignments) {
            //TODO: at excel 35% is give for incomplete, need to generify this for other schools.
            if(null == a.getAwardedPoints() ||
                    a.getAwardedPoints().equals(0l) ||
                    a.getAwardedPoints() / a.getAssignment().getAvailablePoints() <= 0.35D) {
                numerator--;
            }
        }
        return numerator / (double) studentAssignments.size() * 100D;
    }

    private ServiceResponse<List<ScoreAsOfWeek>> determineWeeklyScore(ServiceResponse<Collection<StudentAssignment>> studAssResp) {
        List<ScoreAsOfWeek> weekEndToCompletion = new ArrayList<>();
        if(null == studAssResp.getCode()) {
            List<StudentAssignment> studentAssignments = new ArrayList<>(studAssResp.getValue());
            studentAssignments.sort((object1, object2) ->
                    object1.getAssignment().getDueDate().compareTo(object2.getAssignment().getDueDate()));
            List<StudentAssignment> hwAssignments = new ArrayList<>();
            //Sort by due date
            LocalDate currentLastDayOfWeek = null;
            for(StudentAssignment sa: studentAssignments) {
                if(sa.getAssignment().getType().equals(AssignmentType.HOMEWORK)) {
                    LocalDate dueDate = sa.getAssignment().getDueDate();
                    int daysToAdd = DayOfWeek.SATURDAY.getValue() - dueDate.getDayOfWeek().getValue();
                    LocalDate endOfWeek = dueDate.plusDays(daysToAdd);
                    if(null == currentLastDayOfWeek) {
                        currentLastDayOfWeek = endOfWeek;
                    }
                    if(!currentLastDayOfWeek.equals(endOfWeek)) {
                        weekEndToCompletion.add(
                                new ScoreAsOfWeek(currentLastDayOfWeek, calculateHwCompletionRate(hwAssignments)));
                        hwAssignments = new ArrayList<>();
                        currentLastDayOfWeek = endOfWeek;
                    }
                    hwAssignments.add(sa);
                }
            }
            if(hwAssignments.size() > 0) {
                weekEndToCompletion.add(
                        new ScoreAsOfWeek(currentLastDayOfWeek, calculateHwCompletionRate(hwAssignments)));
            }
        }
        return new ServiceResponse<>(weekEndToCompletion);
    }
}
