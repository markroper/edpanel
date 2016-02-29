package com.scholarscore.models.query.measure;

import com.google.common.collect.ImmutableSet;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.Measure;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("serial")
public class AssignmentGradeMeasure extends BaseMeasure implements IMeasure {
    final Set<Measure> compatibleMeasures = Collections.unmodifiableSet(new HashSet<Measure>());
    final Set<Dimension> compatibleDimensions = ImmutableSet.of(Dimension.STUDENT, Dimension.SECTION);
    
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
        return Measure.ASSIGNMENT_GRADE;
    }

    @Override
    public String getName() {
        return "Assignment Grade";
    }

    @Override
    public Set<String> getFields() {
        return new HashSet<String>();
    }

}
