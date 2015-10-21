package com.scholarscore.etl.powerschool.api.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "section_enrollments")
public class SectionEnrollments {
    public List<SectionEnrollment> section_enrollment;
}
