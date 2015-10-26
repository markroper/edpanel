package com.scholarscore.models.query.measure;

import com.google.common.collect.ImmutableSet;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.Measure;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("serial")
public class AttendanceMeasure implements IMeasure {
    final Set<Measure> compatibleMeasures = Collections.unmodifiableSet(new HashSet<Measure>());
    final Set<Dimension> compatibleDimensions = ImmutableSet.of(Dimension.STUDENT, Dimension.SCHOOL);
    
    public static final String ID = "ID";
    public static final String STUDENT_FK = "Student";
    public static final String SCHOOL_FK = "School";
    public static final String DATE = "Date";
    public static final String STATUS = "Status";
    public static final String STATUS_DESCRIPTION = "Description";
    private static final Set<String> fields = 
            ImmutableSet.of(ID, STUDENT_FK, SCHOOL_FK, DATE, STATUS, STATUS_DESCRIPTION);

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
        return Measure.ATTENDANCE;
    }

    @Override
    public String getName() {
        return "Attendance Count";
    }

    @Override
    public Set<String> getFields() {
        return fields;
    }

}
