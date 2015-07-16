package com.scholarscore.etl.powerschool.api.model;

/**
 * Created by mattg on 6/28/15.
 */
public class Name {
    public String first_name;
    public String middle_name;
    public String last_name;

    @Override
    public String toString() {
        return first_name + " " + middle_name + " " + last_name;
    }
}
