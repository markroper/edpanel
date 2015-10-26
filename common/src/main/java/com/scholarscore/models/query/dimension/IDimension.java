package com.scholarscore.models.query.dimension;

import com.scholarscore.models.query.Dimension;

import java.io.Serializable;
import java.util.Set;
/**
 * Implementations represent a single eligible dimension field for use in a query.
 * Dimensions are the select columns that are included in the group by clause and have
 * no aggregate functions performed on them.
 * 
 * @author markroper
 *
 */
public interface IDimension extends Serializable {
    public Dimension getType();
    
    public Class<?> getAssociatedClass();
    
    public Set<Dimension> getParentDimensions();
    
    public Set<String> getFields();
    
    public String getName();
}
