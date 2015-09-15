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
    public Administrator select(String username) {
        List<Administrator> users = (List<Administrator>)hibernateTemplate.findByNamedParam("from administrator a where a.username = :username", "username", username);
        if (users.size() == 1) {
            return users.get(0);
        }
        return null;
    }

    @Override
    public Long createAdministrator(Administrator administrator) {
        Administrator adminOut = hibernateTemplate.merge(administrator);
        administrator.setId(adminOut.getId());
        return adminOut.getId();
    }

    @Override
    public void replaceAdministrator(long administratorId, Administrator administrator) {
        hibernateTemplate.merge(administrator);
    }

    @Override
    public Long delete(long administratorId) {
        Administrator admin = hibernateTemplate.get(Administrator.class, administratorId);
        hibernateTemplate.delete(admin);
        return administratorId;
    }
}
