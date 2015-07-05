package com.scholarscore.models;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.DimensionField;

/**
 * The class represents a school subject area, for example history, or biology.
 * An instance of a subject area within a school will have an ID that is unique 
 * at the district level.
 * 
 * @author markroper
 *
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubjectArea extends ApiModel implements Serializable, IApiModel<SubjectArea>{
    public static final DimensionField ID = new DimensionField(Dimension.SUBJECT_AREA, "ID");
    public static final DimensionField NAME = new DimensionField(Dimension.SUBJECT_AREA, "Name");
    public static final Set<DimensionField> DIMENSION_FIELDS = new HashSet<DimensionField>() {{
        add(ID);
        add(NAME);
    }};
    
    public SubjectArea() {
        super();
    }
    
    @Override
    public void mergePropertiesIfNull(SubjectArea mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom); 
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!super.equals(obj)) {
            return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode();
    }
}
