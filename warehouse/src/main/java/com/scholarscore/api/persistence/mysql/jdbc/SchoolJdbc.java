package com.scholarscore.api.persistence.mysql.jdbc;

import java.util.Collection;

import com.scholarscore.models.School;

import org.springframework.beans.factory.annotation.Autowired;

import com.scholarscore.api.persistence.SchoolPersistence;

import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.transaction.Transactional;

@Transactional
public class SchoolJdbc implements SchoolPersistence {

    @Autowired
    private HibernateTemplate hibernateTemplate;

    public SchoolJdbc() {
    }

    public SchoolJdbc(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    public void setHibernateTemplate(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    @Override
    public Collection<School> selectAll() {
        return hibernateTemplate.loadAll(School.class);
    }

    @Override
    public School selectSchool(Long schoolId) {
        return hibernateTemplate.get(School.class, schoolId);
    }

    @Override
    public Long createSchool(School school) {
        School out = hibernateTemplate.merge(school);
        return out.getId();
    }

    @Override
    public Long replaceSchool(long schoolId, School school) {
        school.setId(schoolId);
        hibernateTemplate.merge(school);
        return schoolId;
    }

    @Override
    public Long delete(long schoolId) {
        School admin = hibernateTemplate.get(School.class, schoolId);
        hibernateTemplate.delete(admin);
        return schoolId;
    }
}
