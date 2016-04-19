package com.scholarscore.api.manager;

import com.scholarscore.api.persistence.MaMcasPersistence;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.state.ma.McasResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by markroper on 4/10/16.
 */
public class MaMcasManagerImpl implements MaMcasManager {
    private final static Logger LOGGER = LoggerFactory.getLogger(MaMcasManagerImpl.class);

    @Autowired
    private MaMcasPersistence mcasPersistence;

    @Autowired
    private OrchestrationManager pm;

    public void setMcasPersistence(MaMcasPersistence mcasPersistence) {
        this.mcasPersistence = mcasPersistence;
    }

    public void setPm(OrchestrationManager pm) {
        this.pm = pm;
    }

    @Override
    public ServiceResponse<List<McasResult>> getAllMcasResultsForStudent(Long schoolId, Long studentId) {
        return new ServiceResponse<>(mcasPersistence.selectMcasForStudent(studentId));
    }

    @Override
    public ServiceResponse<McasResult> getMcasResult(Long schoolId, Long studentId, Long mcasId) {
        McasResult result = mcasPersistence.select(mcasId);
        if(null == result) {
            return new ServiceResponse<>(
                    StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{"MCAS result"}));
        }
        return new ServiceResponse<>(result);
    }

    @Override
    public ServiceResponse<Long> createMcasResult(Long schoolId, Long studentId, McasResult result) {
        return new ServiceResponse<>(mcasPersistence.insertMcasResult(schoolId, studentId, result));
    }

    @Override
    public ServiceResponse<List<Long>> createMcasResults(List<McasResult> results) {
        return new ServiceResponse<>(mcasPersistence.insertMcasResults(results));
    }

    @Override
    public ServiceResponse<Void> replaceMcasResult(Long schoolId, Long studentId, Long mcasId, McasResult result) {
        mcasPersistence.replaceMcasResult(schoolId, studentId, mcasId, result);
        return new ServiceResponse<>((Void) null);
    }

    @Override
    public ServiceResponse<Void> deleteMcasResult(Long schoolId, Long studentId, Long mcasId) {
        mcasPersistence.deleteMcasResult(schoolId, studentId, mcasId);
        return new ServiceResponse<>((Void) null);
    }
}
