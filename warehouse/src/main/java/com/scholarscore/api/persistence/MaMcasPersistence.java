package com.scholarscore.api.persistence;

import com.scholarscore.models.state.ma.McasResult;

import java.util.List;

/**
 * Created by markroper on 4/10/16.
 */
public interface MaMcasPersistence {
    List<McasResult> selectMcasForStudent(Long schoolId, Long studentId);
    McasResult select(Long mcasId);
    Long insertMcasResult(Long schoolId, Long studentId, McasResult result);
    List<Long> insertMcasResults(List<McasResult> results);
    void replaceMcasResult(Long schoolId, Long studentId, Long mcasId, McasResult result);
    void deleteMcasResult(Long schoolId, Long studentId, Long mcasId);
}
