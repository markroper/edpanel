package com.scholarscore.etl.powerschool.api.response;

import com.scholarscore.etl.powerschool.api.model.assignment.scores.PsSectionScoreIds;

import java.util.List;

/**
 * Created by markroper on 10/22/15.
 */
public class SectionScoreIdsResponse {
    public String name;
    public List<PsSectionScoreIds> record;
}
