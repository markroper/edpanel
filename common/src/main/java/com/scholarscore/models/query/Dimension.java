package com.scholarscore.models.query;

import com.scholarscore.models.query.dimension.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    SCHOOL,
    TEACHER,
    STUDENT,
    ADMINISTRATOR,
    USER,
    STUDENT_ASSIGNMENT,
    STUDENT_SECTION_GRADE,
    SECTION_GRADE,
    SCHOOL_DAY,
    ATTENDANCE,
    BEHAVIOR;
    
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
            case STUDENT_ASSIGNMENT:
                return new StudentAssignmentDimension();
            case STUDENT_SECTION_GRADE:
                return new StudentSectionGradeDimension();
            case SECTION_GRADE:
                return new SectionGradeDimension();
            case SCHOOL_DAY:
                return new SchoolDayDimension();
            case ATTENDANCE:
                return new AttendanceDimension();
            case BEHAVIOR:
                return new BehaviorDimension();
            default:
                throw new QueryException("Unsupported Dimension " + d + "!");
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
        return orderedDimensions.stream().filter(selectedDims::contains).collect(Collectors.toList());
    }
    
    private static final List<Dimension> orderedDimensions = new ArrayList<Dimension>(){{
        add(Dimension.STUDENT_SECTION_GRADE);
        add(Dimension.SECTION_GRADE);
        add(Dimension.ATTENDANCE);
        add(Dimension.BEHAVIOR);
        add(Dimension.STUDENT);
        add(Dimension.TEACHER);
        add(Dimension.ADMINISTRATOR);
        add(Dimension.STUDENT_ASSIGNMENT);
        add(Dimension.ASSIGNMENT);
        add(Dimension.SECTION);
        add(Dimension.TERM);
        add(Dimension.YEAR);
        add(Dimension.COURSE);
        add(Dimension.SUBJECT_AREA);
        add(Dimension.SCHOOL_DAY);
        add(Dimension.SCHOOL);
        add(Dimension.USER);
    }};
}