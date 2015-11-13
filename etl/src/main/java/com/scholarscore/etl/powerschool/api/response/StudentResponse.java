package com.scholarscore.etl.powerschool.api.response;

import com.scholarscore.etl.powerschool.api.model.student.PsStudent;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by markroper on 10/22/15.
 */
@XmlRootElement(name = "student")
public class StudentResponse {
    public PsStudent student;
}
