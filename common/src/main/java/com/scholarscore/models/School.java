package com.scholarscore.models;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.DimensionField;

/**
 * The class represents a single school within a school district.
 * 
 * @author markroper
 *
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class School extends ApiModel implements Serializable, IApiModel<School>{
    public static final DimensionField ID = new DimensionField(Dimension.SCHOOL, "ID");
    public static final DimensionField NAME = new DimensionField(Dimension.SCHOOL, "Name");
    public static final DimensionField ADDRESS = new DimensionField(Dimension.SCHOOL, "Address");
    
    public static final Set<DimensionField> DIMENSION_FIELDS = new HashSet<DimensionField>() {{
        add(ID);
        add(NAME);
        add(ADDRESS);
    }};
    
    
    List<SchoolYear> years;
    
    public School() {
        super();
    }
    
    public School(School clone) {
        super(clone);
        this.years = clone.years;
    }
    
    public List<SchoolYear> getYears() {
        return years;
    }

    public void setYears(List<SchoolYear> years) {
        this.years = years;
    }

    @Override
    public void mergePropertiesIfNull(School mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);
        if (mergeFrom == null) { return; }
        if(null == years) {
            this.years = mergeFrom.years;
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!super.equals(obj)) {
            return false;
        }
        final School other = (School) obj;
        return Objects.equals(this.years, other.years);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(years);
    }

}
