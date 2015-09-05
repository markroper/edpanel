package com.scholarscore.etl.powerschool.api.model;

import java.util.Optional;

/**
 * Created by mattg on 6/28/15.
 */
public class Name {
    public String first_name;
    public String middle_name;
    public String last_name;

    @Override
    public String toString() {
        return ((null != first_name) ? first_name + " " : "")
                + ((null != middle_name) ? middle_name + " " : "")
                + ((null != last_name) ? last_name : "");
    }
}
