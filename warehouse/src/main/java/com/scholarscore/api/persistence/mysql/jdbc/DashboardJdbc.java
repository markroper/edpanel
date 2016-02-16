package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.DashboardPersistence;
import com.scholarscore.models.dashboard.Dashboard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by markroper on 2/16/16.
 */
@Transactional
public class DashboardJdbc implements DashboardPersistence {
    public static final String DASH_HQL = "from dashboard d " +
            "join fetch rows r " +
            "join fetch r.reports rs " +
            "join fetch rs.chartQuery " +
            "join fetch rs.clickTableQuery";
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
        dashboard.setId(null);
        Dashboard d =  hibernateTemplate.merge(dashboard);
        return d.getId();
    }

    @Override
    public Dashboard selectDashboard(Long schoolId, Long dashboardId) {
        return hibernateTemplate.get(Dashboard.class, dashboardId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Dashboard selectDashboardForUser(Long schoolId, Long userId) {
        String[] params = new String[]{ "userId", "schoolId" };
        Object[] paramVals = new Object[]{ userId, schoolId };
        List<Dashboard> dashes = (List<Dashboard>)hibernateTemplate.findByNamedParam(
                DASH_HQL + " where d.userId = :userId and d.schoolId = :schoolId",
                params,
                paramVals);
        if(null == dashes || dashes.isEmpty()) {
            return null;
        }
        return dashes.get(0);
    }

    @Override
    public void deleteDashboard(Long dashboardId) {
        Dashboard d = selectDashboard(null, dashboardId);
        if(null != d) {
            hibernateTemplate.delete(d);
        }
    }

    @Override
    public void updateDashboard(Long schoolId, Long dashboardId, Dashboard dash) {
        Dashboard d = selectDashboard(schoolId, dashboardId);
        if(null != d) {
            hibernateTemplate.merge(dash);
        }
    }
}
