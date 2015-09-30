package com.scholarscore.api.manager;

import com.scholarscore.api.persistence.EntityPersistence;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Term;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created by cwallace on 9/16/2015.
 */
public class SchoolYearManagerImpl implements SchoolYearManager {
    private static final String SCHOOL_YEAR = "school year";
    
    private EntityPersistence<SchoolYear> schoolYearPersistence;

    private OrchestrationManager pm;

    public void setSchoolYearPersistence(EntityPersistence<SchoolYear> schoolYearPersistence) {
        this.schoolYearPersistence = schoolYearPersistence;
    }

    public void setPm(OrchestrationManager pm) {
        this.pm = pm;
    }

    //SCHOOL YEARS
    @Override
    public ServiceResponse<Collection<SchoolYear>> getAllSchoolYears(long schoolId) {
        StatusCode code = pm.getSchoolManager().schoolExists(schoolId);
        if(!code.isOK()) {
            return new ServiceResponse<Collection<SchoolYear>>(code);
        }
        Collection<SchoolYear> schoolYears = schoolYearPersistence.selectAll(schoolId);
        for(SchoolYear year : schoolYears) {
            ServiceResponse<Collection<Term>> sr = pm.getTermManager().getAllTerms(schoolId, year.getId());
            if(null != sr.getValue() && !sr.getValue().isEmpty()) {
                year.setTerms(new ArrayList<Term>(sr.getValue()));
            }
        }
        return new ServiceResponse<Collection<SchoolYear>>(schoolYears);
    }

    @Override
    public StatusCode schoolYearExists(long schoolId, long schoolYearId) {
        StatusCode code = pm.getSchoolManager().schoolExists(schoolId);
        if(!code.isOK()) {
            return code;
        }
        SchoolYear schoolYear =  schoolYearPersistence.select(schoolId, schoolYearId);
        if(null == schoolYear) {
            return StatusCodes.getStatusCode(
                    StatusCodeType.MODEL_NOT_FOUND,
                    new Object[]{ SCHOOL_YEAR, schoolYearId });
        }
        return StatusCodes.getStatusCode(StatusCodeType.OK);
    }

    @Override
    public ServiceResponse<SchoolYear> getSchoolYear(long schoolId, long schoolYearId) {
        StatusCode code = schoolYearExists(schoolId, schoolYearId);
        if(!code.isOK()) {
            return new ServiceResponse<SchoolYear>(code);
        }
        SchoolYear year = schoolYearPersistence.select(schoolId, schoolYearId);
        ServiceResponse<Collection<Term>> terms = pm.getTermManager().getAllTerms(schoolId, schoolYearId);
        if(null != terms.getValue() && !terms.getValue().isEmpty()) {
            year.setTerms(new ArrayList<Term>(terms.getValue()));
        }
        return new ServiceResponse<SchoolYear>(year);
    }

    @Override
    public ServiceResponse<Long> createSchoolYear(long schoolId, SchoolYear schoolYear) {
        StatusCode code = pm.getSchoolManager().schoolExists(schoolId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        Long schoolYearId = schoolYearPersistence.insert(schoolId, schoolYear);
        if(null != schoolYear.getTerms()) {
            for(Term t: schoolYear.getTerms()) {
                pm.getTermManager().createTerm(schoolId, schoolYearId, t);
            }
        }
        return new ServiceResponse<Long>(schoolYearId);
    }

    @Override
    public ServiceResponse<Long> replaceSchoolYear(long schoolId, long schoolYearId, SchoolYear schoolYear) {
        StatusCode code = schoolYearExists(schoolId, schoolYearId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }

        //Resolve the set of previously existing terms
        Collection<Term> originalTerms = pm.getTermManager().getAllTerms(schoolId, schoolYearId).getValue();
        HashSet<Long> termIds = new HashSet<>();
        if(null != originalTerms) {
            for(Term t : originalTerms) {
                termIds.add(t.getId());
            }
        }
        if(null != schoolYear.getTerms()) {
            //Insert or update terms on the school year
            for(Term t : schoolYear.getTerms()) {
                if(null == t.getId() || !termIds.contains(t.getId())) {
                    pm.getTermManager().createTerm(schoolId, schoolYearId, t);
                } else  {
                    termIds.remove(t.getId());
                    pm.getTermManager().replaceTerm(schoolId, schoolYearId, t.getId(), t);
                }
            }
        }
        //Remove remaining terms
        for(Long id : termIds) {
            pm.getTermManager().deleteTerm(schoolId, schoolYearId, id);
        }
        schoolYearPersistence.update(schoolId, schoolYearId, schoolYear);
        return new ServiceResponse<Long>(schoolYearId);
    }

    @Override
    public ServiceResponse<Long> updateSchoolYear(long schoolId, long schoolYearId, SchoolYear schoolYear) {
        StatusCode code = schoolYearExists(schoolId, schoolYearId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        schoolYear.setId(schoolYearId);
        SchoolYear originalYear =
                schoolYearPersistence.select(schoolId, schoolYearId);
        originalYear.setTerms(new ArrayList<Term>(
                pm.getTermManager().getAllTerms(schoolId, schoolYearId).getValue()));
        schoolYear.mergePropertiesIfNull(originalYear);
        return replaceSchoolYear(schoolId, schoolYearId, schoolYear);
    }

    @Override
    public ServiceResponse<Long> deleteSchoolYear(long schoolId, long schoolYearId) {
        StatusCode code = schoolYearExists(schoolId, schoolYearId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        //Only need to delete the parent record, our deletes cascade
        schoolYearPersistence.delete(schoolYearId);
        return new ServiceResponse<Long>((Long) null);
    }
}
