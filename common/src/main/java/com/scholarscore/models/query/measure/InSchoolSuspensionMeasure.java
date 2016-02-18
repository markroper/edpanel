package com.scholarscore.models.query.measure;

import com.google.common.collect.ImmutableSet;
import com.scholarscore.models.BehaviorCategory;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.Measure;

import java.util.Set;

@SuppressWarnings("serial")
public class InSchoolSuspensionMeasure extends BehaviorMeasure implements IMeasure {
    final Set<Measure> compatibleMeasures = ImmutableSet.of(Measure.DEMERIT, Measure.MERIT, Measure.DETENTION, Measure.REFERRAL);
    final Set<Dimension> compatibleDimensions = ImmutableSet.of(Dimension.TERM, Dimension.YEAR, 
            Dimension.STUDENT, Dimension.TEACHER, Dimension.SCHOOL);
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
        return Measure.IN_SCHOOL_SUSPENSION;
    }

    @Override
    public String getName() {
        return BehaviorCategory.IN_SCHOOL_SUSPENSION.toString();
    }

}
