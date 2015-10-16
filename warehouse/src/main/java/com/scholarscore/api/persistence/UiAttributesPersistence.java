package com.scholarscore.api.persistence;

import com.scholarscore.models.UiAttributes;

public interface UiAttributesPersistence {
    public UiAttributes select(long studentId);

    public Long createUiAttributes(long schoolId, UiAttributes attrs);

    public Long replaceUiAttributes(long schoolId, UiAttributes attrs);

}
