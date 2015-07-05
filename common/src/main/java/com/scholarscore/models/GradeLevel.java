package com.scholarscore.models;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.DimensionField;

/**
 * This class represents a grade level. For example, the sixth grade.
 * Each instance of a grade level has unique ID within the school district.
 * 
 * For example, the sixth grade for the 2011-2012 school year at North Central 
 * High School has a unique ID that is not repeated in the containing school district.
 * 
 * @author markroper
 *
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GradeLevel extends ApiModel implements Serializable, IApiModel<GradeLevel>{
    public static final DimensionField ID = new DimensionField(Dimension.GRADE_LEVEL, "ID");
    public static final DimensionField NAME = new DimensionField(Dimension.GRADE_LEVEL, "Name");
    public static final Set<DimensionField> DIMENSION_FIELDS = new HashSet<DimensionField>() {{
        add(ID);
        add(NAME);
    }};
    
    public GradeLevel() {
        
    }
    
    public GradeLevel(GradeLevel year) {
        super(year);
    }
    
    @Override
    public void mergePropertiesIfNull(GradeLevel mergeFrom) {
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
