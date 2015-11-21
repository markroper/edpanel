package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.EntityPersistence;
import com.scholarscore.api.persistence.StudentAssignmentPersistence;
import com.scholarscore.models.assignment.Assignment;
import com.scholarscore.models.assignment.StudentAssignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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
    @SuppressWarnings("unchecked")
    public Collection<StudentAssignment> selectAll(long id) {
        String[] params = new String[]{"assignmentId"};
        Object[] paramValues = new Object[]{ new Long(id) };
        List<StudentAssignment> objects = (List<StudentAssignment>) hibernateTemplate.findByNamedParam(
                "from student_assignment s where s.assignment.id = :assignmentId",
                params,
                paramValues);
        return objects;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<StudentAssignment> selectAllBetweenDates(long studentId, Date start, Date end) {
        String[] params = new String[]{"studentId", "startDate", "endDate"};
        Object[] paramValues = new Object[]{ new Long(studentId), start, end };
        List<StudentAssignment> objects = (List<StudentAssignment>) hibernateTemplate.findByNamedParam(
                "from student_assignment s " +
                "join fetch s.student st left join fetch st.homeAddress left join fetch st.mailingAddress " +
                "left join fetch st.contactMethods join fetch s.assignment a " +
                "where st.id = :studentId and a.dueDate >= :startDate and a.dueDate <= :endDate",
                params,
                paramValues);
        return objects;
    }


    @Override
    public StudentAssignment select(long assignmentId, long id) {
        return hibernateTemplate.get(StudentAssignment.class, id);
    }

    @Override
    public Long insert(long assignmentId, StudentAssignment entity) {
        hibernateTemplate.save(entity);
        return entity.getId();
    }

    @Override
    public List<Long> insertAll(long assignmentId, List<StudentAssignment> studentAssignmentList) {
        int i = 0;
        List<Long> ids = new ArrayList<>();
        for(StudentAssignment sa : studentAssignmentList) {
            hibernateTemplate.save(sa);
            ids.add(sa.getId());
            //Release newly created entities from hibernates session im-memory storage
            if(i % 20 == 0) {
                hibernateTemplate.flush();
                hibernateTemplate.clear();
            }
            i++;
        }
        return ids;
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
        hibernateTemplate.update(entity);
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
                        "select sa from student_assignment sa inner join fetch sa.assignment a " +
                        "inner join fetch sa.student s left join fetch s.mailingAddress " +
                        "left join fetch s.homeAddress left join fetch s.contactMethods " +
                        "where a.sectionFK = :sectionId and s.id = :studentId",
                        paramNames, 
                        paramValues);
        return studentAssignments;
    }

    @SuppressWarnings("unchecked")
    public Collection<StudentAssignment> selectAllAttendanceSection(long sectionId, long studentId) {
        String[] paramNames = new String[]{ "sectionId", "studentId" };
        Object[] paramValues = new Object[]{ new Long(sectionId), new Long(studentId) };
        List<StudentAssignment> studentAssignments = (List<StudentAssignment>)
                hibernateTemplate.findByNamedParam(
                        "select sa from student_assignment sa inner join fetch sa.assignment a inner join fetch sa.student s "
                                + "where a.sectionFK = :sectionId and s.id = :studentId and a.type = 1",
                        paramNames,
                        paramValues);
        //Would it be better to filter for the data range here as oppose to in java code later?
        return studentAssignments;
    }
}
