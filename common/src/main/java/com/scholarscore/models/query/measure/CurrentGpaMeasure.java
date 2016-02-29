package com.scholarscore.models.query.measure;

import com.google.common.collect.ImmutableSet;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.Measure;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by markroper on 2/9/16.
 */
public class CurrentGpaMeasure extends BaseMeasure implements IMeasure {
    public static final String GPA = "GPA";
    public static final String STUDENT = "Student";
    private static final Set<String> FIELDS =
            ImmutableSet.of(GPA, STUDENT);

    final Set<Measure> compatibleMeasures = Collections.unmodifiableSet(new HashSet<Measure>(){{ add(Measure.GPA); }});
    final Set<Dimension> compatibleDimensions = ImmutableSet.of(Dimension.STUDENT);

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
        return null;
    }

    @Override
    public String getName() {
        return "Current GPA";
    }

    @Override
    public Set<String> getFields() {
        return FIELDS;
    }
}
