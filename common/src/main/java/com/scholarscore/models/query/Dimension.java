package com.scholarscore.models.query;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
    //TODO: return sets of field strings from the POJO static map
    COURSE (new HashSet<String>(){{ add("course.name"); add("course.id"); add("course.teacher"); }}, 
            new HashSet<Dimension>(){{ add(Dimension.SCHOOL); add(Dimension.SUBJECT_AREA); }}),
    SECTION (new HashSet<String>(){{ add("section.name"); add("section.enddate"); add("section.startdate"); 
                add("section.room"); add("section.gradeformula"); }}, 
            new HashSet<Dimension>(){{ add(Dimension.COURSE); add(Dimension.TERM); }}),
    TERM (new HashSet<String>(){{ add("term.name"); add("term.enddate"); add("term.startdate"); }}, 
            new HashSet<Dimension>(){{ add(Dimension.YEAR); }}),
    YEAR (new HashSet<String>(){{ add("year.name"); add("year.startdate"); add("year.enddate"); }}, 
            new HashSet<Dimension>(){{ add(Dimension.SCHOOL); }}),
    SUBJECT_AREA (new HashSet<String>(){{ add("subject.name"); add("subject.id"); }}, 
            new HashSet<Dimension>(){{ add(Dimension.SCHOOL); }}),
    GRADE_LEVEL (new HashSet<String>(){{ add("grade.name"); add("grade.id"); }}, 
            new HashSet<Dimension>(){{ add(Dimension.SCHOOL); }}),
    SCHOOL (new HashSet<String>(){{ add("school.name"); add("school.id"); }}, 
            new HashSet<Dimension>(){{ add(Dimension.SCHOOL); add(Dimension.DISTRICT); }}),
    DISTRICT (new HashSet<String>(){{ add("district.name"); add("district.id"); }}, 
            new HashSet<Dimension>()),
    TEACHER (new HashSet<String>(){{ add("teacher.name"); }}, 
            new HashSet<Dimension>(){{ add(Dimension.SCHOOL); }}),
    STUDENT (new HashSet<String>(){{ add("student.name"); add("student.gender"); add("student.freelunch"); 
                add("student.age"); add("student.graderepeater"); add("student.ethnicity"); add("student.race"); 
                add("student.ell"); add("student.specialed"); add("student.cityofresidence");  }}, 
            new HashSet<Dimension>(){{ add(Dimension.DISTRICT); add(Dimension.SCHOOL); add(Dimension.GRADE_LEVEL); }}),
    DATE (new HashSet<String>(){{ add("date.date");  add("date.quarter");  add("date.week");  add("date.month");  add("date.year"); }}, 
            new HashSet<Dimension>());
    
    private Set<Dimension> parentDimensions;
    private Set<String> availableFields;
    
    private Dimension(HashSet<String> fields, HashSet<Dimension> parents) {
        this.availableFields = fields;
        this.parentDimensions = parents;
    }
    
    @JsonIgnore
    public Set<String> getAvailableFields() {
        return availableFields;
    }
    
    @JsonIgnore
    public Set<Dimension> getParentDimensions() {
        return parentDimensions;
    }
}