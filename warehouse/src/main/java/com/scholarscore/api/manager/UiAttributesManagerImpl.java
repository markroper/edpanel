package com.scholarscore.api.manager;

import java.util.List;

import com.scholarscore.api.persistence.TeacherPersistence;
import com.scholarscore.api.persistence.UiAttributePersistence;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.UiAttributes;

public class UiAttributesManagerImpl implements UiAttributesManager {
    private UiAttributePersistence uiAttributePersistence;

    private OrchestrationManager pm;

    private static final String UI_ATTRIBUTES = "UI attributes";

    public void setTeacherPersistence(UiAttributePersistence uiAttributePersistence) {
        this.uiAttributePersistence = uiAttributePersistence;
    }

    public void setPm(OrchestrationManager pm) {
        this.pm = pm;
    }
    
    @Override
    public ServiceResponse<UiAttributes> getUiAttributes(Long schoolId) {
        StatusCode code = pm.getSchoolManager().schoolExists(schoolId);
        if(!code.isOK()) {
            return new ServiceResponse<UiAttributes>(code);
        }
        UiAttributes attrs = uiAttributePersistence.select(schoolId);
        if(null == attrs) {
            code = StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{UI_ATTRIBUTES, schoolId});
            return new ServiceResponse<UiAttributes>(code);
        }
        return new ServiceResponse<UiAttributes>(attrs);
    }

    @Override
    public ServiceResponse<Long> createUiAttributes(Long schoolId,
            UiAttributes attrs) {
        StatusCode code = pm.getSchoolManager().schoolExists(schoolId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        uiAttributePersistence.createUiAttributes(schoolId, attrs);
        return new ServiceResponse<Long>((Long)null);
    }

    @Override
    public ServiceResponse<Long> replaceUiAttributes(Long schoolId,
            UiAttributes attrs) {
        StatusCode code = pm.getSchoolManager().schoolExists(schoolId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        uiAttributePersistence.replaceUiAttributes(schoolId, attrs);
        return new ServiceResponse<Long>((Long)null);
    }

}
