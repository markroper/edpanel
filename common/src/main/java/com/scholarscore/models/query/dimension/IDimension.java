package com.scholarscore.models.query.dimension;

import java.io.Serializable;
import java.util.Set;

import com.scholarscore.models.query.Dimension;

public interface IDimension extends Serializable {
    public Dimension getType();
    
    public Class<?> getAssociatedClass();
    
    public Set<Dimension> getParentDimensions();
    
    public Set<String> getFields();
}
