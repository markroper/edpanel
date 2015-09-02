package com.scholarscore.api.persistence.mysql.jdbc;

import java.util.Collection;

import com.scholarscore.models.Section;
import com.scholarscore.models.Term;
import org.springframework.beans.factory.annotation.Autowired;
import com.scholarscore.api.persistence.mysql.EntityPersistence;
import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.transaction.Transactional;

@Transactional
public class SectionJdbc implements EntityPersistence<Section> {
    @Autowired
    private HibernateTemplate hibernateTemplate;

    private EntityPersistence<Term> termEntityPersistence;

    public SectionJdbc() {
    }

    public SectionJdbc(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    public void setHibernateTemplate(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Section> selectAll(long termId) {
        return (Collection<Section>)hibernateTemplate.findByNamedParam("from section s where s.term_fk = :id", "id", termId);
    }

    @Override
    public Section select(long termId, long sectionId) {
        return hibernateTemplate.get(Section.class, sectionId);
    }

    @Override
    public Long insert(long termId, Section entity) {
        injectTerm(termId, entity);
        Section result = hibernateTemplate.merge(entity);
        return result.getId();
    }

    private void injectTerm(long termId, Section entity) {
        if (null == entity.getTerm()) {
            entity.setTerm(termEntityPersistence.select(0L, termId));
        }
    }

    @Override
    public Long update(long termId, long sectionId, Section entity) {
        injectTerm(termId, entity);
        if (null == entity.getId()) {
            entity.setId(sectionId);
        }
        hibernateTemplate.merge(entity);
        return sectionId;
    }

    @Override
    public Long delete(long id) {
        Section term = select(0L, id);
        if (null != term) {
            hibernateTemplate.delete(term);
        }
        return id;
    }

    public EntityPersistence<Term> getTermEntityPersistence() {
        return termEntityPersistence;
    }

    public void setTermEntityPersistence(EntityPersistence<Term> termEntityPersistence) {
        this.termEntityPersistence = termEntityPersistence;
    }
}
