package com.scholarscore.etl.powerschool.api.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by mattg on 7/3/15.
 */
@XmlRootElement(name = "courses")
public class Courses {
    public Course course;
}
