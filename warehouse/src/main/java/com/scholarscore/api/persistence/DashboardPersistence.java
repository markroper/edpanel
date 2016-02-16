package com.scholarscore.api.persistence;

import com.scholarscore.models.dashboard.Dashboard;

/**
 * Created by markroper on 2/16/16.
 */
public interface DashboardPersistence {
    Long insertDashboard(Dashboard dashboard);
    Dashboard selectDashboard(Long schoolId, Long dashboardId);
    Dashboard selectDashboardForUser(Long schoolId, Long userId);
    void deleteDashboard(Long dashboardId);
    void updateDashboard(Long schoolId, Long dashboardId, Dashboard dash);
}
