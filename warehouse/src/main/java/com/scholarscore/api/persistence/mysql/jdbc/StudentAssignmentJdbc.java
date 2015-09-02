package com.scholarscore.api.persistence.mysql.jdbc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
        implements EntityPersistence<StudentAssignment>{
    @Autowired
    private HibernateTemplate hibernateTemplate;

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
    public StudentAssignment select(long parentId, long id) {
        return hibernateTemplate.get(StudentAssignment.class, id);
    }

    @Override
    public Long insert(long parentId, StudentAssignment entity) {
        StudentAssignment out = hibernateTemplate.merge(entity);
        return out.getId();
    }

    @Override
    public Long update(long parentId, long id, StudentAssignment entity) {
        hibernateTemplate.merge(entity);
        return id;
    }

    @Override
    public Long delete(long id) {
        StudentAssignment assignment = select(0L, id);
        if (null != assignment) {
            hibernateTemplate.delete(assignment);
        }
        return id;
    }
}
