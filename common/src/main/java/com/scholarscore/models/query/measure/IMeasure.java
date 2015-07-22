package com.scholarscore.models.query.measure;

import java.io.Serializable;
import java.util.Set;

import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.Measure;

/**
 * Implementations represent a single valid query measure (aggregate field within a SQL query) including
 * methods returning compatible dimensions with the measure, other compatible measures, the associated enum value
 * and a human readable name for the measure.
 * 
 * @author markroper
 *
 */
public interface IMeasure extends Serializable {
    
    public Set<Dimension> getCompatibleDimensions();
    
    public Set<String> getCompatibleMeasures();
    
    public Measure getMeasure();
    
    public String getName();
}