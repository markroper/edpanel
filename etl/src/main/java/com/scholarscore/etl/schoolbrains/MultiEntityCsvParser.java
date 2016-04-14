package com.scholarscore.etl.schoolbrains;

import java.io.File;

/**
 * Created by markroper on 4/14/16.
 */
public abstract class MultiEntityCsvParser<T> extends BaseParser<T> {
    protected static final int SchoolID = 0;
    protected static final int SectionID = 1;
    protected static final int SectionName = 2;
    protected static final int NumberOfCredits = 3;
    protected static final int SchoolIdentificationNumber = 4;
    protected static final int SchoolName = 5;
    protected static final int SchoolYearEnd = 6;
    protected static final int SchoolYearStart = 7;
    protected static final int SchoolYearNumber = 8;
    protected static final int StandardCourseDifficulty = 9;
    protected static final int StandardCourseType = 10;
    protected static final int SchoolYearID = 11;
    protected static final int CourseCode = 12;
    protected static final int CourseEAID = 13;
    protected static final int CourseName = 14;
    protected static final int Department = 15;
    protected static final int IsActive = 16;
    protected static final int IsTerm1 = 17;
    protected static final int IsTerm2 = 18;
    protected static final int IsTerm3 = 19;
    protected static final int IsTerm4 = 20;
    protected static final int IsTerm5 = 21;
    protected static final int IsTerm6 = 22;
    protected static final int UsedInGPA = 23;
    protected static final int Teacher = 24;
    protected static final int TeacherID = 25;
    protected static final int SecondTeacherID = 26;
    protected static final int SecondTeacher = 27;

    public MultiEntityCsvParser(File file) {
        super(file);
    }
}
