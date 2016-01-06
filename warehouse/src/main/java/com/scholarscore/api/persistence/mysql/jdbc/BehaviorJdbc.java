package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.BehaviorPersistence;
import com.scholarscore.api.persistence.StudentPersistence;
import com.scholarscore.api.persistence.UserPersistence;
import com.scholarscore.models.Behavior;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;

import javax.transaction.Transactional;
import java.util.Collection;

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
    private UserPersistence userPersistence;

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Behavior> selectAll(long studentId) {
        return (Collection<Behavior>)hibernateTemplate.findByNamedParam("from behavior b where b.student.id = :studentId", "studentId", studentId);
    }

    @Override
    public Behavior select(long studentId, long behaviorId) {
        Behavior result = hibernateTemplate.get(Behavior.class, behaviorId);
        return result;
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
            behavior.setAssigner(userPersistence.selectUser(behavior.getAssigner().getId()));
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

    public HibernateTemplate getHibernateTemplate() {
        return hibernateTemplate;
    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }

    public void setStudentPersistence(StudentPersistence studentPersistence) {
        this.studentPersistence = studentPersistence;
    }

    public void setUserPersistence(UserPersistence userPersistence) {
        this.userPersistence = userPersistence;
    }
}
