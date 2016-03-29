package com.scholarscore.etl.powerschool.sync.student.gpa;

/**
 * Created by mattg on 11/24/15.
 */
public enum GpaType {
    SIMPLE,
    SIMPLE_PERCENT,
    ADDED_VALUE,
    SIMPLE_ADDED_VALUE;

    public static GpaType fromString(String value) {
        for (GpaType type : values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}
