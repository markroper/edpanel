package com.scholarscore.models.query.measure;

import com.google.common.collect.ImmutableSet;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.Measure;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("serial")
public class CourseGradeMeasure implements IMeasure {
    public static final String GRADE = "Grade";
    private static final Set<String> FIELDS =
            ImmutableSet.of(GRADE);
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
        return Measure.COURSE_GRADE;
    }

    @Override
    public String getName() {
        return "Course Grade";
    }

    @Override
    public Set<String> getFields() {
        return FIELDS;
    }

}
