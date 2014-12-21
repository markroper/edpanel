package com.scholarscore.api.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import com.scholarscore.api.util.ErrorCode;
import com.scholarscore.api.util.ErrorResponseFactory;
import com.scholarscore.models.Assignment;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.SubjectArea;
import com.scholarscore.models.Term;

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
    protected static final String SCHOOL_YEAR = "school year";
    protected static final String TERM = "term";
    protected static final String SECTION = "section";
    
    protected final AtomicLong schoolCounter = new AtomicLong();
    protected static Map<Long, School> schools = Collections.synchronizedMap(new HashMap<Long, School>());
    protected final AtomicLong schoolYearCounter = new AtomicLong();
    protected final AtomicLong termCounter = new AtomicLong();
    protected static Map<Long, Map<Long, SchoolYear>> schoolYears = 
            Collections.synchronizedMap(new HashMap<Long, Map<Long, SchoolYear>>());
    
    //Map<termId, Map<sectionId, Section>>
    protected final AtomicLong sectionCounter = new AtomicLong();
    protected static Map<Long, Map<Long, Section>> sections = Collections.synchronizedMap(new HashMap<Long, Map<Long, Section>>());
    
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
    
    protected HashSet<Long> resolveTermIds(SchoolYear year) {
        HashSet<Long> termIds = new HashSet<>();
        if(null != year.getTerms()) {
            for(Term t : year.getTerms()) {
                termIds.add(t.getId());
            }
        }
        return termIds;
    }
    
    protected Term getTermById(Set<Term> terms, Long termId) {
        Term termWithTermId = null;
        if(null != terms) {
            for(Term t : terms) {
                if(t.getId().equals(termId)) {
                    termWithTermId = t;
                    break;
                }
            }
        }
        return termWithTermId;
    }

}
