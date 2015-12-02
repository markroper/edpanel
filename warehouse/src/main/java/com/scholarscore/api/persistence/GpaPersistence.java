package com.scholarscore.api.persistence;

import com.scholarscore.models.gpa.Gpa;

import java.time.LocalDate;
import java.util.List;

/**
 * @author markroper on 11/24/15.
 */
public interface GpaPersistence {

    public Long insertGpa(long studentId, Gpa gpa);

    public Long updateGpa(long studentId, long gpaId, Gpa gpa);

    public Gpa selectGpa(long studentId);

    public List<Gpa> selectAllCurrentGpas(Long schoolId);

    public List<Gpa> selectStudentGpas(List<Long> studentIds, LocalDate startDate, LocalDate endDate);

    public void delete(long studentId, long gpaId);
}
