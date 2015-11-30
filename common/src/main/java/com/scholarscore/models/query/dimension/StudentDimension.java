package com.scholarscore.models.query.dimension;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableSet;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.user.Student;

import java.util.Set;

@SuppressWarnings("serial")
public class StudentDimension implements IDimension {
    public static final String ID = "ID";
    public static final String NAME = "Name";
    public static final String GENDER = "Gender";
    public static final String AGE = "Age";
    public static final String ETHNICITY = "Ethnicity";
    public static final String RACE = "Race";
    public static final String CITY_OF_RESIDENCE = "City of Residence";
    public static final String HOME_ADDRESS = "Home Address";
    public static final String MAILING_ADDRESS = "Mailing Address";
    public static final String PROJECTED_GRADUATION_YEAR = "Proj. Graduation Year";
    public static final String SCHOOL = "School";
    public static final Set<String> DIMENSION_FIELDS = 
            ImmutableSet.of(ID, NAME, GENDER, AGE, ETHNICITY, RACE, CITY_OF_RESIDENCE, 
                    HOME_ADDRESS, MAILING_ADDRESS, PROJECTED_GRADUATION_YEAR, SCHOOL);
    public static final Set<Dimension> PARENT_DIMENSIONS = 
            ImmutableSet.of(Dimension.SCHOOL, Dimension.GRADE_LEVEL);
    
    @Override
    public Dimension getType() {
        return Dimension.STUDENT;
    }

    @Override
    @JsonIgnore
    public Class<?> getAssociatedClass() {
        return Student.class;
    }

    @Override
    public Set<Dimension> getParentDimensions() {
        return StudentDimension.PARENT_DIMENSIONS;
    }

    @Override
    public Set<String> getFields() {
        return StudentDimension.DIMENSION_FIELDS;
    }

    @Override
    public String getName() {
        return "Student";
    }
}
