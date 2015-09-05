package com.scholarscore.api.persistence.mysql.jdbc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.scholarscore.api.persistence.mysql.StudentAssignmentPersistence;
import com.scholarscore.models.Assignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.api.persistence.mysql.EntityPersistence;
import com.scholarscore.api.persistence.mysql.mapper.StudentAssignmentMapper;
import com.scholarscore.models.StudentAssignment;
import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.transaction.Transactional;

@Transactional
public class StudentAssignmentJdbc
        implements StudentAssignmentPersistence {
    @Autowired
    private HibernateTemplate hibernateTemplate;
    private EntityPersistence<Assignment> assignmentPersistence;

    public StudentAssignmentJdbc() {
    }

    public StudentAssignmentJdbc(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    public void setHibernateTemplate(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    @Override
    public Collection<StudentAssignment> selectAll(long id) {
        return hibernateTemplate.loadAll(StudentAssignment.class);
    }

    @Override
    public StudentAssignment select(long assignmentId, long id) {
        return hibernateTemplate.get(StudentAssignment.class, id);
    }

    @Override
    public Long insert(long assignmentId, StudentAssignment entity) {
        injectAssignment(assignmentId, entity);
        StudentAssignment out = hibernateTemplate.merge(entity);
        return out.getId();
    }

    private void injectAssignment(long assignmentId, StudentAssignment entity) {
        if (null == entity.getAssignment()) {
            Assignment assignment = assignmentPersistence.select(0L, assignmentId);
            entity.setAssignment(assignment);
        }
    }

    @Override
    public Long update(long assignmentId, long id, StudentAssignment entity) {
        injectAssignment(assignmentId, entity);
        hibernateTemplate.merge(entity);
        return id;
    }

    @Override
    public Long delete(long studentAssignmentId) {
        StudentAssignment assignment = select(0L, studentAssignmentId);
        if (null != assignment) {
            hibernateTemplate.delete(assignment);
        }
        return studentAssignmentId;
    }

    public void setAssignmentPersistence(EntityPersistence<Assignment> assignmentPersistence) {
        this.assignmentPersistence = assignmentPersistence;
    }

    @Override
    public Collection<StudentAssignment> selectAllAssignmentsOneSectionOneStudent(long sectionId, long studentId) {
        return null;
    }
}
