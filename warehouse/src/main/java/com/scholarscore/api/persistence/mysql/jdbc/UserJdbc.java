package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.AdministratorPersistence;
import com.scholarscore.api.persistence.StudentPersistence;
import com.scholarscore.api.persistence.TeacherPersistence;
import com.scholarscore.api.persistence.UserPersistence;
import com.scholarscore.models.Term;
import com.scholarscore.models.user.Administrator;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.Teacher;
import com.scholarscore.models.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.transaction.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * Maintain User identities separate from Student / Teacher entities for Spring Security
 * 
 * @author mattg
 */
@Transactional
public class UserJdbc implements UserPersistence {

    private TeacherPersistence teacherPersistence;
    private AdministratorPersistence administratorPersistence;
    private StudentPersistence studentPersistence;

    @Autowired
    private HibernateTemplate hibernateTemplate;

    public UserJdbc() {}

    @Override
    public Collection<User> selectAllUsers() {
        return hibernateTemplate.loadAll(User.class);
    }

    @Override
    public User selectUser(Long userId) {
        return hibernateTemplate.get(User.class, userId);
    }

    @Override
    public User selectUserByName(String username) {
        List<?> values = hibernateTemplate.findByNamedParam("from user u where u.username = :username", "username", username);
        if (values.size() == 1) {
            return (User)values.get(0);
        }
        return null;
    }
    
    @Override
    public Long createUser(User user) {
        User out = hibernateTemplate.merge(user);
        return out.getId();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Long replaceUser(Long userId, User value) {
        value.setId(userId);
//        User fromDB = selectUser(userId);
//        fromDB.setPassword(value.getPassword());
//        fromDB.setEnabled(value.getEnabled());
/*
        fromDB.setEmailAddress(value.getEmailAddress());
        fromDB.setEmailConfirmCode(value.getEmailConfirmCode());
        fromDB.setEmailConfirmCodeTime(value.getEmailConfirmCodeTime());
        fromDB.setEmailConfirmed(value.getEmailConfirmed());
        fromDB.setPhoneNumber(value.getPhoneNumber());
        fromDB.setPhoneConfirmCode(value.getPhoneConfirmCode());
        fromDB.setPhoneConfirmCodeTime(value.getPhoneConfirmCodeTime());
        fromDB.setPhoneConfirmed(value.getPhoneConfirmed());
        */
        hibernateTemplate.merge(value);
        return userId;
    }

    @Override
    public Long deleteUser(Long userId) {
        User fromDB = selectUser(userId);
        if (null != fromDB) {
            hibernateTemplate.delete(fromDB);
        }
        return userId;
    }

    public HibernateTemplate getHibernateTemplate() {
        return hibernateTemplate;
    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }

    public void setTeacherPersistence(TeacherPersistence teacherPersistence) {
        this.teacherPersistence = teacherPersistence;
    }

    public void setAdministratorPersistence(AdministratorPersistence administratorPersistence) {
        this.administratorPersistence = administratorPersistence;
    }

    public void setStudentPersistence(StudentPersistence studentPersistence) {
        this.studentPersistence = studentPersistence;
    }
}
