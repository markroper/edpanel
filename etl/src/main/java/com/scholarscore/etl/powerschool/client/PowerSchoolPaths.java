package com.scholarscore.etl.powerschool.client;

import java.time.LocalDate;

/**
 * This class is a non-static path factory.  It is not static because a number of things can be dynamically changed
 * including page size and cutoff date.
 *
 * Created by markroper on 11/2/15.
 */
public class PowerSchoolPaths {
    private static final String BASE = "/ws/v1";
    private static final String SCHEMA_BASE = "/ws/schema/table";
    private Integer pageSize = PowerSchoolClient.PAGE_SIZE;
    private String cutoffDate = "2015-08-01";

    public void setPageSize(Integer size) {
        pageSize = size;
    }
    public void setCutoffDate(LocalDate date) {
        cutoffDate = date.toString();
    }

    public String getPageSizeParam() {
        return "pagesize=" + pageSize;
    }
    public String getSchoolPath() {
        return "/ws/v1/district/school?expansions=school_boundary,school_fees_setup";
    }

    public String getStudentsPath() {
        return BASE +
            "/school/{0}/student?pagesize=" +
            pageSize +
            "&expansions=addresses,alerts,contact,contact_info,demographics,ethnicity_race,fees,initial_enrollment,lunch,phones,schedule_setup,school_enrollment";
    }

    public String getStudentPath() {
        return BASE +
            "/student/{0}?expansions=addresses,alerts,contact,contact_info,demographics,ethnicity_race,fees,initial_enrollment,lunch,phones,schedule_setup";
    }
    
    public String getStudentsFromTablePath() {
        return SCHEMA_BASE + 
                "/students?q=id=={0}&projection=DCID,ID" + "&" + "pagesize=" + pageSize;
    }
    
    public String getCalendarDayPath() {
        return SCHEMA_BASE +
            "/calendar_day?" +
            getPageSizeParam() +
            "&projection=dcid,date_value,insession,note,membershipvalue,scheduleid,schoolid,type,id,cycle_day_id" +
                // If this query has date_value supplied, we now get this error from Excel powerschool:
                // 
                // "At least one column lacks sufficient permission"
                // "resource": "Calendar_Day"
                // "field": "Date"
                // 
                // ... even though we're requesting date_value, not date, and PS documentation 
                // doesn't make any mention of a calendar_day.date field. However, this field DOES 
                // exist and can be added to our whitelisted fields, however this only changes the resulting 
                // error to: 
                // 
                // "message": "org.hibernate.exception.SQLGrammarException: could not extract ResultSet"
                // 
                // awesome.
            "&q=schoolid=={0};insession==1";
    }

    public String getAttendancePath() {
        return SCHEMA_BASE +
            "/attendance?" +
            getPageSizeParam() +
            "&projection=*&q=studentid=={0};att_date=gt="+ cutoffDate;
    }

    public String getAttendanceCodePath() {
        return SCHEMA_BASE +
            "/attendance_code?" +
            getPageSizeParam() +
            "&projection=*";
    }

    public String getStaffPath() {
        return BASE +
            "/school/{0}/staff?" +
            getPageSizeParam() +
            "&expansions=phones,addresses,emails,school_affiliations";
    }

    public String getCoursePath() {
        return BASE + "/school/{0}/course?" + getPageSizeParam();
    }

    public String getCyclePath() {
        return SCHEMA_BASE +
                "/cycle_day?" +
                "projection=Abbreviation,Day_Name,Day_Number,ID,DCID,Letter,SchoolId,Year_Id" +
                "&q=schoolId=={0}";
    }

    public String getTermPath() {
        return BASE + "/school/{0}/term?" + getPageSizeParam();
    }

    public String getSectionPath() {
        return BASE + "/school/{0}/section?" + getPageSizeParam();
    }

    public String getPowerTeacherSectionPath(Long sourceSectionId) {
        return SCHEMA_BASE +
            "/SYNC_SectionMap?" +
            "projection=*" + 
            "&q=SectionsDCID==" + sourceSectionId;
    }

    public String getPowerTeacherSectionMappingPath() {
        return SCHEMA_BASE +
                "/SYNC_SectionMap?" +
                getPageSizeParam() +
                "&projection=*";
    }

    public String getPowerTeacherTermMappingPath() {
        return SCHEMA_BASE +
                "/SYNC_TermMap?" +
                getPageSizeParam() +
                "&projection=*";
    }

    public String getPowerTeacherTermnBinMappingPath() {
        return SCHEMA_BASE +
                "/SYNC_ReportingTermMap?" +
                getPageSizeParam() +
                "&projection=*";
    }

    public String getTermBinPath() {
        return SCHEMA_BASE +
                "/termbins?" +
                getPageSizeParam() +
                "&projection=*";
    }

    public String getPowerTeacherTermPath(Long termId) {
        return SCHEMA_BASE +
            "/PSM_ReportingTerm?" +
            getPageSizeParam() +
            "&projection=*&q=id==" + termId;
    }

    public String getPeriodPath() {
        return SCHEMA_BASE +
            "/period?" +
            getPageSizeParam() +
            "&projection=*" +
            "&q=schoolId=={0}";
    }

    public String getSectionGradesSetupPath() {
        return SCHEMA_BASE +
            "/PSM_FinalGradeSetup?" +
            getPageSizeParam() +
            "&projection=*";
    }

    public String getSectionGradeFormula(long formulaId) {
        return SCHEMA_BASE +
            "/PSM_GradingFormula?" +
            getPageSizeParam() +
            "&projection=*&q=id==" + String.valueOf(formulaId);
    }

    public String getSectionGradeFormulaWeights(long formulaId) {
        return SCHEMA_BASE +
            "/PSM_GradingFormulaWeighting?" +
            getPageSizeParam() +
            "&projection=*&q=ParentGradingFormulaID==" + String.valueOf(formulaId);
    }

    public String getSectionEnrollmentPath() {
        return BASE + "/section/{0}/section_enrollment";
    }

    public String getSectionAssignmentsPath() {
        return "/ws/schema/table/PGAssignments?" +
            getPageSizeParam() +
            "&projection=Name,SectionID,AssignmentID,Description,DateDue,PointsPossible,Type,Weight,IncludeInFinalGrades,Abbreviation,PGCategoriesID,PublishScores,PublishState&q=SectionID=={0}";
    }

    public String getSectionAssignmentCategories() {
        return SCHEMA_BASE +
            "/pgcategories?q=SectionID=={0}" + 
            "&" + getPageSizeParam() +
            "&projection=Abbreviation,DCID,DefaultPtsPoss,Description,ID,Name,SectionID";

    }

    public String getPowerTeacherAssignmentCategories() {
        return SCHEMA_BASE +
            "/psm_assignmentcategory?" +
            "projection=*&" + getPageSizeParam();
    }

    public String getPowerTeacherFinalScores(Long sectionEnrollmentId) {
        return SCHEMA_BASE +
                "/psm_finalscore?" +
                "q=sectionenrollmentid==" + sectionEnrollmentId +
                "&projection=*&" + getPageSizeParam();
    }

    public String getPowerTeacherSectionEnrollment(Long powerTeacherSectionId) {
        return SCHEMA_BASE +
                "/PSM_SectionEnrollment?" +
                "q=sectionid==" + powerTeacherSectionId +
                "&projection=*&" + getPageSizeParam();
    }

    public String getPowerTeacherStudentMappings() {
        return SCHEMA_BASE +
                "/sync_studentmap?" +
                "projection=*&" + getPageSizeParam();
    }

    public String getSectionScoresPath() {
        return SCHEMA_BASE +
            "/storedgrades?" +
                getPageSizeParam() +
            "&q=sectionid=={0}&projection=dcid,grade,datestored,studentid,sectionid,termid";
    }
    public String getAssignmentScores() {
        return SCHEMA_BASE +
            "/SectionScoresAssignments?" + 
                getPageSizeParam() +
                "&q=assignment=={0}&projection=*";
    }
    public String getSectionScoreIds() {
        return SCHEMA_BASE +
            "/SectionScoresId?" + 
                getPageSizeParam() +
            "&q=sectionid=={0}&projection=*";
    }
}
