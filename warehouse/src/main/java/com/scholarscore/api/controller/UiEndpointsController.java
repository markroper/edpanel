package com.scholarscore.api.controller;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.School;
import com.scholarscore.models.Section;
import com.scholarscore.models.assignment.StudentAssignment;
import com.scholarscore.models.ui.SectionGradeWithProgression;
import com.scholarscore.models.ui.StudentSectionDashboardData;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * These may be moved out of the core API and into a UI server in the future.
 * In fact they should be, but I've run out of time before the beta ;)
 *
 * Created by markroper on 11/17/15.
 */
@Controller
@RequestMapping(ApiConsts.API_V1_ENDPOINT + "/ui/students/{studentId}")
public class UiEndpointsController extends BaseController {

    @ApiOperation(
            value = "Get all the data for a single student needed for the student dashboard",
            notes = "Returns the current sections, section grades, section assignments, and grade progressions for a student",
            response = School.class)
    @RequestMapping(
            value = "/schools/{schoolId}/years/{schoolYearId}/terms/{termId}",
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity
        getSchool(@ApiParam(name = "studentId", required = true, value = "Student ID")
             @PathVariable(value="studentId") Long studentId,
             @ApiParam(name = "schoolId", required = true, value = "School ID")
             @PathVariable(value="schoolId") Long schoolId,
             @ApiParam(name = "schoolYearId", required = true, value = "School year long ID")
             @PathVariable(value="schoolYearId") Long schoolYearId,
             @ApiParam(name = "termId", required = true, value = "Term ID")
             @PathVariable(value="termId") Long termId) {
        List<StudentSectionDashboardData> response = new ArrayList<>();
        ServiceResponse<Collection<Section>> sectionsResponse =
                pm.getSectionManager().getAllSections(studentId, schoolId, schoolYearId, termId);
        if(null == sectionsResponse.getCode()) {
            for(Section s: sectionsResponse.getValue()) {
                StudentSectionDashboardData sectionDashData = new StudentSectionDashboardData();
                sectionDashData.setSection(s);
                ServiceResponse<Collection<StudentAssignment>> studAssesResp = pm.getStudentAssignmentManager().
                        getOneSectionOneStudentsAssignments(studentId, schoolId, schoolYearId, termId, s.getId());
                if(null == studAssesResp.getCode()) {
                    sectionDashData.setStudentAssignments(new ArrayList<>(studAssesResp.getValue()));
                }

                ServiceResponse<SectionGradeWithProgression> gradesByWeekResp =
                    pm.getStudentSectionGradeManager().getStudentSectionGradeByWeek(
                            schoolId, schoolYearId, termId, s.getId(), studentId);
                if(null == gradesByWeekResp.getCode()) {
                    sectionDashData.setGradeProgression(gradesByWeekResp.getValue());
                }
                response.add(sectionDashData);
            }
        }
        return respond(new ServiceResponse<List<StudentSectionDashboardData>>(response));
    }
}
