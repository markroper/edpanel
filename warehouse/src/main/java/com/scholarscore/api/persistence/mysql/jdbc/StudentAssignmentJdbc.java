package com.scholarscore.api.persistence.mysql.jdbc;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.scholarscore.api.persistence.DbMappings;
import com.scholarscore.api.persistence.EntityPersistence;
import com.scholarscore.api.persistence.StudentAssignmentPersistence;
import com.scholarscore.models.Assignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

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

    //TODO we should be getting using the assignment Id and student ID, not just the student_assignmnet_id
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
    @SuppressWarnings("unchecked")
    public Collection<StudentAssignment> selectAllAssignmentsOneSectionOneStudent(long sectionId, long studentId) {
        String[] paramNames = new String[]{ "sectionId", "studentId" };
        Object[] paramValues = new Object[]{ new Long(sectionId), new Long(studentId) };
        List<StudentAssignment> studentAssignments = (List<StudentAssignment>)
                hibernateTemplate.findByNamedParam(
                        "select sa from student_assignment sa inner join fetch sa.assignment a inner join fetch sa.student s "
                        + "where a.sectionFK = :sectionId and s.id = :studentId", 
                        paramNames, 
                        paramValues);
        return studentAssignments;
    }
}
