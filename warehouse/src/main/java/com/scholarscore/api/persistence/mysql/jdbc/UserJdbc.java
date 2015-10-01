package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.AdministratorPersistence;
import com.scholarscore.api.persistence.StudentPersistence;
import com.scholarscore.api.persistence.TeacherPersistence;
import com.scholarscore.api.persistence.UserPersistence;
import com.scholarscore.models.Identity;
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

//    @Override
//    public Identity getIdentity(String username) {
//
//        Teacher teacher = teacherPersistence.select(username);
//        if (null != teacher) { return teacher; }
//
//        Administrator administrator = administratorPersistence.select(username);
//        if (null != administrator) { return administrator; }
//        
//        Student student = studentPersistence.select(username);
//        if (null != student) { return student; }
//        
//        return null;
//    }

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
//        fromDB.setName(value.getName());
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
