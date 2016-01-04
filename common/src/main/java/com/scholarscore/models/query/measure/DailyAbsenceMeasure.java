package com.scholarscore.models.query.measure;

import com.google.common.collect.ImmutableSet;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.Measure;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by markroper on 11/29/15.
 */
public class DailyAbsenceMeasure implements IMeasure {
    final Set<Measure> compatibleMeasures = Collections.unmodifiableSet(
            new HashSet<Measure>(){{ add(Measure.ATTENDANCE); add(Measure.TARDY); }});
    final Set<Dimension> compatibleDimensions = ImmutableSet.of(Dimension.STUDENT, Dimension.SCHOOL,
            Dimension.SECTION);
    public static final String DATE = "Date";
    public static final String TYPE = "Type";
    private static final Set<String> fields =
            ImmutableSet.of(DATE, TYPE);


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
        return Measure.ABSENCE;
    }

    @Override
    public String getName() {
        return "Absense Count";
    }

    @Override
    public Set<String> getFields() {
        return fields;
    }
}
