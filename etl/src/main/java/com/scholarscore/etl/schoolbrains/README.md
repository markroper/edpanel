#SchoolBrains CSV extract file columns
Below are the Haverhill SchoolBrains report names and the associated CSV file column lists.  Sample files in `etl/src/test/resources`.

## EdPanelStudents
    SASID, StudentID, GradeID, CurrentGradeID, CurrentSchool, DateOfBirth, Email, EthnicCategory, EthnicCode, EthnicID, FirstName, MiddleName, LastName, Gender, HomeAddressLine1, HomeAddressLine2, HomeCity, HomeStateID, HomePostalCode, LASID, SchoolStateID, YearOfGraduation, SchoolID, SpecialEducationEvaluationResults, SpecialEducationLevelOfNeed, EnglishLanguageLearnersProgramStatus, Gpa
## EdPanelAttendance
    SASID, AttendanceCode, AttendanceCode2, AttendanceCodeCategory, AttendanceComment, AttendanceDate, AttendanceGradeEnrollmentId, AttendanceSchoolId, AttendanceGradeId, YTDAbsence, YTDTardy, YTDDismissal, AttendanceDate
## EdPanelBehavior
    StudentID, SchoolYearID, SchoolID, GradeID, GradeEnrollmentID, ReferralID, Referral_Time, Referral_ReportedBys, Referral_Locations, Referral_Date, Referral_Comment, Referral_Codes, Referral_Actions
## EdPanelEnrollment
    StudentID, GradeId, SchoolID, SchoolYearID, SASID, LASID, IsActive, DistrictEntryDate, CurrentSchool, SchoolYear
## EdPanelGradeCategories
    SectionID, Category, IsExcludedFromTermGrade, LowScoreDrop, Term1Weight, Term2Weight, Term3Weight, Term4Weight, Term5Weight, Term6Weight, PRTerm1Weight, PRTerm2Weight, PRTerm3Weight, PRTerm4Weight, PRLowScoreDrop, PRTerm5Weight, SummerSchoolWeight, PreTermWeight
## EdPanelSchools
    SchoolID, SchoolName, DistrictID, DistrictName, DistrictState, GraduationRequirementCredits, PrincipalID
## EdPanelSectionAssignments

## EdPanelSectionGrades
    StudentID, SectionID, _ID, Term, Letter, Percent, _Current
## EdPanelSectionRoster
    LASID, SASID, StudentID, StartDate, EndDate, CourseEnrollmentStatus, GradeID, SectionID
## EdPanelSections
    SchoolID, SectionID, SectionName, NumberOfCredits, SchoolIdentificationNumber, SchoolName, SchoolYearEnd, SchoolYearStart, SchoolYearNumber, StandardCourseDifficulty, StandardCourseType, SchoolYearID, CourseCode, CourseEAID, CourseName, Department, IsActive, IsTerm1, IsTerm2, IsTerm3, IsTerm4, IsTerm5, IsTerm6, UsedInGPA, Teacher, TeacherID, SecondTeacherID, SecondTeacher, FullYearCourse
## EdPanelStudentAssignments
    StudentID, SectionID, GradebookWorkID, Number, Work, Name, Category, DateDue, Possible, Symbol, Score, Percent, Letter, Dropped, Missing, _ID, _Current
## EdPanelStudentCategoryGrades
    StudentID, SectionID, Term, Category, Earned, Possible, Percentage, Weight, _ID, _Current