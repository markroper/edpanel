package com.scholarscore.api.persistence.mysql.jdbc;

import java.sql.Timestamp;
import java.util.Collection;

import com.scholarscore.models.Assignment;
import org.springframework.beans.factory.annotation.Autowired;
import com.scholarscore.api.persistence.mysql.EntityPersistence;
import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.transaction.Transactional;

@Transactional
public class AssignmentJdbc implements EntityPersistence<Assignment> {
    @Autowired
    private HibernateTemplate hibernateTemplate;

    public AssignmentJdbc() {
    }

    public AssignmentJdbc(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    public void setHibernateTemplate(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    @Override
    public Collection<Assignment> selectAll(long id) {
        return (Collection<Assignment>)hibernateTemplate.find("from assignment with section_fk = (?)", "section_fk", id);
    }

    @Override
    public Assignment select(long parentId, long id) {
        return hibernateTemplate.get(Assignment.class, id);
    }

    @Override
    public Long insert(long parentId, Assignment entity) {
        entity.setSectionFK(parentId);
        return (Long)hibernateTemplate.save(entity);
    }

    @Override
    public Long update(long parentId, long id, Assignment entity) {
        entity.setSectionFK(parentId);
        hibernateTemplate.update(entity);
        return id;
    }

    @Override
    public Long delete(long id) {
        Assignment assignment = select(0L, id);
        if (null != assignment) {
            hibernateTemplate.delete(assignment);
        }
        return id;
    }

}
