package com.scholarscore.api.manager;

import com.scholarscore.api.persistence.EntityPersistence;
import com.scholarscore.api.persistence.SchoolPersistence;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created by cwallace on 9/16/2015.
 */
public class SchoolManagerImpl implements SchoolManager {

    private SchoolPersistence schoolPersistence;
    
    private EntityPersistence<SchoolYear> schoolYearPersistence;

    private OrchestrationManager pm;

    private static final String SCHOOL = "school";

    public void setSchoolPersistence(SchoolPersistence schoolPersistence) {
        this.schoolPersistence = schoolPersistence;
    }

    public void setPm(OrchestrationManager pm) {
        this.pm = pm;
    }

    public void setSchoolYearPersistence(
            EntityPersistence<SchoolYear> schoolYearPersistence) {
        this.schoolYearPersistence = schoolYearPersistence;
    }

    //SCHOOLS
    @Override
    public Collection<School> getAllSchools() {
        Collection<School> schools = schoolPersistence.selectAll();
        if(null != schools) {
            for(School s : schools) {
                ServiceResponse<Collection<SchoolYear>> sr =
                        pm.getSchoolYearManager().getAllSchoolYears(s.getId());
                if(null != sr.getValue() && !sr.getValue().isEmpty()) {
                    s.setYears(new ArrayList<SchoolYear>(sr.getValue()));
                }
            }
        }
        return schools;
    }

    @Override
    public StatusCode schoolExists(Long schoolId) {
        School school = schoolPersistence.selectSchool(schoolId);
        if(null == school) {
            return StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{SCHOOL, schoolId});
        };
        return StatusCodes.getStatusCode(StatusCodeType.OK);
    }

    @Override
    public ServiceResponse<School> getSchool(long schoolId) {
        StatusCode code = schoolExists(schoolId);
        if(!code.isOK()) {
            return new ServiceResponse<School>(code);
        }
        School school = schoolPersistence.selectSchool(schoolId);
        ServiceResponse<Collection<SchoolYear>> years = pm.getSchoolYearManager().getAllSchoolYears(schoolId);
        if(null != years.getValue() && !years.getValue().isEmpty()) {
            school.setYears(new ArrayList<SchoolYear>(years.getValue()));
        }
        return new ServiceResponse<School>(school);
    }

    @Override
    public ServiceResponse<Long> createSchool(School school) {
        Long schoolId = schoolPersistence.createSchool(school);
        if(null != school.getYears()) {
            for(SchoolYear year : school.getYears()) {
                pm.getSchoolYearManager().createSchoolYear(schoolId, year);
            }
        }
        return new ServiceResponse<Long>(schoolId);
    }

    @Override
    public ServiceResponse<Long> replaceSchool(long schoolId, School school) {
        StatusCode code = schoolExists(schoolId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        //Resolve the set of previously existing terms
        Collection<SchoolYear> originalYears = schoolYearPersistence.selectAll(schoolId);
        HashSet<Long> termIds = new HashSet<>();
        if(null != originalYears) {
            for(SchoolYear t : originalYears) {
                termIds.add(t.getId());
            }
        }
        if(null != school.getYears()) {
            //Insert or update terms on the school year
            for(SchoolYear t : school.getYears()) {
                if(null == t.getId() || !termIds.contains(t.getId())) {
                    pm.getSchoolYearManager().createSchoolYear(schoolId, t);
                } else  {
                    termIds.remove(t.getId());
                    pm.getSchoolYearManager().replaceSchoolYear(schoolId, t.getId(), t);
                }
            }
        }
        //Remove remaining terms
        for(Long id : termIds) {
            pm.getSchoolYearManager().deleteSchoolYear(schoolId, id);
        }

        return new ServiceResponse<Long>(
                schoolPersistence.replaceSchool(schoolId, school));
    }

    @Override
    public ServiceResponse<Long> updateSchool(long schoolId, School partialSchool) {
        ServiceResponse<School> sr = getSchool(schoolId);
        if(null == sr.getValue()) {
            return new ServiceResponse<Long>(sr.getCode());
        }
        partialSchool.mergePropertiesIfNull(schoolPersistence.selectSchool(schoolId));
        replaceSchool(schoolId, partialSchool);
        return new ServiceResponse<Long>(schoolId);
    }

    @Override
    public ServiceResponse<Long> deleteSchool(long schoolId) {
        StatusCode code = schoolExists(schoolId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        //Only need to delete the parent row, FK cascades deletes
        schoolPersistence.delete(schoolId);
        return new ServiceResponse<Long>((Long) null);
    }
}
