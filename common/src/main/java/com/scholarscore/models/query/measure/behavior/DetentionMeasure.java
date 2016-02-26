package com.scholarscore.models.query.measure.behavior;

import com.google.common.collect.ImmutableSet;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.Measure;
import com.scholarscore.models.query.measure.IMeasure;
import com.scholarscore.models.query.measure.behavior.BehaviorMeasure;

import java.util.Set;

@SuppressWarnings("serial")
public class DetentionMeasure extends BehaviorMeasure implements IMeasure {
    final Set<Measure> compatibleMeasures = ImmutableSet.of(Measure.DEMERIT, Measure.MERIT, Measure.IN_SCHOOL_SUSPENSION,
            Measure.OUT_OF_SCHOOL_SUSPENSION, Measure.REFERRAL);
    
    @Override
    public Set<Dimension> getCompatibleDimensions() {
        return compatibleDimensions;
    }

    @Override
    public Set<Measure> getCompatibleMeasures() {
        return compatibleMeasures;
    }

    @Override
    public Measure getMeasure() {
        return Measure.DETENTION;
    }

    @Override
    public String getName() {
        return Measure.DETENTION.name();
    }

}