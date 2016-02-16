package com.scholarscore.api.persistence;

import com.scholarscore.models.dashboard.Dashboard;

/**
 * Created by markroper on 2/16/16.
 */
public interface DashboardPersistence {
    Long insertDashboard(Dashboard dashboard);
    Dashboard selectDashboard(long schoolId, long dashboardId);
    Dashboard selectDashboardForUser(long schoolId, long userId);
    void deleteDashboard(long dashboardId);
    void updateDashboard(long schoolId, long dashboardId, Dashboard dash);
}
