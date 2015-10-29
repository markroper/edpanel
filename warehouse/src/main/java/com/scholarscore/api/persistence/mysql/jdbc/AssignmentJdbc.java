package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.EntityPersistence;
import com.scholarscore.models.assignment.Assignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.transaction.Transactional;
import java.util.Collection;

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
    @SuppressWarnings("unchecked")
    public Collection<Assignment> selectAll(long assignmentId) {
        return (Collection<Assignment>)hibernateTemplate.findByNamedParam("from assignment a where a.sectionFK = :id",
                "id", assignmentId);
    }

    @Override
    public Assignment select(long parentId, long id) {
        return hibernateTemplate.get(Assignment.class, id);
    }



    @Override
    public Long insert(long parentId, Assignment entity) {
        entity.setSectionFK(parentId);
        hibernateTemplate.save(entity);
        return entity.getId();
    }

    @Override
    public Long update(long parentId, long assignmentId,
                       Assignment entity) {
        entity.setSectionFK(parentId);
        entity.setId(assignmentId);
        hibernateTemplate.update(entity);
        return assignmentId;
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
