package com.scholarscore.api.manager;

import com.scholarscore.api.persistence.EntityPersistence;
import com.scholarscore.api.persistence.SchoolPersistence;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.user.Staff;
import com.scholarscore.models.user.Student;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

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
                    s.setYears(new ArrayList<>(sr.getValue()));
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
            return new ServiceResponse<>(code);
        }
        School school = schoolPersistence.selectSchool(schoolId);
        ServiceResponse<Collection<SchoolYear>> years = pm.getSchoolYearManager().getAllSchoolYears(schoolId);
        if(null != years.getValue() && !years.getValue().isEmpty()) {
            school.setYears(new ArrayList<>(years.getValue()));
        }
        return new ServiceResponse<>(school);
    }

    @Override
    public ServiceResponse<Long> createSchool(School school) {
        Long schoolId = schoolPersistence.createSchool(school);
        if(null != school.getYears()) {
            for(SchoolYear year : school.getYears()) {
                pm.getSchoolYearManager().createSchoolYear(schoolId, year);
            }
        }
        return new ServiceResponse<>(schoolId);
    }

    @Override
    public ServiceResponse<Long> replaceSchool(long schoolId, School school) {
        StatusCode code = schoolExists(schoolId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
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

        return new ServiceResponse<>(
                schoolPersistence.replaceSchool(schoolId, school));
    }

    @Override
    public ServiceResponse<Long> updateSchool(long schoolId, School partialSchool) {
        ServiceResponse<School> sr = getSchool(schoolId);
        if(null == sr.getValue()) {
            return new ServiceResponse<>(sr.getCode());
        }
        partialSchool.mergePropertiesIfNull(schoolPersistence.selectSchool(schoolId));
        replaceSchool(schoolId, partialSchool);
        return new ServiceResponse<>(schoolId);
    }

    @Override
    public ServiceResponse<Long> deleteSchool(long schoolId) {
        StatusCode code = schoolExists(schoolId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        //Only need to delete the parent row, FK cascades deletes
        schoolPersistence.delete(schoolId);
        return new ServiceResponse<>((Long) null);
    }

    @Override
    public ServiceResponse<Long> associateAdvisors(long schoolId) {
        Comparator<SchoolYear> schoolYearComparator = (schoolYear1, schoolYear2) -> {
            return schoolYear2.getEndDate().compareTo(schoolYear1.getEndDate()); // use your logic
        };

        try {
            Collection<SchoolYear> years = pm.getSchoolYearManager().getAllSchoolYears(schoolId).getValue();
            List<SchoolYear> asList = new ArrayList<>(years);
            Collections.sort(asList, schoolYearComparator);
            Collection<Section> sections = pm.getSectionManager().getAllSectionsInYear(
                    schoolId, asList.get(asList.size() - 1).getId()
            ).getValue();
            for (Section s : sections) {
                if (s.getName().matches("(.*)Advisor(.*)") || s.getName().matches("(.*)Homeroom(.*)") ) {
                    //THese are advisor sections, find enrolled students, add advisor and update
                    List<Student> enrolledStudents = s.getEnrolledStudents();
                    for (Student stud : enrolledStudents) {
                        Iterator<Staff> it = s.getTeachers().iterator();
                        //TODO, if there is more then one teacher in advisor?
                        if (it.hasNext()) {
                            Staff advisor = it.next();
                            stud.setAdvisor(advisor);
                        }
                        //Update the student with their new advisor.
                        pm.getStudentManager().updateStudent(stud.getId(), stud);

                    }
                }
            }
            return new ServiceResponse<>(StatusCodes.getStatusCode(StatusCodeType.OK));
        } catch (Exception e) {
            return new ServiceResponse<>(StatusCodes.getStatusCode(StatusCodeType.UNKNOWN_INTERNAL_SERVER_ERROR));
        }


    }
}
