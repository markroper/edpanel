package com.scholarscore.models.query.measure;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.Measure;

@SuppressWarnings("serial")
public class CourseGradeMeasure implements IMeasure {
    Set<String> compatibleMeasures = Collections.unmodifiableSet(new HashSet<String>());
    Set<Dimension> compatibleDimensions = ImmutableSet.of(Dimension.STUDENT, Dimension.SECTION);
    
    @Override
    public Set<Dimension> getCompatibleDimensions() {
        return compatibleDimensions;
    }

    @Override
    public Set<String> getCompatibleMeasures() {
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

}