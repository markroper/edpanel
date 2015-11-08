package com.scholarscore.etl.powerschool.client;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.etl.powerschool.api.model.PsCourses;
import com.scholarscore.etl.powerschool.api.model.PsStaffs;
import com.scholarscore.etl.powerschool.api.model.PsStudents;
import com.scholarscore.etl.powerschool.api.model.assignment.PsAssignmentWrapper;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PsAssignmentScoreWrapper;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PsSectionScoreIdWrapper;
import com.scholarscore.etl.powerschool.api.model.assignment.type.PsAssignmentTypeWrapper;
import com.scholarscore.etl.powerschool.api.model.assignment.type.PtAssignmentCategoryWrapper;
import com.scholarscore.etl.powerschool.api.model.attendance.PsAttendanceCodeWrapper;
import com.scholarscore.etl.powerschool.api.model.attendance.PsAttendanceWrapper;
import com.scholarscore.etl.powerschool.api.model.attendance.PsCalendarDayWrapper;
import com.scholarscore.etl.powerschool.api.model.section.PsFinalGradeSetupWrapper;
import com.scholarscore.etl.powerschool.api.model.section.PsGradeFormulaWrapper;
import com.scholarscore.etl.powerschool.api.model.section.PsSectionGradeFormulaWeightingWrapper;
import com.scholarscore.etl.powerschool.api.model.section.PsSectionGradeWrapper;
import com.scholarscore.etl.powerschool.api.model.section.PtSectionWrapper;
import com.scholarscore.etl.powerschool.api.response.DistrictResponse;
import com.scholarscore.etl.powerschool.api.response.PsResponse;
import com.scholarscore.etl.powerschool.api.response.SchoolsResponse;
import com.scholarscore.etl.powerschool.api.response.SectionEnrollmentsResponse;
import com.scholarscore.etl.powerschool.api.response.SectionResponse;
import com.scholarscore.etl.powerschool.api.response.StudentResponse;
import com.scholarscore.etl.powerschool.api.response.TermResponse;

import java.util.Date;

/**
 * Created by mattg on 7/2/15.
 */
public interface IPowerSchoolClient {
    void setSyncCutoff(Date date);

    SchoolsResponse getSchools() throws HttpClientException;

    DistrictResponse getDistrict() throws HttpClientException;

    PsStaffs getStaff(Long schoolId) throws HttpClientException;

    PsStudents getStudentsBySchool(Long schoolId) throws HttpClientException;

    StudentResponse getStudentById(Long studentId) throws HttpClientException;

    PsCourses getCoursesBySchool(Long schoolId) throws HttpClientException;

    Object getAsMap(String path) throws HttpClientException;

    TermResponse getTermsBySchoolId(Long schoolId) throws HttpClientException;

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

    PsResponse<PtSectionWrapper> getPowerTeacherSection(String sourceSectionNumberVarchar) throws HttpClientException;

    PsResponse<PsGradeFormulaWrapper> getGradeFormula(Long gradeFormulaId) throws HttpClientException;

    PsResponse<PsSectionGradeFormulaWeightingWrapper> getGradeFormulaWeights(Long gradeFormulaId) throws HttpClientException;
}