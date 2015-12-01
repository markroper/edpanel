package com.scholarscore.etl.powerschool.sync.student.gpa;

/**
 * Created by mattg on 11/24/15.
 */
public enum GPAType {
    simple,
    simple_percent,
    added_value;

    public static GPAType fromString(String value) {
        for (GPAType type : values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}
