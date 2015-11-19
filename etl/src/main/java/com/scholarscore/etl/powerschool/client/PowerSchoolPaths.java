package com.scholarscore.etl.powerschool.client;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class is a non-static path factory.  It is not static because a number of things can be dynamically changed
 * including page size and cutoff date.
 *
 * Created by markroper on 11/2/15.
 */
public class PowerSchoolPaths {
    private static final  String PAGE_NUM_PARAM = "page={0}";
    private static final String BASE = "/ws/v1";
    private static final String SCHEMA_BASE = "/ws/schema/table";
    private Integer pageSize = 1000;
    private String cutoffDate = "2015-08-01";

    public void setPageSize(Integer size) {
        pageSize = size;
    }
    public void setCutoffDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        cutoffDate = format.format(date);
    }

    public String getPageSizeParam() {
        return "pagesize=" + pageSize;
    }
    public String getSchoolPath() {
        return "/ws/v1/district/school?expansions=school_boundary,school_fees_setup";
    }

    public String getStudentsPath() {
        return BASE +
            "/school/{1}/student?pagesize=" +
            pageSize +
            "&" + PAGE_NUM_PARAM +
            "&expansions=addresses,alerts,contact,contact_info,demographics,ethnicity_race,fees,initial_enrollment,lunch,phones,schedule_setup,school_enrollment";
    }

    public String getStudentPath() {
        return BASE +
            "/student/{0}?expansions=addresses,alerts,contact,contact_info,demographics,ethnicity_race,fees,initial_enrollment,lunch,phones,schedule_setup";
    }

    public String getCalendarDayPath() {
        return SCHEMA_BASE +
            "/calendar_day?" +
            getPageSizeParam() +
            "&" + PAGE_NUM_PARAM +
            "&projection=dcid,date_value,insession,note,membershipvalue,scheduleid,schoolid,type,id" +
            "&q=schoolid=={1};date_value=gt=" + cutoffDate + ";insession==1";
    }

    public String getAttendancePath() {
        return SCHEMA_BASE +
            "/attendance?" +
            getPageSizeParam() +
            "&" + PAGE_NUM_PARAM +
            "&projection=*&q=studentid=={1};att_date=gt="+ cutoffDate;
    }

    public String getAttendanceCodePath() {
        return SCHEMA_BASE +
            "/attendance_code?" +
            getPageSizeParam() +
            "&" + PAGE_NUM_PARAM +
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

    public String getTermPath() {
        return BASE + "/school/{0}/term?" + getPageSizeParam();
    }

    public String getSectionPath() {
        return BASE + "/school/{0}/section?" + getPageSizeParam();
    }

    public String getPowerTeacherSectionPath(Long sourceSectionId) {
        return SCHEMA_BASE +
            "/SYNC_SectionMap?" +
            "projection=*&q=SectionsDCID==" + sourceSectionId;
    }

    public String getPowerTeacherSectionMappingPath() {
        return SCHEMA_BASE +
                "/SYNC_SectionMap?" +
                getPageSizeParam() +
                "&" + PAGE_NUM_PARAM +
                "&projection=*";
    }

    public String getPowerTeacherTermnMappingPath() {
        return SCHEMA_BASE +
                "/SYNC_TermMap?" +
                getPageSizeParam() +
                "&" + PAGE_NUM_PARAM +
                "&projection=*";
    }

    public String getPowerTeacherTermnBinMappingPath() {
        return SCHEMA_BASE +
                "/SYNC_ReportingTermMap?" +
                getPageSizeParam() +
                "&" + PAGE_NUM_PARAM +
                "&projection=*";
    }

    public String getTermBinPath() {
        return SCHEMA_BASE +
                "/termbins?" +
                getPageSizeParam() +
                "&" + PAGE_NUM_PARAM +
                "&projection=*";
    }

    public String getPowerTeacherTermPath(Long termId) {
        return SCHEMA_BASE +
            "/PSM_ReportingTerm?" +
            getPageSizeParam() +
            "&" + PAGE_NUM_PARAM +
            "&projection=*&q=id==" + termId;
    }

    public String getPeriodPath() {
        return SCHEMA_BASE +
            "/period?" +
            getPageSizeParam() +
            "&" + PAGE_NUM_PARAM +
            "&projection=*";
    }

    public String getSectionGradesSetupPath() {
        return SCHEMA_BASE +
            "/PSM_FinalGradeSetup?" +
            getPageSizeParam() +
            "&" + PAGE_NUM_PARAM +
            "&projection=*";
    }

    public String getSectionGradeFormula(long formulaId) {
        return SCHEMA_BASE +
            "/PSM_GradingFormula?" +
            getPageSizeParam() +
            "&" + PAGE_NUM_PARAM +
            "&projection=*&q=id==" + String.valueOf(formulaId);
    }

    public String getSectionGradeFormulaWeights(long formulaId) {
        return SCHEMA_BASE +
            "/PSM_GradingFormulaWeighting?" +
            getPageSizeParam() +
            "&" + PAGE_NUM_PARAM +
            "&projection=*&q=ParentGradingFormulaID==" + String.valueOf(formulaId);
    }

    public String getSectionEnrollmentPath() {
        return BASE + "/section/{0}/section_enrollment";
    }

    public String getSectionAssignmentsPath() {
        return "/ws/schema/table/PGAssignments?" +
            PAGE_NUM_PARAM +
            "&" + getPageSizeParam() +
            "&projection=Name,SectionID,AssignmentID,Description,DateDue,PointsPossible,Type,Weight,IncludeInFinalGrades,Abbreviation,PGCategoriesID,PublishScores,PublishState&q=SectionID=={1}";
    }

    public String getSectionAssignmentCategories() {
        return SCHEMA_BASE +
            "/pgcategories?q=SectionID=={1}&"+
            PAGE_NUM_PARAM +
            "&" + getPageSizeParam() +
            "&projection=Abbreviation,DCID,DefaultPtsPoss,Description,ID,Name,SectionID";
    }

    public String getPowerTeacherAssignmentCategories() {
        return SCHEMA_BASE +
            "/psm_assignmentcategory?" +
            PAGE_NUM_PARAM +
            "&projection=*&" + getPageSizeParam();
    }

    public String getPowerTeacherFinalScores(Long sectionEnrollmentId) {
        return SCHEMA_BASE +
                "/psm_finalscore?" +
                PAGE_NUM_PARAM +
                "&q=sectionenrollmentid==" + sectionEnrollmentId +
                "&projection=*&" + getPageSizeParam();
    }

    public String getPowerTeacherSectionEnrollment(Long powerTeacherSectionId) {
        return SCHEMA_BASE +
                "/PSM_SectionEnrollment?" +
                PAGE_NUM_PARAM +
                "&q=sectionid==" + powerTeacherSectionId +
                "&projection=*&" + getPageSizeParam();
    }

    public String getPowerTeacherStudentMappings() {
        return SCHEMA_BASE +
                "/sync_studentmap?" +
                PAGE_NUM_PARAM +
                "&projection=*&" + getPageSizeParam();
    }

    public String getSectionScoresPath() {
        return SCHEMA_BASE +
            "/storedgrades?" +
            PAGE_NUM_PARAM +
            "&" + getPageSizeParam() +
            "&q=sectionid=={1}&projection=dcid,grade,datestored,studentid,sectionid,termid";
    }
    public String getAssignmentScores() {
        return SCHEMA_BASE +
            "/SectionScoresAssignments?" +
            PAGE_NUM_PARAM +
            "&" + getPageSizeParam() +
            "&q=assignment=={1}&projection=*";
    }
    public String getSectionScoreIds() {
        return SCHEMA_BASE +
            "/SectionScoresId?" +
            PAGE_NUM_PARAM +
            "&" + getPageSizeParam() +
            "&q=sectionid=={1}&projection=*";
    }
}
