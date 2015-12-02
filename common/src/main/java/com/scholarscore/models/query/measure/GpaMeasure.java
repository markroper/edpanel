package com.scholarscore.models.query.measure;

import com.google.common.collect.ImmutableSet;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.Measure;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("serial")
public class GpaMeasure implements IMeasure {
    public static final String DATE = "Date";
    public static final String TYPE = "Type";
    public static final String STUDENT = "Student";
    private static final Set<String> FIELDS =
            ImmutableSet.of(DATE, TYPE, STUDENT);

    final Set<Measure> compatibleMeasures = Collections.unmodifiableSet(new HashSet<Measure>());
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
        return Measure.GPA;
    }

    @Override
    public String getName() {
        return "GPA";
    }

    @Override
    public Set<String> getFields() {
        return FIELDS;
    }

}
