package com.scholarscore.models.query.measure;

import com.google.common.collect.ImmutableSet;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.Measure;
import com.scholarscore.models.query.dimension.GoalDimension;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by markroper on 3/4/16.
 */
public class GoalMeasure extends BaseMeasure {
    final Set<Measure> compatibleMeasures = Collections.unmodifiableSet(new HashSet<Measure>());
    final Set<Dimension> compatibleDimensions = ImmutableSet.of(Dimension.STUDENT, Dimension.STAFF, Dimension.SECTION);
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
        return Measure.GOAL;
    }

    @Override
    public String getName() {
        return "Goal";
    }

    @Override
    public Set<String> getFields() {
        return GoalDimension.DIMENSION_FIELDS;
    }

}
