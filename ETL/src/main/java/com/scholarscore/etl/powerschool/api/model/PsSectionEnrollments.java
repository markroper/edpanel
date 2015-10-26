package com.scholarscore.etl.powerschool.api.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "section_enrollments")
public class PsSectionEnrollments {
    public List<PsSectionEnrollment> section_enrollment;
}
