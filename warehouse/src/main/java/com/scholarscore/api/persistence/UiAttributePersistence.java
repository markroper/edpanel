package com.scholarscore.api.persistence;

import com.scholarscore.models.UiAttributes;

public interface UiAttributePersistence {
    public UiAttributes select(long studentId);

    public void createUiAttributes(long schoolId, UiAttributes student);

    public void replaceUiAttributes(long schoolId, UiAttributes student);

}
