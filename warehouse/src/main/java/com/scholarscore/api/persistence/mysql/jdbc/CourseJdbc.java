package com.scholarscore.api.persistence.mysql.jdbc;

import java.util.Collection;

import com.scholarscore.api.persistence.mysql.SchoolPersistence;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import org.springframework.beans.factory.annotation.Autowired;
import com.scholarscore.api.persistence.mysql.EntityPersistence;
import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.transaction.Transactional;

@Transactional
public class CourseJdbc implements EntityPersistence<Course> {

    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private SchoolPersistence schoolPersistence;

    public CourseJdbc() {
    }

    public CourseJdbc(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    public void setHibernateTemplate(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Course> selectAll(long schoolId) {
        return (Collection<Course>)hibernateTemplate.findByNamedParam("from course c where c.school.id = :id", "id", schoolId);
    }

    @Override
    public Course select(long schoolId, long id) {
        return hibernateTemplate.get(Course.class, id);
    }

    @Override
    public Long insert(long schoolId, Course entity) {
        Long value = (Long) hibernateTemplate.save(entity);
        return value;
    }

    @Override
    public Long update(long schoolId, long id, Course entity) {
        entity.setId(id);
        if (null == entity.getSchool()) {
            School school = schoolPersistence.selectSchool(schoolId);
            entity.setSchool(school);
        }
        hibernateTemplate.update(entity);
        return id;
    }


    @Override
    public Long delete(long id) {
        Course course = hibernateTemplate.get(Course.class, id);
        if (null != course) {
            hibernateTemplate.delete(course);
        }
        return id;
    }

    public SchoolPersistence getSchoolPersistence() {
        return schoolPersistence;
    }

    public void setSchoolPersistence(SchoolPersistence schoolPersistence) {
        this.schoolPersistence = schoolPersistence;
    }
}
