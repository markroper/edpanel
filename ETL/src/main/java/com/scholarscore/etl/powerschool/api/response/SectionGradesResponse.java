package com.scholarscore.etl.powerschool.api.response;

import com.scholarscore.etl.powerschool.api.model.section.PsSectionGrades;

import java.util.List;

/**
 * Created by markroper on 10/22/15.
 */
public class SectionGradesResponse {
    public String name;
    public List<PsSectionGrades> record;
}
