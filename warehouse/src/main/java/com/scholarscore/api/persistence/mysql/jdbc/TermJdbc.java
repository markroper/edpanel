    package com.scholarscore.api.persistence.mysql.jdbc;

import java.util.Collection;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Term;
import org.springframework.beans.factory.annotation.Autowired;
import com.scholarscore.api.persistence.mysql.EntityPersistence;
import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.transaction.Transactional;

@Transactional
public class TermJdbc implements EntityPersistence<Term> {
    @Autowired
    private HibernateTemplate hibernateTemplate;

    private EntityPersistence<SchoolYear> schoolYearPersistence;

    public TermJdbc() {
    }

    public TermJdbc(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    public void setHibernateTemplate(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Term> selectAll(long schoolYearId) {
        return (Collection<Term>)hibernateTemplate.findByNamedParam("from term t where t.schoolYear.id = :id", "id", schoolYearId);
    }

    @Override
    public Term select(long schoolYearId, long termId) {
        return hibernateTemplate.get(Term.class, termId);
    }

    @Override
    public Long insert(long schoolYearId, Term term) {
        injectSchoolYear(schoolYearId, term);
        Term out = hibernateTemplate.merge(term);
        return out.getId();
    }

    private void injectSchoolYear(long schoolYearId, Term term) {
        if (null == term.getSchoolYear()) {
            SchoolYear year = schoolYearPersistence.select(0L, schoolYearId);
            term.setSchoolYear(year);
        }
    }

    @Override
    public Long update(long schoolYearId, long termId, Term term) {
        injectSchoolYear(schoolYearId, term);
        hibernateTemplate.merge(term);
        return termId;
    }

    @Override
    public Long delete(long id) {
        Term term = select(0L, id);
        if (null != term) {
            hibernateTemplate.delete(term);
        }
        return id;
    }

    public EntityPersistence<SchoolYear> getSchoolYearPersistence() {
        return schoolYearPersistence;
    }

    public void setSchoolYearPersistence(EntityPersistence<SchoolYear> schoolYearPersistence) {
        this.schoolYearPersistence = schoolYearPersistence;
    }
}
