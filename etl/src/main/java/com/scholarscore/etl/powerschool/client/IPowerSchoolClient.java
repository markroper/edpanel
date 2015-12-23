package com.scholarscore.etl.powerschool.client;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.etl.powerschool.api.model.*;
import com.scholarscore.etl.powerschool.api.model.assignment.PsAssignmentWrapper;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PsAssignmentScoreWrapper;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PsSectionScoreIdWrapper;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PtFinalScoreWrapper;
import com.scholarscore.etl.powerschool.api.model.assignment.type.PsAssignmentTypeWrapper;
import com.scholarscore.etl.powerschool.api.model.assignment.type.PtAssignmentCategoryWrapper;
import com.scholarscore.etl.powerschool.api.model.attendance.PsAttendanceCodeWrapper;
import com.scholarscore.etl.powerschool.api.model.attendance.PsAttendanceWrapper;
import com.scholarscore.etl.powerschool.api.model.attendance.PsCalendarDayWrapper;
import com.scholarscore.etl.powerschool.api.model.section.PsFinalGradeSetupWrapper;
import com.scholarscore.etl.powerschool.api.model.section.PsGradeFormulaWrapper;
import com.scholarscore.etl.powerschool.api.model.section.PsSectionGradeFormulaWeightingWrapper;
import com.scholarscore.etl.powerschool.api.model.section.PsSectionGradeWrapper;
import com.scholarscore.etl.powerschool.api.model.section.PtSectionEnrollmentWrapper;
import com.scholarscore.etl.powerschool.api.model.section.PtSectionMapWrapper;
import com.scholarscore.etl.powerschool.api.model.section.PtTermWrapper;
import com.scholarscore.etl.powerschool.api.model.student.PsStudents;
import com.scholarscore.etl.powerschool.api.model.student.PtPsStudentMapWrapper;
import com.scholarscore.etl.powerschool.api.model.term.PsTermBinWrapper;
import com.scholarscore.etl.powerschool.api.model.term.PtPsTermBinReportingTermWrapper;
import com.scholarscore.etl.powerschool.api.model.term.PtPsTermMapWrapper;
import com.scholarscore.etl.powerschool.api.response.DistrictResponse;
import com.scholarscore.etl.powerschool.api.response.PsResponse;
import com.scholarscore.etl.powerschool.api.response.SchoolsResponse;
import com.scholarscore.etl.powerschool.api.response.SectionEnrollmentsResponse;
import com.scholarscore.etl.powerschool.api.response.SectionResponse;
import com.scholarscore.etl.powerschool.api.response.StudentResponse;
import com.scholarscore.etl.powerschool.api.response.TermResponse;

import java.time.LocalDate;
import java.util.Date;

/**
 * Created by mattg on 7/2/15.
 */
public interface IPowerSchoolClient {
    void setSyncCutoff(LocalDate date);

    SchoolsResponse getSchools() throws HttpClientException;

    PsResponse<PsPeriodWrapper> getPeriods() throws HttpClientException;

    DistrictResponse getDistrict() throws HttpClientException;

    PsStaffs getStaff(Long schoolId) throws HttpClientException;

    PsStudents getStudentsBySchool(Long schoolId) throws HttpClientException;

    StudentResponse getStudentById(Long studentId) throws HttpClientException;

    PsCourses getCoursesBySchool(Long schoolId) throws HttpClientException;

    PsCycles getCyclesBySchool(Long schoolId) throws HttpClientException;

    Object getAsMap(String path) throws HttpClientException;

    TermResponse getTermsBySchoolId(Long schoolId) throws HttpClientException;

    PsResponse<PsTermBinWrapper> getTermBins() throws HttpClientException;

    SectionResponse getSectionsBySchoolId(Long schoolId) throws HttpClientException;
    
    SectionEnrollmentsResponse getEnrollmentBySectionId(Long sectionId) throws HttpClientException;

    PsResponse<PsSectionGradeWrapper> getSectionScoresBySectionId(Long sectionId) throws HttpClientException;

    PsResponse<PsAssignmentWrapper> getAssignmentsBySectionId(Long sectionId) throws HttpClientException;

    PsResponse<PsAssignmentTypeWrapper> getAssignmentCategoriesBySectionId(Long sectionId) throws HttpClientException;

    PsResponse<PtAssignmentCategoryWrapper> getPowerTeacherAssignmentCategory() throws HttpClientException;

    PsResponse<PsAssignmentScoreWrapper> getStudentScoresByAssignmentId(Long assignmentId) throws HttpClientException;

    PsResponse<PsSectionScoreIdWrapper> getStudentScoreIdsBySectionId(Long sectionId) throws HttpClientException;

    PsResponse<PsCalendarDayWrapper> getSchoolCalendarDays(Long schoolId) throws HttpClientException;

    PsResponse<PsAttendanceWrapper> getStudentAttendance(Long studentId) throws HttpClientException;

    PsResponse<PsAttendanceCodeWrapper> getAttendanceCodes() throws HttpClientException;

    PsResponse<PsFinalGradeSetupWrapper> getFinalGradeSetups() throws HttpClientException;

    PsResponse<PtSectionMapWrapper> getPowerTeacherSectionMapping(Long sourceSectionNumberVarchar) throws HttpClientException;

    PsResponse<PtSectionMapWrapper> getPowerTeacherSectionMappings() throws HttpClientException;

    PsResponse<PtPsTermMapWrapper> getPowerTeacherTermMappings() throws HttpClientException;

    PsResponse<PtPsTermBinReportingTermWrapper> getPowerTeacherTermBinMappings() throws HttpClientException;

    PsResponse<PtPsStudentMapWrapper> getPowerTeacherStudentMappings() throws HttpClientException;

    PsResponse<PtSectionEnrollmentWrapper> getPowerTeacherSectionEnrollments(Long ptSectionId) throws HttpClientException;

    PsResponse<PtFinalScoreWrapper> getPowerTeacherFinalScore(Long sectionEnrollmentId) throws HttpClientException;

    PsResponse<PtTermWrapper> getPowerTeacherTerm(Long powerTeacherTermId) throws HttpClientException;

    PsResponse<PsGradeFormulaWrapper> getGradeFormula(Long gradeFormulaId) throws HttpClientException;

    PsResponse<PsSectionGradeFormulaWeightingWrapper> getGradeFormulaWeights(Long gradeFormulaId) throws HttpClientException;
}