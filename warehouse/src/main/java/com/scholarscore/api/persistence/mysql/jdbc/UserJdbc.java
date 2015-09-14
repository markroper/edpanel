package com.scholarscore.api.persistence.mysql.jdbc;

import java.util.Collection;
import java.util.List;

import com.scholarscore.api.persistence.AdministratorPersistence;
import com.scholarscore.api.persistence.mysql.StudentPersistence;
import com.scholarscore.api.persistence.mysql.TeacherPersistence;
import com.scholarscore.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.scholarscore.api.persistence.mysql.UserPersistence;
import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.transaction.Transactional;

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

    public UserJdbc(TeacherPersistence teacherPersistence,
                    AdministratorPersistence administratorPersistence,
                    StudentPersistence studentPersistence) {
        this.teacherPersistence = teacherPersistence;
        this.administratorPersistence = administratorPersistence;
        this.studentPersistence = studentPersistence;
    }

    @Override
    public Collection<User> selectAllUsers() {
        return hibernateTemplate.loadAll(User.class);
    }

    @Override
    public Identity getIdentity(String username) {
        Teacher teacher = teacherPersistence.select(username);
        if (null != teacher) {
            return teacher;
        }
        Administrator administrator = administratorPersistence.select(username);
        if (null != administrator) {
            return administrator;
        }
        Student student = studentPersistence.select(username);
        if (null != student) {
            return student;
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public User selectUser(String username) {
        List values = hibernateTemplate.findByNamedParam("from user u where u.username = :username", "username", username);
        if (values.size() == 1) {
            return (User)values.get(0);
        }
        return null;
    }

    @Override
    public String createUser(User user) {
        hibernateTemplate.merge(user);
        return user.getUsername();
    }

    @Override
    @SuppressWarnings("unchecked")
    public String replaceUser(String username, User value) {
        User fromDB = selectUser(username);
        fromDB.setName(value.getName());
        fromDB.setPassword(value.getPassword());
        fromDB.setEnabled(value.getEnabled());
        hibernateTemplate.merge(fromDB);
        return username;
    }

    @Override
    public String deleteUser(String username) {
        User fromDB = selectUser(username);
        if (null != fromDB) {
            hibernateTemplate.delete(fromDB);
        }
        return username;
    }

    public HibernateTemplate getHibernateTemplate() {
        return hibernateTemplate;
    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }
}
