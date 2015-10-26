package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.AdministratorPersistence;
import com.scholarscore.api.persistence.StudentPersistence;
import com.scholarscore.api.persistence.TeacherPersistence;
import com.scholarscore.api.persistence.UserPersistence;
import com.scholarscore.models.user.ContactMethod;
import com.scholarscore.models.user.Person;
import com.scholarscore.models.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.transaction.Transactional;
import java.util.ArrayList;
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
    public Collection<User> selectAllUsersInSchool(Long schoolId) {
        List<User> values = hibernateTemplate.loadAll(User.class);
        return filterUsersBySchool(values, schoolId);
    }

    @Override
    public Collection<User> selectAllUsersInSchool(
            Long schoolId,
            boolean enabled) {
        @SuppressWarnings("unchecked")
        List<User> values = 
                (List<User>) hibernateTemplate.findByNamedParam("from user u where u.enabled = :enabled", "enabled", enabled);
        return filterUsersBySchool(values, schoolId);
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
    public Long replaceUser(Long userId, User value) {

        User fromDB = selectUser(userId);
        
        // a subset of a fields are 'internal' to the state of the user object and are preserved 
        // even when the rest of the object is replaced.
        value.setId(userId);
        value.setType(fromDB.getType());
        value.setPassword(fromDB.getPassword());
        value.setOneTimePass(fromDB.getOneTimePass());
        value.setOneTimePassCreated(fromDB.getOneTimePassCreated());
        value.setEnabled(fromDB.getEnabled());
        ContactMethod.mergeContactMethods(value.getContactMethods(), fromDB.getContactMethods());
                
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
    
    private static Collection<User> filterUsersBySchool(Collection<User> users, Long schoolId) {
        if(null == users || null == schoolId) {
            return null;
        }
        Collection<User> filteredValues = new ArrayList<User>();
        for(User u: users) {
            Person p = (Person) u;
            if(null != p.getCurrentSchoolId() && p.getCurrentSchoolId().equals(schoolId)) {
                filteredValues.add(p);
            }  
        }
        return filteredValues;
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
