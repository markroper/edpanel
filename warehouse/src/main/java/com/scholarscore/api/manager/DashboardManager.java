package com.scholarscore.api.manager;

import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.dashboard.Dashboard;

/**
 * Created by markroper on 2/16/16.
 */
public interface DashboardManager {
    ServiceResponse<Dashboard> getDashboard(Long schoolId);
    
    ServiceResponse<Dashboard> getDashboard(Long schoolId, Long dashboardId);
    
    ServiceResponse<EntityId> createDashboard(Long schoolId, Dashboard dashboard);

    ServiceResponse<Void> replaceDashboard(Long schoolId, Long dashboardId, Dashboard dashboard);

    ServiceResponse<Void> deleteDashboard(Long schoolId, Long dashboardId);
}
