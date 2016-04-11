package com.scholarscore.api.manager;

import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.state.ma.McasResult;

import java.util.List;

/**
 * Created by markroper on 4/10/16.
 */
public interface MaMcasManager {
    ServiceResponse<List<McasResult>> getAllMcasResultsForStudent(Long schoolId, Long studentId);
    ServiceResponse<McasResult> getMcasResult(Long schoolId, Long studentId, Long mcasId);
    ServiceResponse<Long> createMcasResult(Long schoolId, Long studentId, McasResult result);
    ServiceResponse<List<Long>> createMcasResults(List<McasResult> results);
    ServiceResponse<Void> replaceMcasResult(Long schoolId, Long studentId, Long mcasId, McasResult result);
    ServiceResponse<Void> deleteMcasResult(Long schoolId, Long studentId, Long mcasId);
}
