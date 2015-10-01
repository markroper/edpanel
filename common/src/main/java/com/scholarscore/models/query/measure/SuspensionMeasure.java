package com.scholarscore.models.query.measure;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.Measure;

@SuppressWarnings("serial")
public class SuspensionMeasure extends BehaviorMeasure implements IMeasure {
    final Set<Measure> compatibleMeasures = ImmutableSet.of(Measure.DEMERIT, Measure.MERIT, Measure.DETENTION);
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
        return Measure.SUSPENSION;
    }

    @Override
    public String getName() {
        return Measure.SUSPENSION.name();
    }

}
