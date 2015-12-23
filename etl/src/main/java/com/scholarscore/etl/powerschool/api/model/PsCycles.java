package com.scholarscore.etl.powerschool.api.model;

import com.scholarscore.etl.powerschool.api.response.ITranslateCollection;
import com.scholarscore.models.Cycle;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by cwallace on 12/23/15.
 */
public class PsCycles extends ArrayList<PsCycle> implements ITranslateCollection<Cycle> {
    @Override
    public Collection<Cycle> toInternalModel() {
        return null;
    }
}
