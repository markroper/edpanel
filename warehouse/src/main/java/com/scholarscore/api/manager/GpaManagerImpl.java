package com.scholarscore.api.manager;

import com.scholarscore.api.persistence.GpaPersistence;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.gpa.Gpa;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * Created by markroper on 11/24/15.
 */
public class GpaManagerImpl implements GpaManager {
    @Autowired
    private GpaPersistence gpaPersistence;

    @Autowired
    private OrchestrationManager pm;

    private static final String GPA = "GPA";

    public void setGpaPersistence(GpaPersistence gpaPersistence) {
        this.gpaPersistence = gpaPersistence;
    }

    public void setPm(OrchestrationManager pm) {
        this.pm = pm;
    }

    @Override
    public ServiceResponse<Long> createGpa(long studentId, Gpa gpa) {
        StatusCode code = pm.getStudentManager().studentExists(studentId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        return new ServiceResponse<>(gpaPersistence.insertGpa(studentId, gpa));
    }

    @Override
    public ServiceResponse<Gpa> getGpa(long studentId) {
        StatusCode code = pm.getStudentManager().studentExists(studentId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        Gpa gpa = gpaPersistence.selectGpa(studentId);
        if(null == gpa) {
            return new ServiceResponse<>(
                    StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{ GPA }));
        } else {
            return new ServiceResponse<>(gpa);
        }
    }

    @Override
    public ServiceResponse<Collection<Gpa>> getAllGpasForStudents(
            List<Long> studentIds, LocalDate startDate, LocalDate endDate) {
        return new ServiceResponse<>(
                gpaPersistence.selectStudentGpas(studentIds, startDate, endDate));
    }

    @Override
    public ServiceResponse<Collection<Gpa>> getAllCurrentGpas(Long schoolId) {
        return new ServiceResponse<>(
                gpaPersistence.selectAllCurrentGpas(schoolId));
    }

    @Override
    public ServiceResponse<Long> updateGpa(long studentId, long gpaId, Gpa gpa) {
        StatusCode code = pm.getStudentManager().studentExists(studentId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        return new ServiceResponse<>(gpaPersistence.updateGpa(studentId, gpaId, gpa));
    }

    @Override
    public ServiceResponse<Void> deleteGpa(long studentId, long goalId) {
        StatusCode code = pm.getStudentManager().studentExists(studentId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        gpaPersistence.delete(studentId, goalId);
        return new ServiceResponse<>((Void)null);
    }
}
