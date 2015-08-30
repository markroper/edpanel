package com.scholarscore.api.persistence.mysql.jdbc;

import java.util.Collection;

import com.scholarscore.models.Section;
import org.springframework.beans.factory.annotation.Autowired;
import com.scholarscore.api.persistence.mysql.EntityPersistence;
import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.transaction.Transactional;

@Transactional
public class SectionJdbc implements EntityPersistence<Section> {
    @Autowired
    private HibernateTemplate hibernateTemplate;

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
        return (Long)hibernateTemplate.save(entity);
    }

    @Override
    public Long update(long termId, long sectionId, Section entity) {
        hibernateTemplate.update(entity);
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

}
