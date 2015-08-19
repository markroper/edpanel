package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.AdministratorPersistence;
import com.scholarscore.models.Administrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.transaction.Transactional;
import java.util.*;


@Transactional
public class AdministratorJdbc implements AdministratorPersistence {
    @Autowired
    private HibernateTemplate hibernateTemplate;

    public AdministratorJdbc() {
    }

    public AdministratorJdbc(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    public void setHibernateTemplate(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    @Override
    public Collection<Administrator> selectAll() {
        return hibernateTemplate.loadAll(Administrator.class);
    }

    @Override
    public Administrator select(long administratorId) {
        return hibernateTemplate.get(Administrator.class, administratorId);
    }

    @Override
    public Long createAdministrator(Administrator administrator) {
        Long value = (Long)hibernateTemplate.save(administrator);
        return value;
    }

    @Override
    public void replaceAdministrator(long administratorId, Administrator administrator) {
        hibernateTemplate.update(administrator);
    }

    @Override
    public Long delete(long administratorId) {
        Administrator admin = hibernateTemplate.get(Administrator.class, administratorId);
        hibernateTemplate.delete(admin);
        return administratorId;
    }
}
