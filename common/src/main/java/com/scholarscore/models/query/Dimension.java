package com.scholarscore.models.query;

/**
 * Enumerates the supported dimensions of the warehouse report and querying model.
 * Think of dimensions as those attributes you might GROUP BY in an aggregate SELECT
 * statement in SQL.
 * 
 * @author markroper
 *
 */
public enum Dimension {
    DATE,
    COURSE,
    SECTION,
    SUBJECT_AREA,
    GRADE_LEVEL,
    SCHOOL,
    DISTRICT,
    TEACHER,
    //Student dimensions
    STUDENT,
    //TODO: figure out how we want to model the sub-attributes for a student like:
    GENDER,
    FREE_LUNCH, //true if the student's family qualifies for free or reduce priced lunch, otherwise false
    AGE,
    GRADE_REPEATER, //true if the student is presently repeating a grade, otherwise false
    ETHNICITY,
    RACE,
    ELL, //true if the student is designated an english language learner, otherwise false
    SPECIAL_ED, //true if the student has an Individual Education Plan (IEP), otherwise false
    CITY_OF_RESIDENCE;
}
