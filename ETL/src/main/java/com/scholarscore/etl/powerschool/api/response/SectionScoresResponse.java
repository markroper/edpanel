package com.scholarscore.etl.powerschool.api.response;

import com.scholarscore.etl.powerschool.api.model.section.PsSectionScores;

import java.util.List;

/**
 * Created by markroper on 10/22/15.
 */
public class SectionScoresResponse {
    public String name;
    public List<PsSectionScores> record;
}
