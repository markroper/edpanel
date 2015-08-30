package com.scholarscore.api.persistence.mysql.jdbc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.scholarscore.api.persistence.mysql.SchoolPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.api.persistence.mysql.EntityPersistence;
import com.scholarscore.api.persistence.mysql.mapper.SchoolYearMapper;
import com.scholarscore.models.SchoolYear;
import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.transaction.Transactional;

@Transactional
public class SchoolYearJdbc implements EntityPersistence<SchoolYear> {
    @Autowired
    private HibernateTemplate hibernateTemplate;
    private SchoolPersistence schoolPersistence;

    public SchoolYearJdbc() {
    }

    public SchoolYearJdbc(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    public void setHibernateTemplate(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<SchoolYear> selectAll(long schoolId) {
        return (Collection<SchoolYear>)hibernateTemplate
                .findByNamedParam("from SchoolYear sy where sy.school.id = :id", "id", schoolId);
    }

    @Override
    public SchoolYear select(long schoolId, long schoolYearId) {
        return hibernateTemplate.get(SchoolYear.class, schoolYearId);
    }

    @Override
    public Long insert(long parentId, SchoolYear entity) {
        fillInSchool(parentId, entity);
        return (Long)hibernateTemplate.save(entity);
    }

    private void fillInSchool(long schoolId, SchoolYear schoolYear) {
        if (null == schoolYear.getSchool()) {
            schoolYear.setSchool(schoolPersistence.selectSchool(schoolId));
        }
    }

    @Override
    public Long update(long parentId, long id, SchoolYear entity) {
        fillInSchool(parentId, entity);
        entity.setId(id);
        hibernateTemplate.update(entity);
        return id;
    }

    @Override
    public Long delete(long schoolYearId) {
        SchoolYear year = select(0L, schoolYearId);
        if (null != year) {
            hibernateTemplate.delete(year);
        }
        return schoolYearId;
    }

    public SchoolPersistence getSchoolPersistence() {
        return schoolPersistence;
    }

    public void setSchoolPersistence(SchoolPersistence schoolPersistence) {
        this.schoolPersistence = schoolPersistence;
    }
}
