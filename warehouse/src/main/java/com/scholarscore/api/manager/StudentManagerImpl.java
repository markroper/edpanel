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
import com.scholarscore.models.user.Student;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cwallace on 9/16/2015.
 */
public class StudentManagerImpl implements StudentManager {

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
        return new ServiceResponse<>(studentPersistence.createStudent(student));
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
                studentPersistence.selectAll(schoolId));
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

    @Override
    public ServiceResponse<Map<Date, Double>> getStudentHomeworkRates(Long studentId, Date startDate, Date endDate) {

        ServiceResponse<Collection<StudentAssignment>> studAssResp =
                pm.getStudentAssignmentManager().getAllStudentAssignmentsBetweenDates(studentId, startDate, endDate);
        Map<Date, Double> weekEndToCompletion = new HashMap<>();
        if(null == studAssResp.getCode()) {
            List<StudentAssignment> studentAssignments = new ArrayList<>(studAssResp.getValue());
            studentAssignments.sort((object1, object2) ->
                    object1.getAssignment().getDueDate().compareTo(object2.getAssignment().getDueDate()));
            List<StudentAssignment> hwAssignments = new ArrayList<>();
            //Sort by due date
            Date currentLastDayOfWeek = null;
            Calendar cal  = Calendar.getInstance();
            int i = 1;
            for(StudentAssignment sa: studentAssignments) {
                if(sa.getAssignment().getType().equals(AssignmentType.HOMEWORK)) {
                    Date dueDate = sa.getAssignment().getDueDate();
                    cal.setTime(dueDate);
                    int currentDay = cal.get(Calendar.DAY_OF_WEEK);
                    int leftDays= Calendar.SATURDAY - currentDay;
                    cal.add(Calendar.DATE, leftDays);
                    if(null == currentLastDayOfWeek) {
                        currentLastDayOfWeek = cal.getTime();
                    }
                    if((null != currentLastDayOfWeek && !currentLastDayOfWeek.equals(cal.getTime())) ||
                            i == studentAssignments.size()) {
                        weekEndToCompletion.put(currentLastDayOfWeek, calculateHwCompletionRate(hwAssignments));
                        hwAssignments = new ArrayList<>();
                        currentLastDayOfWeek = cal.getTime();
                    }
                    hwAssignments.add(sa);
                }
                i++;
            }
            if(hwAssignments.size() > 0) {
                weekEndToCompletion.put(currentLastDayOfWeek, calculateHwCompletionRate(hwAssignments));
            }
        }
        return new ServiceResponse<>(weekEndToCompletion);
    }
    private static Double calculateHwCompletionRate(List<StudentAssignment> studentAssignments) {
        Integer numerator = studentAssignments.size();
        for(StudentAssignment a: studentAssignments) {
            if(null == a.getAwardedPoints() || a.getAwardedPoints().equals(0l)) {
                numerator--;
            }
        }
        return numerator / (double) studentAssignments.size();
    }
}
