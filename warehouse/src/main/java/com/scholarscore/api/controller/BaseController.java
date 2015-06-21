package com.scholarscore.api.controller;

import com.scholarscore.api.persistence.AssignmentManager;
import com.scholarscore.api.persistence.CourseManager;
import com.scholarscore.api.persistence.ReportManager;
import com.scholarscore.api.persistence.SchoolManager;
import com.scholarscore.api.persistence.SchoolYearManager;
import com.scholarscore.api.persistence.SectionManager;
import com.scholarscore.api.persistence.StudentAssignmentManager;
import com.scholarscore.api.persistence.StudentManager;
import com.scholarscore.api.persistence.StudentSectionGradeManager;
import com.scholarscore.api.persistence.TeacherManager;
import com.scholarscore.api.persistence.TermManager;
import com.scholarscore.api.persistence.UserManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import com.scholarscore.api.persistence.PersistenceManager;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodeResponseFactory;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCodeToHttpCode;
import com.scholarscore.models.EntityId;

/**
 * All SpringMVC controllers defined in the package subclass this base
 * controller class, which contains utility methods used to generate error
 * and non-error API responses.
 * 
 * @author markroper
 *
 */
@Validated
public abstract class BaseController {
    public static final String JSON_ACCEPT_HEADER = "application/json";

    @Autowired
    private PersistenceManager PM;
   
    public void setPM(PersistenceManager persistenceManager) {
        this.PM = persistenceManager;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected ResponseEntity respond(Object obj) {
        if(obj instanceof StatusCode) {
            //If the object passed in is an error code, localize the error message to build the response
            StatusCode err = (StatusCode) obj;
            return respond(err);
        } else if(obj instanceof ServiceResponse){
            //If the object is a ServiceResponse, resolve whether to return the ErrorCode or the value instance member
            ServiceResponse sr = (ServiceResponse) obj;
            if(null != sr.getValue()) {
                if(sr.getValue() instanceof Long) {
                    //For a long, return it as an EntityId so that serialization is of the form { id: <longval> }
                    return new ResponseEntity(new EntityId((Long)sr.getValue()), HttpStatus.OK);
                } else {
                    //For all other cases, just return the value
                    return new ResponseEntity(sr.getValue(), HttpStatus.OK);
                }
            } else if(null != sr.getCode()){
                //Handle the error code on the service response
                return respond(sr.getCode());
            } else {
                //If both value and error code are null on the service response, we're dealing with a successful body-less response
                return new ResponseEntity((Object) null, HttpStatus.OK);
            }
        } 
        //If the object is neither a ServiceResponse nor an ErrorCode, respond with it directly
        return new ResponseEntity(obj, HttpStatus.OK);
    }
    
    protected ResponseEntity<StatusCode> respond(StatusCode code) {
        Object[] args = code.getArguments();
        StatusCodeResponseFactory factory = new StatusCodeResponseFactory();
        StatusCode returnError = new StatusCode(code);
        returnError.setArguments(args);
        return new ResponseEntity<StatusCode>(
                factory.localizeError(returnError), 
                StatusCodeToHttpCode.resolveHttpStatus(returnError.getCode()));
    }
    
    // TODO: incremental refactoring towards a decomposed PersistenceManager
    protected StudentManager getStudentManager() { return PM; }
    protected CourseManager getCourseManager() { return PM; }
    protected SchoolManager getSchoolManager() { return PM; }
    protected SchoolYearManager getSchoolYearManager() { return PM; }
    protected AssignmentManager getAssignmentManager() { return PM; }
    protected TermManager getTermManager() { return PM; }
    protected TeacherManager getTeacherManager() { return PM; }
    protected SectionManager getSectionManager() { return PM; }
    protected StudentAssignmentManager getStudentAssignmentManager() { return PM; }
    protected StudentSectionGradeManager getStudentSectionGradeManager() { return PM; }
    protected UserManager getUserManager() { return PM; }
    protected ReportManager getReportManager() { return PM; }
}
