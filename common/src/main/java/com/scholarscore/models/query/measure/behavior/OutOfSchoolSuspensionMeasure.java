package com.scholarscore.models.query.measure.behavior;

import com.google.common.collect.ImmutableSet;
import com.scholarscore.models.BehaviorCategory;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.Measure;
import com.scholarscore.models.query.measure.IMeasure;
import com.scholarscore.models.query.measure.behavior.BehaviorMeasure;

import java.util.Set;

@SuppressWarnings("serial")
public class OutOfSchoolSuspensionMeasure extends BehaviorMeasure implements IMeasure {
    final Set<Measure> compatibleMeasures = ImmutableSet.of(Measure.DEMERIT, Measure.MERIT, Measure.DETENTION, Measure.REFERRAL);

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
        return Measure.OUT_OF_SCHOOL_SUSPENSION;
    }

    @Override
    public String getName() {
        return BehaviorCategory.OUT_OF_SCHOOL_SUSPENSION.toString();
    }

}
