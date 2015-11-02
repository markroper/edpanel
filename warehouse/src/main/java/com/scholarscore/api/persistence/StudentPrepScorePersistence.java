package com.scholarscore.api.persistence;

import com.scholarscore.models.PrepScore;

import java.util.Date;
import java.util.List;

/**
 * User: jordan
 * Date: 11/1/15
 * Time: 11:30 PM
 */
public interface StudentPrepScorePersistence {
    List<PrepScore> selectStudentPrepScore(Long[] studentId, Date startDate, Date endDate);
}
