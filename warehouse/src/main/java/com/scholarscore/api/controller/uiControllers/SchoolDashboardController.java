package com.scholarscore.api.controller.uiControllers;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.api.controller.BaseController;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.School;
import com.scholarscore.models.Section;
import com.scholarscore.models.StudentSectionGrade;
import com.scholarscore.models.assignment.StudentAssignment;
import com.scholarscore.models.goal.CumulativeGradeGoal;
import com.scholarscore.models.goal.Goal;
import com.scholarscore.models.goal.GoalType;
import com.scholarscore.models.ui.SectionGradeWithProgression;
import com.scholarscore.models.ui.StudentSectionDashboardData;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.Teacher;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * Created by cwallace on 12/2/15.
 */
@Controller
@RequestMapping(ApiConsts.API_V1_ENDPOINT + "/ui/school/{schoolId}")
public class SchoolDashboardController extends BaseController {

    @ApiOperation(
            value = "Get all the data for a single student needed for the student dashboard",
            notes = "Returns the current sections, section grades, section assignments, and grade progressions for a student",
            response = School.class)
    @RequestMapping(
            value = "/years/{schoolYearId}/terms/{termId}/classes",
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody
    ResponseEntity
    getFailingClasses(
              @ApiParam(name = "schoolId", required = true, value = "School ID")
              @PathVariable(value="schoolId") Long schoolId,
              @ApiParam(name = "schoolYearId", required = true, value = "School year long ID")
              @PathVariable(value="schoolYearId") Long schoolYearId,
              @ApiParam(name = "termId", required = true, value = "Term ID")
              @PathVariable(value="termId") Long termId) {

        Collection<StudentSectionGrade> studentSectionGrades = pm.getStudentSectionGradeManager().getAllStudentSectionGradesByTerm(schoolId, schoolYearId, termId).getValue();
        Map<Student, Integer> studentsFailing = new HashMap<>();
        HashSet<Student> totalStudents = new HashSet<Student>();
        Integer maxFailedClasses = 0;

        for (StudentSectionGrade grade : studentSectionGrades) {
            totalStudents.add(grade.getStudent());
            if (grade.getGrade() < 70) {
                Integer numberOfFailedSections = studentsFailing.get(grade.getStudent());
                Student student = grade.getStudent();
                if (null != numberOfFailedSections) {
                    studentsFailing.put(student, numberOfFailedSections+1);
                    if (maxFailedClasses < numberOfFailedSections + 1) {
                        maxFailedClasses = numberOfFailedSections + 1;
                    }
                } else {
                    studentsFailing.put(student, 1);
                    if (maxFailedClasses < 1) {
                        maxFailedClasses = 1;
                    }
                }
            }
        }
        ArrayList<Object> studentDatapoints = new ArrayList<>();
        ArrayList<Object> xAxisArray = new ArrayList<Object>();
        xAxisArray.add("Number of Students Failing");
        xAxisArray.add(0);
        studentDatapoints.add("All Students");
        studentDatapoints.add(totalStudents.size() - studentsFailing.size());

        //Prepopulate object Arraylist to correct size
        for (int i = 0; i < maxFailedClasses; i++) {
            studentDatapoints.add(0);
            xAxisArray.add(i + 1);
        }

        Iterator<Map.Entry<Student, Integer>> it = studentsFailing.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Student, Integer> entry = it.next();
            Integer failingIndex = entry.getValue()+1;
            Integer presentNumberFailing = (Integer)studentDatapoints.get(failingIndex);
            studentDatapoints.set(failingIndex,presentNumberFailing+1);

        }

        ArrayList<ArrayList<Object>> chartArrays = new ArrayList<>();
        chartArrays.add(studentDatapoints);
        chartArrays.add(xAxisArray);
        return respond(chartArrays);
    }
}
