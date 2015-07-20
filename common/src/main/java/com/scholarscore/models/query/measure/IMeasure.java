package com.scholarscore.models.query.measure;

import java.io.Serializable;
import java.util.Set;

import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.Measure;

public interface IMeasure extends Serializable {
    public Set<Dimension> getCompatibleDimensions();
    public Set<String> getCompatibleMeasures();
    public Measure getMeasure();
}
