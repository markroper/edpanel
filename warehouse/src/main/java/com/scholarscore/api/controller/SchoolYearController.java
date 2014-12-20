package com.scholarscore.api.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import com.scholarscore.models.EntityId;
import com.scholarscore.models.Term;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Controller
@RequestMapping("/api/v1/schools/{schoolId}/years")
public class SchoolYearController extends BaseController {
    @ApiOperation(
            value = "Get all school years within a school", 
            notes = "Retrieve all school years", 
            response = List.class)
    @RequestMapping(
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getAllSchoolYears(
            @ApiParam(name = "schoolId", required = true, value = "The school long ID")
            @PathVariable(value="schoolId") Long schoolId) {
        return respond(new ArrayList<>(schoolYears.get(schoolId).values()));
    }
    
    @ApiOperation(
            value = "Get a school year by ID", 
            notes = "Given a school year ID, the endpoint returns the school year", 
            response = SchoolYear.class)
    @RequestMapping(
            value = "/{schoolYearId}", 
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getSchoolYear(
            @ApiParam(name = "schoolId", required = true, value = "The school long ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "schoolYearId", required = true, value = "The school year long ID")
            @PathVariable(value="schoolYearId") Long schoolYearId) {
        if(!schools.containsKey(schoolId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId });
        }
        if(!schoolYears.containsKey(schoolId) || !schoolYears.get(schoolId).containsKey(schoolYearId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { COURSE, schoolYearId });
        } else {
            return respond(schoolYears.get(schoolId).get(schoolYearId));
        }
    }

    @ApiOperation(
            value = "Create a school year within a school", 
            notes = "Creates, assigns an ID to, persists and returns a school year",
            response = EntityId.class)
    @RequestMapping(
            method = RequestMethod.POST, 
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity createSchoolYear(
            @ApiParam(name = "schoolId", required = true, value = "The school long ID")
            @PathVariable(value="schoolId") Long schoolId,
            @RequestBody @Valid SchoolYear schoolYear) {
        if(!schools.containsKey(schoolId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId });
        }
        schoolYear.setId(schoolYearCounter.incrementAndGet());
        if(null != schoolYear.getTerms() && !schoolYear.getTerms().isEmpty()) {
            for(Term t : schoolYear.getTerms()) {
                t.setId(termCounter.incrementAndGet());
            }
        }
        if(!schoolYears.containsKey(schoolId)) {
            schoolYears.put(schoolId, new HashMap<Long, SchoolYear>());
        }
        schoolYears.get(schoolId).put(schoolYear.getId(), schoolYear);
        return respond(new EntityId(schoolYear.getId()));
    }

    @ApiOperation(
            value = "Overwrite an existing school year within a district", 
            notes = "Overwrites an existing school year entity within a district with the ID provided",
            response = EntityId.class)
    @RequestMapping(
            value = "/{schoolYearId}", 
            method = RequestMethod.PUT, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity replaceSchoolYear(
            @ApiParam(name = "schoolId", required = true, value = "The school long ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "schoolYearId", required = true, value = "The school year ID")
            @PathVariable(value="schoolYearId") Long schoolYearId,
            @RequestBody @Valid SchoolYear schoolYear) {
        if(!schools.containsKey(schoolId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId });
        }
        if(null != schoolYearId && schoolYears.containsKey(schoolId) && schoolYears.get(schoolId).containsKey(schoolYearId)) {
            SchoolYear originalSchoolYear = schoolYears.get(schoolId).get(schoolYearId);
            HashSet<Long> termIds = resolveTermIds(originalSchoolYear);
            if(null != schoolYear.getTerms() && !schoolYear.getTerms().isEmpty()) {
                for(Term t : schoolYear.getTerms()) {
                    if(null == t.getId() || !termIds.contains(t.getId())) {
                        t.setId(termCounter.incrementAndGet());
                    }
                }
            }
            schoolYears.get(schoolId).put(schoolYearId, schoolYear);
            return respond(new EntityId(schoolYearId));
        } else {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ COURSE, schoolYearId });
        }
    }
    
    @ApiOperation(
            value = "Update an existing school year within a district", 
            notes = "Updates an existing school year properties. Will not overwrite existing values with null.",
            response = EntityId.class)
    @RequestMapping(
            value = "/{schoolYearId}", 
            method = RequestMethod.PATCH, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity updateSchoolYear(
            @ApiParam(name = "schoolId", required = true, value = "The school long ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "schoolYearId", required = true, value = "The school year ID")
            @PathVariable(value="schoolYearId") Long schoolYearId,
            @RequestBody @Valid SchoolYear schoolYear) {
        if(null == schoolId || !schools.containsKey(schoolId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SCHOOL, schoolId });
        }
        if(null != schoolYear && null != schoolYearId && schoolYears.containsKey(schoolId) 
                && schoolYears.get(schoolId).containsKey(schoolYearId)) {
            SchoolYear originalSchoolYear = schoolYears.get(schoolId).get(schoolYearId);
            schoolYear.mergePropertiesIfNull(originalSchoolYear);
            if(null != schoolYear.getTerms() && !schoolYear.getTerms().isEmpty()) {
                HashSet<Long> termIds = resolveTermIds(originalSchoolYear);
                for(Term t : schoolYear.getTerms()) {
                    if(null == t.getId() || !termIds.contains(t.getId())) {
                        t.setId(termCounter.getAndIncrement());
                    }
                }
            }
            schoolYears.get(schoolId).put(schoolYearId, schoolYear);
            return respond(new EntityId(schoolYearId));
        } else {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ COURSE, schoolYearId });
        }
    }

    @ApiOperation(
            value = "Delete a school year from a school", 
            response = Void.class)
    @RequestMapping(
            value = "/{schoolYearId}", 
            method = RequestMethod.DELETE, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity deleteSchoolYear(
            @ApiParam(name = "schoolId", required = true, value = "The school long ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "schoolYearId", required = true, value = "The school year ID")
            @PathVariable(value="schoolYearId") Long schoolYearId) {
        if(null == schoolId || !schools.containsKey(schoolId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SCHOOL, schoolId });
        }
        if(null == schoolYearId || !schoolYears.containsKey(schoolId) 
                || !schoolYears.get(schoolId).containsKey(schoolYearId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ COURSE, schoolYearId });
        }
        schoolYears.get(schoolId).remove(schoolYearId);
        return respond((SchoolYear) null);
    }
    
    private HashSet<Long> resolveTermIds(SchoolYear year) {
        HashSet<Long> termIds = new HashSet<>();
        if(null != year.getTerms()) {
            for(Term t : year.getTerms()) {
                termIds.add(t.getId());
            }
        }
        return termIds;
    }
}
