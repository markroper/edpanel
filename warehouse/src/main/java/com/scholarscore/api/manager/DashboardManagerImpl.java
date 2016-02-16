package com.scholarscore.api.manager;

import com.scholarscore.api.persistence.DashboardPersistence;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.dashboard.Dashboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by markroper on 2/16/16.
 */
public class DashboardManagerImpl implements DashboardManager {
    private final static Logger LOGGER = LoggerFactory.getLogger(DashboardManagerImpl.class);

    @Autowired
    private DashboardPersistence dashboardPersistence;

    @Autowired
    private OrchestrationManager pm;

    private static final String NOTIFICATION = "dashboard";

    public void setDashboardPersistence(DashboardPersistence dpersist) {
        this.dashboardPersistence = dpersist;
    }

    public void setPm(OrchestrationManager pm) {
        this.pm = pm;
    }
    @Override
    public ServiceResponse<Dashboard> getDashboard(Long dashboardId) {
        return null;
    }

    @Override
    public ServiceResponse<Dashboard> getDashboard(Long schoolId, Long dashboardId) {
        return null;
    }

    @Override
    public ServiceResponse<EntityId> createDashboard(Long schoolId, Dashboard dashboard) {
        return null;
    }

    @Override
    public ServiceResponse<Void> replaceDashboard(Long schoolId, Long dashboardId, Dashboard dashboard) {
        return null;
    }

    @Override
    public ServiceResponse<Void> deleteDashboard(Long schoolId, Long dashboardId) {
        return null;
    }
}
