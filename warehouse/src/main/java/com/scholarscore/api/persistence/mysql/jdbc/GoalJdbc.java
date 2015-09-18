package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.mysql.GoalPersistence;
import com.scholarscore.api.persistence.mysql.StudentPersistence;
import com.scholarscore.models.Goal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTemplate;
import javax.transaction.Transactional;

/**
 * Created by cwallace on 9/17/2015.
 */
@Transactional
public class GoalJdbc implements GoalPersistence {

    @Autowired
    private HibernateTemplate hibernateTemplate;
    private StudentPersistence studentPersistence;

    public void setHibernateTemplate(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    @Override
    public Long createGoal(Long studentId, Goal goal) {
        injectStudent(studentId, goal);
        Goal result = hibernateTemplate.merge(goal);
        return result.getId();
    }

    private void injectStudent(long studentId, Goal goal) {
        if (null == goal.getStudent()) {
            goal.setStudent(studentPersistence.select(studentId));
        }
    }

}
