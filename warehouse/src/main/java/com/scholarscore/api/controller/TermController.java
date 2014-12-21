package com.scholarscore.api.controller;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.scholarscore.api.util.ErrorCodes;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Term;
import com.scholarscore.models.EntityId;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Controller
@RequestMapping("/api/v1/schools/{schoolId}/years/{schoolYearId}/terms")
public class TermController extends BaseController {
    @ApiOperation(
            value = "Get all terms", 
            notes = "Retrieve all terms", 
            response = List.class)
    @RequestMapping(
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getAllTerms(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "schoolYearId", required = true, value = "School year ID")
            @PathVariable(value="schoolYearId") Long schoolYearId) {
        if(null == schoolId || !schools.containsKey(schoolId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId });
        }
        if(null == schoolYearId || !schoolYears.containsKey(schoolId) || !schoolYears.get(schoolId).containsKey(schoolYearId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL_YEAR, schoolYearId });
        }
        ArrayList<Term> returnTerms = new ArrayList<Term>();
        if(schoolYears.containsKey(schoolId) && schoolYears.get(schoolId).containsKey(schoolYearId)) {
            returnTerms = new ArrayList<>(schoolYears.get(schoolId).get(schoolYearId).getTerms());
        }
        return respond(returnTerms);
    }
    
    @ApiOperation(
            value = "Get a term", 
            notes = "Given a term ID, the endpoint returns the term", 
            response = Term.class)
    @RequestMapping(
            value = "/{termId}", 
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getTerm(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "schoolYearId", required = true, value = "School year ID")
            @PathVariable(value="schoolYearId") Long schoolYearId,
            @ApiParam(name = "termId", required = true, value = "Term ID")
            @PathVariable(value="termId") Long termId) {
        if(null == schoolId || !schools.containsKey(schoolId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId });
        }
        if(null == schoolYearId || !schoolYears.containsKey(schoolId) || !schoolYears.get(schoolId).containsKey(schoolYearId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL_YEAR, schoolYearId });
        }
        
        if(schoolYears.containsKey(schoolId) && schoolYears.get(schoolId).containsKey(schoolYearId)) {
            SchoolYear year = schoolYears.get(schoolId).get(schoolYearId);
            if(null != year.getTerms()) {
                for(Term t : year.getTerms()) {
                    if(t.getId() == termId) {
                        return respond(t);
                    }
                }
            }
        }
        return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { TERM, termId });
    }

    @ApiOperation(
            value = "Create a term", 
            notes = "Creates, assigns an ID, persists and returns an term",
            response = EntityId.class)
    @RequestMapping(
            method = RequestMethod.POST, 
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity createTerm(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "schoolYearId", required = true, value = "School year ID")
            @PathVariable(value="schoolYearId") Long schoolYearId,
            @RequestBody @Valid Term term) {
        if(null == schoolId || !schools.containsKey(schoolId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId });
        }
        if(null == schoolYearId || !schoolYears.containsKey(schoolId) || !schoolYears.get(schoolId).containsKey(schoolYearId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL_YEAR, schoolYearId });
        }
        
        term.setId(termCounter.getAndIncrement());
        SchoolYear originalYear = schoolYears.get(schoolId).get(schoolYearId);
        if(null == originalYear.getTerms()) {
            originalYear.setTerms(new LinkedHashSet<Term>());
        }
        originalYear.getTerms().add(term);
        return respond(new EntityId(term.getId()));
    }

    @ApiOperation(
            value = "Overwrite an existing term", 
            notes = "Overwrites an existing term with the ID provided",
            response = EntityId.class)
    @RequestMapping(
            value = "/{termId}", 
            method = RequestMethod.PUT, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity replaceTerm(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "schoolYearId", required = true, value = "School year ID")
            @PathVariable(value="schoolYearId") Long schoolYearId,
            @ApiParam(name = "termId", required = true, value = "Term ID")
            @PathVariable(value="termId") Long termId,
            @RequestBody @Valid Term term) {
        if(null == schoolId || !schools.containsKey(schoolId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId });
        }
        if(null == schoolYearId || !schoolYears.containsKey(schoolId) || !schoolYears.get(schoolId).containsKey(schoolYearId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL_YEAR, schoolYearId });
        }
 
        if(null != termId && schoolYears.containsKey(schoolId) && schoolYears.get(schoolId).containsKey(schoolYearId)) {
            SchoolYear originalYear = schoolYears.get(schoolId).get(schoolYearId);
            Term termToReplace = null;
            if(null != originalYear.getTerms()) {
                for(Term t : originalYear.getTerms()) {
                    if(t.getId() == termId) {
                        termToReplace = t;
                        break;
                    }
                }
            }
            if(null != termToReplace) {
                originalYear.getTerms().remove(termToReplace);
                term.setId(termId);
                originalYear.getTerms().add(term);
                return respond(new EntityId(termId));
            }
        }
        return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ TERM, termId });
    }
    
    @ApiOperation(
            value = "Update an existing term", 
            notes = "Updates an existing term. Will not overwrite existing values with null.",
            response = EntityId.class)
    @RequestMapping(
            value = "/{termId}", 
            method = RequestMethod.PATCH, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity updateTerm(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "schoolYearId", required = true, value = "School year ID")
            @PathVariable(value="schoolYearId") Long schoolYearId,
            @ApiParam(name = "termId", required = true, value = "Term ID")
            @PathVariable(value="termId") Long termId,
            @RequestBody @Valid Term term) {
        if(null == schoolId || !schools.containsKey(schoolId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId });
        }
        if(null == schoolYearId || !schoolYears.containsKey(schoolId) || !schoolYears.get(schoolId).containsKey(schoolYearId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL_YEAR, schoolYearId });
        }
        
        if(null != termId && schoolYears.containsKey(schoolId) && schoolYears.get(schoolId).containsKey(schoolYearId)) {
            SchoolYear originalYear = schoolYears.get(schoolId).get(schoolYearId);
            Term termToReplace = null;
            if(null != originalYear.getTerms()) {
                for(Term t : originalYear.getTerms()) {
                    if(t.getId() == termId) {
                        term.mergePropertiesIfNull(t);
                        termToReplace = t;
                    }
                }
            }
            if(null != termToReplace) {
                originalYear.getTerms().remove(termToReplace);
                originalYear.getTerms().add(term);
                return respond(new EntityId(termId));   
            }
        }
        return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ ASSIGNMENT, termId });
    }

    @ApiOperation(
            value = "Delete an term", 
            notes = "Deletes the term with the ID provided",
            response = Void.class)
    @RequestMapping(
            value = "/{termId}", 
            method = RequestMethod.DELETE, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity deleteTerm(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "schoolYearId", required = true, value = "School year ID")
            @PathVariable(value="schoolYearId") Long schoolYearId,
            @ApiParam(name = "termId", required = true, value = "Term ID")
            @PathVariable(value="termId") Long termId) {
        if(null == schoolId || !schools.containsKey(schoolId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId });
        }
        if(null == schoolYearId || !schoolYears.containsKey(schoolId) || !schoolYears.get(schoolId).containsKey(schoolYearId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL_YEAR, schoolYearId });
        }
        if(null != termId && schoolYears.containsKey(schoolId) && schoolYears.get(schoolId).containsKey(schoolYearId)) {
            SchoolYear originalYear =  schoolYears.get(schoolId).get(schoolYearId);
            Term termToRemove = null;
            if(null != originalYear.getTerms()) {
                for(Term t : originalYear.getTerms()){
                    if(t.getId() == termId) {
                        termToRemove = t;
                        break;
                    }
                }
            }
            if(null != termToRemove) {
                originalYear.getTerms().remove(termToRemove);
                return respond((Term) null);
            }
        }
        return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { ASSIGNMENT, termId });
    }
}
