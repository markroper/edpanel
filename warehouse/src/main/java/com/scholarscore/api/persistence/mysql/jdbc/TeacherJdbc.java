package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.mysql.TeacherPersistence;
import com.scholarscore.models.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

@Transactional
public class TeacherJdbc implements TeacherPersistence {

    @Autowired
    private HibernateTemplate hibernateTemplate;

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

    public Teacher select(String username) {
        List<Teacher> teachers = (List<Teacher>) hibernateTemplate.findByNamedParam("from teacher t where t.username = :username", "username", username);
        if (teachers.size() == 1) {
            return teachers.get(0);
        }
        return null;
    }

    @Override
    public Long createTeacher(Teacher teacher) {
        Teacher out = hibernateTemplate.merge(teacher);
        return out.getId();
    }

    @Override
    public void replaceTeacher(long id, Teacher teacher) {
        hibernateTemplate.merge(teacher);
    }

    @Override
    public Long delete(long id) {
        Teacher teacher = hibernateTemplate.get(Teacher.class, id);
        hibernateTemplate.delete(teacher);
        return id;
    }
}