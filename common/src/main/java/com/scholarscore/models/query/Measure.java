package com.scholarscore.models.query;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import com.google.common.collect.ImmutableSet;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Enumerates the supported measures, or scalar values, supported within the 
 * reporting engine.  Think of these as the value aggregated in a SQL query, 
 * it should be reduced to to a numeric value, the thing being counted.
 * 
 * Each enum value contains an array of the associated available classes
 * that can be used to resolve additional information about the Measure and its
 * use.  The enum also contains an array of Dimensions that are incompatible with 
 * that Measure.
 * 
 * Invalid Dimensions & Measure combinations:
 * 1) GPA & ATTENDENCE Measures cannot be paired with TEACHER Dimension in a query 
 * because there is no valid join between either of these and the teacher table.
 * 2) TODO: identify any other invalid combinations?
 * 
 * @author markroper
 *
 */
@SuppressWarnings("serial")
public enum Measure {
    ATTENDANCE(
        Collections.unmodifiableSet(new HashSet<String>()),
        Collections.unmodifiableSet(new HashSet<Dimension>(){{ 
            add(Dimension.TERM); add(Dimension.YEAR); add(Dimension.STUDENT); 
            add(Dimension.SCHOOL); add(Dimension.GRADE_LEVEL);
        }})),
    //Behavioral measures
    DEMERITS(
        ImmutableSet.of("MERITS", "DETENSIONS", "SUSPENSIONS"),  
        Collections.unmodifiableSet(new HashSet<Dimension>(){{ 
            add(Dimension.TERM); add(Dimension.YEAR); add(Dimension.STUDENT); 
            add(Dimension.SCHOOL); add(Dimension.GRADE_LEVEL);
            add(Dimension.TEACHER); add(Dimension.SECTION);
        }})),
    MERITS(
        ImmutableSet.of("DEMERITS", "DETENSIONS", "SUSPENSIONS"), 
        Collections.unmodifiableSet(new HashSet<Dimension>(){{ 
            add(Dimension.TERM); add(Dimension.YEAR); add(Dimension.STUDENT); 
            add(Dimension.SCHOOL); add(Dimension.GRADE_LEVEL);
            add(Dimension.TEACHER); add(Dimension.SECTION);
        }})),
    DETENTIONS(
            ImmutableSet.of("MERITS", "DEMERITS", "SUSPENSIONS"), 
        Collections.unmodifiableSet(new HashSet<Dimension>(){{ 
            add(Dimension.TERM); add(Dimension.YEAR); add(Dimension.STUDENT); 
            add(Dimension.SCHOOL); add(Dimension.GRADE_LEVEL);
            add(Dimension.TEACHER); add(Dimension.SECTION);
        }})),
    HOMEWORK_CLUBS(
        Collections.unmodifiableSet(new HashSet<String>()),
        Collections.unmodifiableSet(new HashSet<Dimension>(){{ 
            add(Dimension.TERM); add(Dimension.YEAR); add(Dimension.STUDENT); 
            add(Dimension.SCHOOL); add(Dimension.GRADE_LEVEL);
        }})),
    SUSPENSIONS(
            ImmutableSet.of("MERITS", "DETENSIONS", "DEMERITS"), 
        Collections.unmodifiableSet(new HashSet<Dimension>(){{ 
            add(Dimension.TERM); add(Dimension.YEAR); add(Dimension.STUDENT); 
            add(Dimension.SCHOOL); add(Dimension.GRADE_LEVEL);
            add(Dimension.TEACHER); add(Dimension.SECTION);
        }})),
    //Academic measures
    GPA(
        Collections.unmodifiableSet(new HashSet<String>()),
        Collections.unmodifiableSet(new HashSet<Dimension>(){{ 
            add(Dimension.TERM); add(Dimension.YEAR); add(Dimension.STUDENT); 
            add(Dimension.SCHOOL); add(Dimension.GRADE_LEVEL);
        }})),
    COURSE_GRADE(
        Collections.unmodifiableSet(new HashSet<String>()),
        Collections.unmodifiableSet(new HashSet<Dimension>(){{ 
            add(Dimension.TERM); add(Dimension.YEAR); add(Dimension.STUDENT); 
            add(Dimension.SCHOOL); add(Dimension.GRADE_LEVEL);
            add(Dimension.SECTION);
        }})),
    ASSIGNMENT_GRADE(
        Collections.unmodifiableSet(new HashSet<String>()),
        Collections.unmodifiableSet(new HashSet<Dimension>(){{ 
            add(Dimension.STUDENT); add(Dimension.SECTION);
        }})),
    HW_COMPLETION(
        Collections.unmodifiableSet(new HashSet<String>()),   
        Collections.unmodifiableSet(new HashSet<Dimension>(){{ 
            add(Dimension.TERM); add(Dimension.YEAR); add(Dimension.STUDENT); 
            add(Dimension.SCHOOL); add(Dimension.GRADE_LEVEL);
            add(Dimension.TEACHER); add(Dimension.SECTION);
        }}));
    
    private Set<String> compatibleMeasures;
    private Set<Dimension> compatibleDimensions;
    
    private Measure(Set<String> compatibleMeasures, Set<Dimension> compatibleDimensions) {
        this.compatibleDimensions = compatibleDimensions;
        this.compatibleMeasures = compatibleMeasures;
    }

    @JsonIgnore
    public Set<Dimension> getCompatibleDimensions() {
        return compatibleDimensions;
    }
    
    @JsonIgnore
    public Set<String> getCompatibleMeasures() {
        return compatibleMeasures;
    }
}