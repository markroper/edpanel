package com.scholarscore.models.query;

import com.scholarscore.models.query.dimension.IDimension;
import com.scholarscore.models.query.measure.IMeasure;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * This class encapsulates all Dimensions and Measures available for use in a valid Query.  It serializes to a form
 * that is consumable by a javascript front end and includes the dependency graph information needed by the front end
 * to generate and validate query objects.
 * 
 * In order to support building of arbitrary queries, client code will request an instance of this object, 
 * inflate a relationship graph between the entities, and use this object to create the UI for building arbitrary 
 * queries to the system.
 * 
 * @author markroper
 *
 */
@SuppressWarnings("serial")
public class QueryComponents implements Serializable {

    
    protected final Set<IDimension> availableDimensions = buildAvailableDimensions();
    protected final Set<IMeasure> availableMeasures = buildAvailableMeasures();
    
    public Set<IDimension> buildAvailableDimensions() { 
        HashSet<IDimension> toReturn = new HashSet<>();
        for (Dimension dimension : Dimension.values()) {
            toReturn.add(dimension.buildDimension());
        }
        return toReturn;
    }
    
    public Set<IMeasure> buildAvailableMeasures()  {
        HashSet<IMeasure> toReturn = new HashSet<>();
        for (Measure measure : Measure.values()) {
            toReturn.add(measure.buildMeasure());
        }
        return toReturn;
    }
    
    public Set<IDimension> getAvailableDimensions() {
        return availableDimensions;
    }
    
    public Set<IMeasure> getAvailableMeasures() {
        return availableMeasures;
    }
}
