package com.scholarscore.models.query;

import com.scholarscore.models.Course;
import com.scholarscore.models.GradeLevel;
import com.scholarscore.models.School;
import com.scholarscore.models.Section;
import com.scholarscore.models.Student;
import com.scholarscore.models.SubjectArea;
import com.scholarscore.models.Teacher;

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
public enum Dimension {
    COURSE(new Class[]{ Course.class, SubjectArea.class, GradeLevel.class, School.class }),
    SECTION(new Class[]{ Section.class, Course.class, SubjectArea.class, GradeLevel.class, School.class }),
    SUBJECT_AREA(new Class[]{ SubjectArea.class, School.class }),
    GRADE_LEVEL(new Class[]{ GradeLevel.class, School.class }),
    SCHOOL(new Class[]{ School.class }),
    DISTRICT(new Class[]{}), //TODO: populate with SchoolDistrict class, when created
    TEACHER(new Class[]{ Teacher.class }),
    //Student dimensions
    STUDENT(new Class[]{ Student.class }),
    //TODO: figure out how we want to model the sub-attributes for a student like:
    GENDER(new Class[]{}),
    FREE_LUNCH(new Class[]{}), //true if the student's family qualifies for free or reduce priced lunch, otherwise false
    AGE(new Class[]{}),
    GRADE_REPEATER(new Class[]{}), //true if the student is presently repeating a grade, otherwise false
    ETHNICITY(new Class[]{}),
    RACE(new Class[]{}),
    ELL(new Class[]{}), //true if the student is designated an english language learner, otherwise false
    SPECIAL_ED(new Class[]{}), //true if the student has an Individual Education Plan (IEP), otherwise false
    CITY_OF_RESIDENCE(new Class[]{});
    
    private Class[] availableClasses;
    
    private Dimension(Class[] availableFields) {
        this.availableClasses = availableFields;
    }
}