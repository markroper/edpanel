package com.scholarscore.api.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import com.scholarscore.api.util.ErrorCode;
import com.scholarscore.api.util.ErrorResponseFactory;
import com.scholarscore.models.Assignment;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.SubjectArea;

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
    //TODO: @mroper we need to add a real persistence layer that we call instead of manipulating this map
    public static final String JSON_ACCEPT_HEADER = "application/json";
    
    protected static final String SCHOOL = "school";
    protected static final String ASSIGNMENT = "assignment";
    protected static final String COURSE = "course";
    
    protected final AtomicLong schoolIdCounter = new AtomicLong();
    protected static Map<Long, School> schools = Collections.synchronizedMap(new HashMap<Long, School>());
    protected final AtomicLong subjectAreaCounter = new AtomicLong();
    protected static Map<Long, Map<Long, SubjectArea>> subjectAreas = Collections.synchronizedMap(new HashMap<Long, Map<Long, SubjectArea>>());
    protected final AtomicLong courseCounter = new AtomicLong();
    protected static Map<Long, Map<Long, Course>> courses = Collections.synchronizedMap(new HashMap<Long, Map<Long, Course>>());
    protected final AtomicLong assignmentCounter = new AtomicLong();
    protected static Map<Long, Map<Long, Map<Long, Assignment>>> assignments = Collections.synchronizedMap(new HashMap<Long, Map<Long, Map<Long, Assignment>>>());
    
    @SuppressWarnings("unchecked")
    protected ResponseEntity respond(Object obj) {
        if(obj instanceof ErrorCode) {
            ErrorCode err = (ErrorCode) obj;
            ErrorResponseFactory factory = new ErrorResponseFactory();
            return new ResponseEntity(factory.localizeError(err), err.getHttpStatus());
        } else {
            return new ResponseEntity(obj, HttpStatus.OK);
        }
    }
    
    protected ResponseEntity<ErrorCode> respond(ErrorCode code, Object[] args) {
        ErrorResponseFactory factory = new ErrorResponseFactory();
        ErrorCode returnError = new ErrorCode(code);
        returnError.setArguments(args);
        return new ResponseEntity<ErrorCode>(factory.localizeError(returnError), returnError.getHttpStatus());
    }

}