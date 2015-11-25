package com.scholarscore.api.manager;

import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.goal.Goal;
import com.scholarscore.models.gpa.Gpa;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * Manager interface for supported operations on student GPA values.
 *
 * Note that EdPanel can store multiple GPAs per student but only one GPA per calculationDate, studentId, goalType
 * combination.  There is a separate object CurrentGpa which maps to a current_gpa database table that tracks,
 * for each student the current GPA value.  Creates, updates and deletes need to handle updating og the current GPA
 * where relevant.
 *
 * Created by markroper on 11/24/15.
 */
public interface GpaManager {

    public ServiceResponse<Long> createGpa(long studentId, Gpa goal);

    public ServiceResponse<Gpa> getGpa(long studentId);

    public ServiceResponse<Collection<Gpa>> getAllGpasForStudents(
            List<Long> studentIds, LocalDate startDate, LocalDate endDate);

    public ServiceResponse<Long> updateGpa(long studentId, long gpaId, Gpa gpa);

    public ServiceResponse<Void> deleteGpa(long studentId, long goalId);
}
