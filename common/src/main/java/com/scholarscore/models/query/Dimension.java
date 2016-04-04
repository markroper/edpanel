package com.scholarscore.models.query;

import com.scholarscore.models.Section;
import com.scholarscore.models.attendance.Attendance;
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
    COURSE(CourseDimension.class),
    SECTION(SectionDimension.class),
    ASSIGNMENT(AssignmentDimension.class),
    TERM(TermDimension.class),
    YEAR(SchoolYearDimension.class),
    SCHOOL(SchoolDimension.class),
    TEACHER(TeacherDimension.class),
    STUDENT(StudentDimension.class),
    STAFF(StaffDimension.class),
    ADMINISTRATOR(AdministratorDimension.class),
    USER(UserDimension.class),
    STUDENT_ASSIGNMENT(StudentAssignmentDimension.class),
    STUDENT_SECTION_GRADE(StudentSectionGradeDimension.class),
    SECTION_GRADE(SectionGradeDimension.class),
    SCHOOL_DAY(SchoolDayDimension.class),
    ATTENDANCE(AttendanceDimension.class),
    BEHAVIOR(BehaviorDimension.class),
    GPA(GpaDimension.class),
    CURRENT_GPA(CurrentGpaDimension.class),
    GOAL(GoalDimension.class);
    
    public Class<? extends IDimension> dimensionClass;
    
    Dimension(Class<? extends IDimension> dimensionClass) { 
        this.dimensionClass = dimensionClass;
    }
    
    /**
     * Factory method for constructing an IDimension of time Dimension.
     * @param d
     * @return
     */
    public IDimension buildDimension() {
        try {
            return dimensionClass.newInstance();
        } catch (InstantiationException|IllegalAccessException e) {
            throw new QueryException("Cannot build dimension class " + this.getClass().getSimpleName() + "!");
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
        add(Dimension.GOAL);
        add(Dimension.STUDENT_SECTION_GRADE);
        add(Dimension.SECTION_GRADE);
        add(Dimension.ATTENDANCE);
        add(Dimension.BEHAVIOR);
        add(Dimension.CURRENT_GPA);
        add(Dimension.GPA);
        add(Dimension.STUDENT);
        add(Dimension.STAFF);
        add(Dimension.TEACHER);
        add(Dimension.ADMINISTRATOR);
        add(Dimension.STUDENT_ASSIGNMENT);
        add(Dimension.ASSIGNMENT);
        add(Dimension.SECTION);
        add(Dimension.TERM);
        add(Dimension.YEAR);
        add(Dimension.COURSE);
        add(Dimension.SCHOOL_DAY);
        add(Dimension.SCHOOL);
        add(Dimension.USER);
    }};
}