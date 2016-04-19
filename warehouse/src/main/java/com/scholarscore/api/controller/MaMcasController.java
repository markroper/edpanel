package com.scholarscore.api.controller;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.api.annotation.StudentAccessible;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.state.ma.McasResult;
import com.scholarscore.models.user.Student;
import com.scholarscore.util.McasParser;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by markroper on 4/10/16.
 */
@Controller
@RequestMapping(ApiConsts.API_V1_ENDPOINT)
public class MaMcasController extends BaseController {
    private final static Logger LOGGER = LoggerFactory.getLogger(MaMcasController.class);

    @ApiOperation(
            value = "Upload MCAS results file and save the results in EdPanel",
            response = Void.class)
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/schools/{schoolId}/mcas/files")
    public @ResponseBody
    ResponseEntity handleFileUpload(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @RequestParam("file") MultipartFile file) {
        String fileName = "mcas" + RandomUtils.nextLong();
        File temp = null;
        ServiceResponse<Collection<Student>> students = this.pm.getStudentManager().getStudents(schoolId, null);
        Map<String, Student> sasidToStudent = new HashMap<>();
        if(null != students.getValue()) {
            for(Student s: students.getValue()) {
                sasidToStudent.put(s.getStateStudentId(), s);
            }
        } else {
            return respond(new ServiceResponse<Void>(students.getCode()));
        }
        if (!file.isEmpty()) {
            try {
                temp = File.createTempFile(fileName, ".csv");
                file.transferTo(temp);
                McasParser parser = new McasParser(temp);
                List<McasResult> results = parser.parse();
                Set<Long> seenStudents = new HashSet<>();
                if(null != results) {
                    for (McasResult r : results) {
                        r.setSchoolId(schoolId);
                        Student stud = sasidToStudent.get(r.getStudent().getStateStudentId());
                        if(null == stud) {
                            ServiceResponse<Student> studResp =
                                    pm.getStudentManager().getStudentByStateId(r.getStudent().getStateStudentId());
                            if(null != studResp.getValue()) {
                                stud = studResp.getValue();
                            }
                        }
                        if(null != stud) {
                            if(seenStudents.contains(stud.getId())) {
                                LOGGER.warn("Duplicate student MCAS score with with student: " + stud.getId());
                            }
                            r.setStudent(stud);
                        } else {
                            LOGGER.warn("Unable to associate an MCAS result with a student in EdPanel for SASID: " +
                                    r.getStudent().getStateStudentId());
                            r.setStudent(null);
                        }
                    }
                    return respond(this.pm.getMcasManager().createMcasResults(results));
                }
            } catch (Exception e) {
                return respond(new ServiceResponse<Void>(
                        StatusCodes.getStatusCode(StatusCodeType.UNKNOWN_INTERNAL_SERVER_ERROR)));
            } finally {
                if(null != temp) {
                    temp.delete();
                }
            }
        }
        return respond(new ServiceResponse<Void>(
                StatusCodes.getStatusCode(StatusCodeType.UNKNOWN_INTERNAL_SERVER_ERROR)));
    }

    @ApiOperation(
            value = "Get all MCAS results for a student",
            response = List.class)
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/schools/{schoolId}/students/{studentId}/mcas",
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    @StudentAccessible(paramName = "studentId")
    public @ResponseBody
    ResponseEntity getAll(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId) {
        return respond(pm.getMcasManager().getAllMcasResultsForStudent(schoolId, studentId));
    }

    @ApiOperation(
            value = "Get an MCAS result ID",
            response = McasResult.class)
    @RequestMapping(
            value = "/schools/{schoolId}/students/{studentId}/mcas/{mcasId}",
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    @StudentAccessible(paramName = "studentId")
    public @ResponseBody ResponseEntity get(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @ApiParam(name = "mcasId", required = true, value = "MCAS ID")
            @PathVariable(value="mcasId") Long mcasId) {
        return respond(pm.getMcasManager().getMcasResult(schoolId, studentId, mcasId));
    }

    @ApiOperation(
            value = "Create an MCAS result for a student",
            notes = "Creates, assigns an ID to, persists and returns a behavior event",
            response = EntityId.class)
    @RequestMapping(
            value = "/schools/{schoolId}/students/{studentId}/mcas",
            method = RequestMethod.POST,
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity create(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @RequestBody @Valid McasResult result) {
        return respond(pm.getMcasManager().createMcasResult(schoolId, studentId, result));
    }

    @ApiOperation(
            value = "Create multiple MCAS results",
            response = List.class)
    @RequestMapping(
            value = "/mcas",
            method = RequestMethod.POST,
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity bulkCreate(
            @RequestBody @Valid List<McasResult> results) {
        return respond(pm.getMcasManager().createMcasResults(results));
    }

    @ApiOperation(
            value = "Overwrite an existing MCAS result",
            response = EntityId.class)
    @RequestMapping(
            value = "/schools/{schoolId}/students/{studentId}/mcas/{mcasId}",
            method = RequestMethod.PUT,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity replaceBehavior(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @ApiParam(name = "mcasId", required = true, value = "MCAS ID")
            @PathVariable(value="mcasId") Long mcasId,
            @RequestBody @Valid McasResult result) {
        return respond(pm.getMcasManager().replaceMcasResult(schoolId, studentId, mcasId, result));
    }

    @ApiOperation(
            value = "Delete an MCAS result",
            response = Void.class)
    @RequestMapping(
            value = "/schools/{schoolId}/students/{studentId}/mcas/{mcasId}",
            method = RequestMethod.DELETE,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity deleteBehavior(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @ApiParam(name = "mcasId", required = true, value = "MCAS ID")
            @PathVariable(value="mcasId") Long mcasId) {
        return respond(pm.getMcasManager().deleteMcasResult(schoolId, studentId, mcasId));
    }
}
