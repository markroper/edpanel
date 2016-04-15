package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.DashboardPersistence;
import com.scholarscore.models.dashboard.Dashboard;
import com.scholarscore.models.dashboard.DashboardRow;
import com.scholarscore.models.dashboard.ReportBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by markroper on 2/16/16.
 */
@Transactional
public class DashboardJdbc extends BaseJdbc implements DashboardPersistence {
    public static final String DASH_HQL = "from dashboard d " +
            "join fetch d.rows r " +
            "join fetch r.reports rs " +
            "join fetch rs.chartQuery " +
            "left join fetch rs.clickTableQuery";
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
        List<DashboardRow> rows = dashboard.getRows();
        dashboard.setRows(null);
        Dashboard d =  hibernateTemplate.merge(dashboard);
        if(null != rows) {
            long rowIndex = 0;
            for(DashboardRow row: rows) {
                row.setDashboardFk(d.getId());
                row.setPosition(rowIndex);
                List<ReportBase> reports = new ArrayList<>();
                if(null != row.getReports()) {
                    reports = row.getReports();
                    row.setReports(null);
                }
                DashboardRow createdRow = hibernateTemplate.merge(row);
                long reportIndex = 0;
                for(ReportBase rpt: reports) {
                    rpt.setPosition(reportIndex);
                    rpt.setRowFk(createdRow.getId());
                    hibernateTemplate.merge(rpt);
                    reportIndex++;
                }
                rowIndex++;
            }
        }
        return d.getId();
    }

    @Override
    public Dashboard selectDashboard(Long schoolId, Long dashboardId) {
        return hibernateTemplate.get(Dashboard.class, dashboardId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Dashboard selectDashboardForUser(Long schoolId, Long userId) {
        String[] params = new String[]{ "schoolId" };
        Object[] paramVals = new Object[]{ schoolId };
        String queryString = " where d.schoolId = :schoolId";
        if(null != userId){
            params = new String[]{ "userId", "schoolId" };
            paramVals = new Object[]{ userId, schoolId };
            queryString =  " where d.userId = :userId and d.schoolId = :schoolId";
        }
        List<Dashboard> dashes = (List<Dashboard>)hibernateTemplate.findByNamedParam(
                DASH_HQL + queryString,
                params,
                paramVals);
        if(null == dashes || dashes.isEmpty()) {
            return null;
        }
        return dashes.get(0);
    }

    @Override
    public void deleteDashboard(Long dashboardId) {
        Map<String, Object> params = new HashMap<>();
        params.put("dashboardId", dashboardId);
        jdbcTemplate.update(
                "DELETE FROM `dashboard` WHERE `dashboard_id` = :dashboardId",
                new MapSqlParameterSource(params));
    }

    @Override
    public void deleteDashboardRows(Long dashboardId) {
        Map<String, Object> params = new HashMap<>();
        params.put("dashboardId", dashboardId);
        jdbcTemplate.update(
                "DELETE FROM `dashboard_row` WHERE `dashboard_fk` = :dashboardId",
                new MapSqlParameterSource(params));
    }

    @Override
    public void updateDashboard(Long schoolId, Long dashboardId, Dashboard dash) {
        dash.setId(dashboardId);
        dash.setSchoolId(schoolId);
        Map<Integer, List<ReportBase>> reportsToCreate = new HashMap<>();
        if(null != dash.getRows()) {
            long rowPos = 0;
            for(DashboardRow row : dash.getRows()) {
                row.setDashboardFk(dashboardId);
                row.setPosition(rowPos);
                if(null != row.getReports()) {
                    if(null != row.getId()) {
                        long rptPos = 0;
                        for(ReportBase rpt: row.getReports()) {
                            rpt.setRowFk(row.getId());
                            rpt.setPosition(rptPos);
                            rptPos++;
                        }
                    } else {
                        //Handle this
                        reportsToCreate.put((int)rowPos, row.getReports());
                        row.setReports(null);
                    }
                }
                rowPos++;
            }
        }
        hibernateTemplate.merge(dash);
        Dashboard d = selectDashboard(schoolId, dashboardId);
        for(Map.Entry<Integer, List<ReportBase>> entry: reportsToCreate.entrySet()) {
            DashboardRow row = d.getRows().get(entry.getKey());
            long rptPos = 0;
            for(ReportBase rpt: entry.getValue()) {
                rpt.setRowFk(row.getId());
                rpt.setPosition(rptPos);
                hibernateTemplate.merge(rpt);
                rptPos++;
            }
        }
    }
}
