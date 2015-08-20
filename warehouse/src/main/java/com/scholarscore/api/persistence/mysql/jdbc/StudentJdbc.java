package com.scholarscore.api.persistence.mysql.jdbc;

import java.util.Collection;
import com.scholarscore.models.Student;
import org.springframework.beans.factory.annotation.Autowired;
import com.scholarscore.api.persistence.mysql.StudentPersistence;
import org.springframework.orm.hibernate4.HibernateTemplate;
import javax.transaction.Transactional;

@Transactional
public class StudentJdbc implements StudentPersistence {

    @Autowired
    private HibernateTemplate hibernateTemplate;

    public StudentJdbc() {
    }

    public StudentJdbc(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    public void setHibernateTemplate(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    @Override
    public Collection<Student> selectAll() {
        return hibernateTemplate.loadAll(Student.class);
    }

    @Override
    public Collection<Student> selectAllStudentsInSection(long sectionId) {
        return null;
    }

    @Override
    public Student select(long studentId) {
        return hibernateTemplate.get(Student.class, studentId);
    }

    @Override
    public Long createStudent(Student student) {
        Long value = (Long) hibernateTemplate.save(student);
        return value;
    }

    @Override
    public Long replaceStudent(long studentId, Student student) {
        hibernateTemplate.update(student);
        return studentId;
    }

    @Override
    public Long delete(long studentId) {
        Student admin = hibernateTemplate.get(Student.class, studentId);
        hibernateTemplate.delete(admin);
        return studentId;
    }
}
