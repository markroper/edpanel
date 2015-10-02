package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.AuthorityPersistence;
import com.scholarscore.api.persistence.TeacherPersistence;
import com.scholarscore.api.util.RoleConstants;
import com.scholarscore.models.Authority;
import com.scholarscore.models.user.Teacher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.transaction.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Transactional
public class TeacherJdbc implements TeacherPersistence {

    @Autowired
    private HibernateTemplate hibernateTemplate;
    
    private AuthorityPersistence authorityPersistence;

    public TeacherJdbc() {
    }

    public TeacherJdbc(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    public void setHibernateTemplate(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    @Override
    public Collection<Teacher> selectAll() {
        return hibernateTemplate.loadAll(Teacher.class);
    }

    @Override
    public Teacher select(long id) {
        return hibernateTemplate.get(Teacher.class, id);
    }

    @SuppressWarnings("unchecked")
    public Teacher select(String username) {
        String query = "select t from teacher t join t.user u where u.username = :username";
        List<Teacher> teachers = (List<Teacher>) hibernateTemplate.findByNamedParam(query, "username", username);
        if (teachers.size() == 1) {
            return teachers.get(0);
        }
        return null;
    }

    @Override
    public Long createTeacher(Teacher teacher) {
        assignDefaults(teacher);
        Teacher out = hibernateTemplate.merge(teacher);
        Authority auth = new Authority();
        auth.setAuthority(RoleConstants.TEACHER);
        auth.setUserId(out.getId());
        authorityPersistence.createAuthority(auth);
        return out.getId();
    }

    @Override
    public void replaceTeacher(long id, Teacher teacher) {
        assignDefaults(teacher);
        hibernateTemplate.merge(teacher);
    }

    @Override
    public Long delete(long id) {
        Teacher teacher = hibernateTemplate.get(Teacher.class, id);
        hibernateTemplate.delete(teacher);
        return id;
    }
    
    private static void assignDefaults(Teacher teacher) {
        if(null == teacher.getPassword()) {
            teacher.setPassword(UUID.randomUUID().toString());
        }
        if(null == teacher.getUsername()) {
            teacher.setUsername(UUID.randomUUID().toString());
        }
    }
    
    public void setAuthorityPersistence(AuthorityPersistence authorityPersistence) {
        this.authorityPersistence = authorityPersistence;
    }
}