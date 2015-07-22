package com.scholarscore.models.query.measure;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.Measure;

@SuppressWarnings("serial")
public class HomeworkCompletionMeasure implements IMeasure {
    Set<String> compatibleMeasures = Collections.unmodifiableSet(new HashSet<String>());
    Set<Dimension> compatibleDimensions = ImmutableSet.of(
            Dimension.TERM, Dimension.STUDENT, Dimension.SCHOOL, Dimension.SECTION, 
            Dimension.YEAR, Dimension.GRADE_LEVEL, Dimension.TEACHER);
    
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
        return Measure.HW_COMPLETION;
    }

    @Override
    public String getName() {
        return "Homework Completion";
    }

}