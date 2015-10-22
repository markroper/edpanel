package com.scholarscore.etl.powerschool.api.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by mattg on 6/28/15.
 */
@XmlRootElement(name = "school")
public class PsSchool {
    public Long id;
    public String name;
    public String school_number;
    public String state_province_id;
    public Long low_grade;
    public Long high_grade;
    public Long alternate_school_number;

    public PsAddresses addresses;
    public PsPerson principal;
    public PsPerson assistant_principal;
    public PsPhones phones;

    @Override
    public String toString() {
        return "PsSchool{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", school_number='" + school_number + '\'' +
                ", state_province_id='" + state_province_id + '\'' +
                ", low_grade=" + low_grade +
                ", high_grade=" + high_grade +
                ", alternate_school_number=" + alternate_school_number +
                ", addresses=" + addresses +
                ", principal=" + principal +
                ", assistant_principal=" + assistant_principal +
                ", phones=" + phones +
                '}';
    }
}
