package com.scholarscore.api.manager;

import com.scholarscore.api.persistence.DashboardPersistence;
import com.scholarscore.api.security.config.UserDetailsProxy;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
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

    private static final String DASHBOARD = "dashboard";

    public void setDashboardPersistence(DashboardPersistence dpersist) {
        this.dashboardPersistence = dpersist;
    }

    public void setPm(OrchestrationManager pm) {
        this.pm = pm;
    }

    @Override
    public ServiceResponse<Dashboard> getDashboard(Long schoolId) {
        UserDetailsProxy udp = pm.getUserManager().getCurrentUserDetails();

        Dashboard d = dashboardPersistence.selectDashboardForUser(schoolId, udp.getUser().getId());
        if(null == d) {
            d = dashboardPersistence.selectDashboardForUser(schoolId, null);
        }
        if(null == d) {
            return new ServiceResponse<>(
                    StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND,
                    new Object[]{DASHBOARD, null}));
        }
        return new ServiceResponse<>(d);
    }

    @Override
    public ServiceResponse<Dashboard> getDashboard(Long schoolId, Long dashboardId) {
        Dashboard d = dashboardPersistence.selectDashboard(schoolId, dashboardId);
        if(null == d) {
            return new ServiceResponse<>(
                    StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND,
                            new Object[]{DASHBOARD, dashboardId}));
        }
        return new ServiceResponse<>(d);
    }

    @Override
    public ServiceResponse<EntityId> createDashboard(Long schoolId, Dashboard dashboard) {
        dashboard.setSchoolId(schoolId);
        Long id = dashboardPersistence.insertDashboard(dashboard);
        return new ServiceResponse<>(new EntityId(id));
    }

    @Override
    public ServiceResponse<Void> replaceDashboard(Long schoolId, Long dashboardId, Dashboard dashboard) {
        Dashboard d = dashboardPersistence.selectDashboard(schoolId, dashboardId);
        if(null == d) {
            return new ServiceResponse<>(
                    StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND,
                            new Object[]{DASHBOARD, dashboardId}));
        }
        dashboard.setSchoolId(schoolId);
        dashboard.setId(dashboardId);
        dashboardPersistence.updateDashboard(schoolId, dashboardId, dashboard);
        return new ServiceResponse<>((Void) null);
    }

    @Override
    public ServiceResponse<Void> deleteDashboard(Long schoolId, Long dashboardId) {
        dashboardPersistence.deleteDashboard(dashboardId);
        return new ServiceResponse<>((Void) null);
    }
}
