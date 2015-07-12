package com.scholarscore.etl.powerschool.api.model;

/**
 * Created by mattg on 6/28/15.
 */
public class Name {
    String first_name;
    String middle_name;
    String last_name;

    @Override
    public String toString() {
        return first_name + " " + middle_name + " " + last_name;
    }
}
