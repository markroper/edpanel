package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.DashboardPersistence;
import com.scholarscore.models.dashboard.Dashboard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by markroper on 2/16/16.
 */
@Transactional
public class DashboardJdbc implements DashboardPersistence {
    @Autowired
    private HibernateTemplate hibernateTemplate;

    public DashboardJdbc() {
    }

    public DashboardJdbc(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    public void setHibernateTemplate(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    @Override
    public Long insertDashboard(Dashboard dashboard) {
        return null;
    }

    @Override
    public Dashboard selectDashboard(long schoolId, long dashboardId) {
        return null;
    }

    @Override
    public Dashboard selectDashboardForUser(long schoolId, long userId) {
        return null;
    }

    @Override
    public void deleteDashboard(long dashboardId) {

    }

    @Override
    public void updateDashboard(long schoolId, long dashboardId, Dashboard dash) {

    }
}
