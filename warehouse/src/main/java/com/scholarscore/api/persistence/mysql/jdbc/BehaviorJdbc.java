package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.AdministratorPersistence;
import com.scholarscore.api.persistence.BehaviorPersistence;
import com.scholarscore.api.persistence.StudentPersistence;
import com.scholarscore.api.persistence.TeacherPersistence;
import com.scholarscore.models.Behavior;
import com.scholarscore.models.user.Staff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * User: jordan, mattg
 * Date: 8/8/15
 * Time: 6:30 PM
 */
@Transactional
public class BehaviorJdbc implements BehaviorPersistence {

    @Autowired
    private HibernateTemplate hibernateTemplate;
    
    private StudentPersistence studentPersistence;
    
    private TeacherPersistence teacherPersistence;
    private AdministratorPersistence administratorPersistence;

    private static String HQL_BASE =
            "from behavior b join fetch b.student st left join fetch st.homeAddress " +
            "left join fetch st.mailingAddress left join fetch st.contactMethods " +
            "left join fetch b.assigner a left join fetch a.homeAddress left join fetch a.contactMethods";

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Behavior> selectAll(long studentId, LocalDate cutoffDate) {
        if(null == cutoffDate) {
            return (Collection<Behavior>) hibernateTemplate.findByNamedParam(
                    HQL_BASE + " where b.student.id = :studentId", "studentId", studentId);
        } else {
            String[] params = new String[]{"studentId", "cutoffDate"};
            Object[] paramValues = new Object[]{ studentId, cutoffDate };
            return (Collection<Behavior>) hibernateTemplate.findByNamedParam(
                    HQL_BASE + " where b.student.id = :studentId and b.behaviorDate >= :cutoffDate",
                    params,
                    paramValues);
        }
    }

    @Override
    public Behavior select(long studentId, long behaviorId) {
        return hibernateTemplate.get(Behavior.class, behaviorId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Behavior selectBySourceSystemId(long studentId, long sourceSystemId) {
        String[] params = new String[]{"studentId", "sourceSystemId"};
        Object[] paramValues = new Object[]{ studentId, String.valueOf(sourceSystemId) };
        List<Behavior> objects = (List<Behavior>) hibernateTemplate.findByNamedParam(
                HQL_BASE + " where b.student.id = :studentId and b.remoteBehaviorId = :sourceSystemId",
                params,
                paramValues);
        if(null != objects && objects.size() > 0) {
            return objects.get(0);
        }
        return null;
    }

    @Override
    public Long createBehavior(long studentId, Behavior behavior) {
        injectStudent(studentId, behavior);
        injectAssignerIfPresent(behavior);
        Behavior result = hibernateTemplate.merge(behavior);
        return result.getId();
    }

    // adding or updating a behavior with an assigner is supported
    // as long as the assigner has (at least) an ID and user type
    private void injectAssignerIfPresent(Behavior behavior) {
        if (behavior != null 
                && behavior.getAssigner() != null 
                && behavior.getAssigner().getId() != null) {
            Long assignerId = behavior.getAssigner().getId();
            Staff assigner = teacherPersistence.select(assignerId);
            if (assigner == null) {
                assigner = administratorPersistence.select(assignerId);
            }
            if (assigner != null) {
                behavior.setAssigner(assigner);
            }
        }
    }

    private void injectStudent(long studentId, Behavior behavior) {
        if (null == behavior.getStudent()) {
            behavior.setStudent(studentPersistence.select(studentId));
        }
    }

    @Override
    public Long replaceBehavior(long studentId, long behaviorId, Behavior behavior) {
        injectStudent(studentId, behavior);
        injectAssignerIfPresent(behavior);
        behavior.setId(behaviorId);
        hibernateTemplate.merge(behavior);
        return behaviorId;
    }

    @Override
    public Long delete(long studentId, long behaviorId) {
        Behavior result = select(studentId, behaviorId);
        if (null != result) {
            hibernateTemplate.delete(result);
        }
        return behaviorId;
    }

    @Override
    public Long deleteBySsid(long studentId, long ssid) {
        Behavior result = selectBySourceSystemId(studentId, ssid);
        if (null != result) {
            hibernateTemplate.delete(result);
        }
        return ssid;
    }

    public HibernateTemplate getHibernateTemplate() {
        return hibernateTemplate;
    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }

    public void setStudentPersistence(StudentPersistence studentPersistence) {
        this.studentPersistence = studentPersistence;
    }

    public void setTeacherPersistence(TeacherPersistence teacherPersistence) {
        this.teacherPersistence = teacherPersistence;
    }

    public void setAdministratorPersistence(AdministratorPersistence administratorPersistence) {
        this.administratorPersistence = administratorPersistence;
    }
}
