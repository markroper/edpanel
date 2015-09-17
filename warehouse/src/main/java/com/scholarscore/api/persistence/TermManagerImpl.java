package com.scholarscore.api.persistence;

import com.scholarscore.api.persistence.mysql.EntityPersistence;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.Section;
import com.scholarscore.models.Student;
import com.scholarscore.models.Term;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by cwallace on 9/16/2015.
 */
public class TermManagerImpl implements TermManager {

    private EntityPersistence<Term> termPersistence;

    private PersistenceManager pm;

    private static final String TERM = "term";

    public void setTermPersistence(EntityPersistence<Term> termPersistence) {
        this.termPersistence = termPersistence;
    }

    public void setPm(PersistenceManager pm) {
        this.pm = pm;
    }

    @Override
    public ServiceResponse<Collection<Term>> getAllTerms(long schoolId, long yearId) {
        StatusCode code = pm.getSchoolYearManager().schoolYearExists(schoolId, yearId);
        if(!code.isOK()) {
            return new ServiceResponse<Collection<Term>>(code);
        }
        Collection<Term> terms = termPersistence.selectAll(yearId);
        return new ServiceResponse<Collection<Term>>(new ArrayList<Term>(terms));
    }

    @Override
    public StatusCode termExists(long schoolId, long yearId, long termId) {
        StatusCode code = pm.getSchoolYearManager().schoolYearExists(schoolId, yearId);
        if(!code.isOK()) {
            return code;
        }
        Term t = termPersistence.select(yearId, termId);
        if(null != t) {
            return StatusCodes.getStatusCode(StatusCodeType.OK);
        }
        return StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[] { TERM, termId });
    }

    @Override
    public ServiceResponse<Term> getTerm(long schoolId, long yearId, long termId) {
        StatusCode code = termExists(schoolId, yearId, termId);
        if(!code.isOK()) {
            return new ServiceResponse<Term>(code);
        }
        return new ServiceResponse<Term>(termPersistence.select(yearId, termId));
    }

    @Override
    public ServiceResponse<Long> createTerm(long schoolId, long yearId, Term term) {
        StatusCode code = pm.getSchoolYearManager().schoolYearExists(schoolId, yearId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        return new ServiceResponse<Long>(termPersistence.insert(yearId, term));
    }

    @Override
    public ServiceResponse<Long> replaceTerm(long schoolId, long yearId, long termId, Term term) {
        StatusCode code = pm.getSchoolYearManager().schoolYearExists(schoolId, yearId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        termPersistence.update(yearId, termId, term);
        return new ServiceResponse<Long>(termId);
    }

    @Override
    public ServiceResponse<Long> updateTerm(long schoolId, long yearId,
                                            long termId, Term partialTerm) {
        StatusCode code = pm.getSchoolYearManager().schoolYearExists(schoolId, yearId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        partialTerm.mergePropertiesIfNull(termPersistence.select(yearId, termId));
        return replaceTerm(schoolId, yearId, termId, partialTerm);
    }

    @Override
    public ServiceResponse<Long> deleteTerm(long schoolId, long yearId,
                                            long termId) {
        StatusCode code = termExists(schoolId, yearId, termId);
        if(!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        termPersistence.delete(termId);
        return new ServiceResponse<Long>((Long) null);
    }

    @Override
    public ServiceResponse<Collection<Student>> getAllStudentsByTermTeacher(
            long schoolId, long schoolYearId, long termId, long teacherId) {
        StatusCode code = pm.getTeacherManager().teacherExists(teacherId);
        if(!code.isOK()) {
            return new ServiceResponse<Collection<Student>>(code);
        }
        code = termExists(schoolId, schoolYearId, termId);
        if(!code.isOK()) {
            return new ServiceResponse<Collection<Student>>(code);
        }
        Collection<Section> sections = pm.getSectionPersistence().selectAllSectionForTeacher(termId, teacherId);
        Set<Student> studentsByTeacherTerm = new HashSet<>();
        for(Section s : sections) {
            Collection<Student> students = pm.getStudentPersistence().selectAllStudentsInSection(s.getId());
            if(null != students && !students.isEmpty()) {
                studentsByTeacherTerm.addAll(students);
            }
        }
        return new ServiceResponse<Collection<Student>>(studentsByTeacherTerm);
    }
}
