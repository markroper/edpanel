package com.scholarscore.models.query.measure;

import com.google.common.collect.ImmutableSet;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.Measure;

import java.util.Set;

/**
 * Created by markroper on 2/11/16.
 */
public class ReferralMeasure extends BehaviorMeasure implements IMeasure {
    final Set<Measure> compatibleMeasures = ImmutableSet.of(Measure.DEMERIT, Measure.MERIT, Measure.DETENTION, Measure.SUSPENSION);
    final Set<Dimension> compatibleDimensions = ImmutableSet.of(Dimension.TERM, Dimension.YEAR,
            Dimension.STUDENT, Dimension.TEACHER, Dimension.SCHOOL, Dimension.GRADE_LEVEL);
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
        return Measure.REFERRAL;
    }

    @Override
    public String getName() {
        return Measure.REFERRAL.name();
    }
}
