package com.scholarscore.api.manager;

import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.UiAttributes;

public interface UiAttributesManager {
    ServiceResponse<UiAttributes> getUiAttributes(Long schoolId);
    ServiceResponse<Long> createUiAttributes(Long schoolId, UiAttributes attrs);
    ServiceResponse<Long> replaceUiAttributes(Long schoolId, UiAttributes attrs);
}
