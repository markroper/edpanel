package com.scholarscore.etl.powerschool.api.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "section_enrollments")
public class PsSectionEnrollments {
    public List<PsSectionEnrollment> section_enrollment;
}
