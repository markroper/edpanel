package com.scholarscore.models.query;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.scholarscore.models.Course;
import com.scholarscore.models.GradeLevel;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.Student;
import com.scholarscore.models.SubjectArea;
import com.scholarscore.models.Teacher;
import com.scholarscore.models.Term;

/**
 * Enumerates the supported dimensions of the warehouse report and querying model.
 * Think of dimensions as those attributes you might GROUP BY in an aggregate SELECT
 * statement in SQL.
 * 
 * Each enum value contains an array of the associated available classes
 * that can be used to resolve the specific fields available for that enum.
 * 
 * @author markroper
 */
@SuppressWarnings("serial")
public enum Dimension {
    COURSE (Course.class,
            new HashSet<Dimension>(){{ add(Dimension.SCHOOL); add(Dimension.SUBJECT_AREA); }}),
    SECTION (Section.class,
            new HashSet<Dimension>(){{add(Dimension.COURSE); add(Dimension.TERM); }}),
    TERM (Term.class,
          new HashSet<Dimension>(){{ add(Dimension.YEAR); }}),
    YEAR (SchoolYear.class,
            new HashSet<Dimension>(){{ add(Dimension.SCHOOL); }}),
    SUBJECT_AREA (SubjectArea.class,
            new HashSet<Dimension>(){{ add(Dimension.SCHOOL); }}),
    GRADE_LEVEL (GradeLevel.class,
            new HashSet<Dimension>(){{ add(Dimension.SCHOOL); }}),
    SCHOOL (School.class,
            new HashSet<Dimension>(){{ }}),
    TEACHER (Teacher.class,
            new HashSet<Dimension>(){{ add(Dimension.SCHOOL); }}),
    STUDENT (Student.class,
            new HashSet<Dimension>(){{ add(Dimension.SCHOOL); add(Dimension.GRADE_LEVEL); }}),
    //Date is a psuedo-dimension that can be used in conjunction with only date-sensitive measures like attendance
    DATE (Date.class,
            new HashSet<Dimension>());
    
    private Class<?> associatedClass;
    private Set<Dimension> parentDimensions;

    private Dimension(Class<?> associatedClass, Set<Dimension> parents) {
        this.associatedClass = associatedClass;
        this.parentDimensions = parents;
    }
    
    @JsonValue
    public String toValue() {
        return this.toString();
    }
    @JsonIgnore
    public Class<?> getAssociatedClass() {
        return associatedClass;
    }
    
    @JsonIgnore
    public Set<Dimension> getParentDimensions() {
        return parentDimensions;
    }
    
    private static final List<Dimension> orderedDimensions = new ArrayList<Dimension>(){{
        add(Dimension.STUDENT);
        add(Dimension.TEACHER);
        add(Dimension.SECTION);
        add(Dimension.TERM);
        add(Dimension.YEAR);
        add(Dimension.COURSE);
        add(Dimension.SUBJECT_AREA);
        add(Dimension.GRADE_LEVEL);
        add(Dimension.SCHOOL);
    }};
    /**
     * Given a set of Dimensions, orders supported dimension from most to least granular in a List,
     * and returns that list. Certain dimensions like Date are not supported and are ignored if they are 
     * included in the input set.
     * 
     * @param selectedDims
     * @return
     */
    public static List<Dimension> resolveOrderedDimensions(Set<Dimension> selectedDims) {
        List<Dimension> orderedDimTables = new ArrayList<>();
        
        for(Dimension d : orderedDimensions) {
            if(selectedDims.contains(d)) {
                orderedDimTables.add(d);
            }
        }
        return orderedDimTables;
    }
}