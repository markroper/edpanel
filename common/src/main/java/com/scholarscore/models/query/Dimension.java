package com.scholarscore.models.query;

import com.scholarscore.models.query.dimension.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    COURSE,
    SECTION,
    ASSIGNMENT,
    TERM,
    YEAR,
    SUBJECT_AREA,
    GRADE_LEVEL,
    SCHOOL,
    TEACHER,
    STUDENT,
    ADMINISTRATOR,
    USER;
    
    /**
     * Factory method for constructing an IDimension of time Dimension.
     * @param d
     * @return
     */
    public static IDimension buildDimension(Dimension d) {
        switch(d) {
            case COURSE:
                return new CourseDimension();
            case SECTION:
                return new SectionDimension();
            case TERM:
                return new TermDimension();
            case YEAR:
                return new SchoolYearDimension();
            case SUBJECT_AREA:
                return new SubjectAreaDimension();
            case GRADE_LEVEL:
                return new GradeLevelDimension();
            case SCHOOL:
                return new SchoolDimension();
            case TEACHER:
                return new TeacherDimension();
            case STUDENT:
                return new StudentDimension();
            case ADMINISTRATOR:
                return new AdministratorDimension();
            case ASSIGNMENT:
                return new AssignmentDimension();
            case USER:
                return new UserDimension();
            default:
                return null;
            
        }
    }
    
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
    
    private static final List<Dimension> orderedDimensions = new ArrayList<Dimension>(){{
        add(Dimension.STUDENT);
        add(Dimension.TEACHER);
        add(Dimension.SECTION);
        add(Dimension.ASSIGNMENT);
        add(Dimension.TERM);
        add(Dimension.YEAR);
        add(Dimension.COURSE);
        add(Dimension.SUBJECT_AREA);
        add(Dimension.GRADE_LEVEL);
        add(Dimension.SCHOOL);
        add(Dimension.ADMINISTRATOR);
        add(Dimension.USER);
    }};
}