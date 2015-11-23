package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.EntityPersistence;
import com.scholarscore.api.persistence.SchoolPersistence;
import com.scholarscore.models.SchoolYear;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;

import javax.transaction.Transactional;
import java.util.Collection;

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
        SchoolYear out = hibernateTemplate.merge(entity);
        return out.getId();
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
