package com.scholarscore.api.controller.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.dashboard.Dashboard;
import com.scholarscore.models.dashboard.Report;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.testng.Assert;

import java.util.ArrayList;

/**
 * Created by markroper on 2/17/16.
 */
public class DashboardValidatingExecutor {
    private final IntegrationBase serviceBase;

    public DashboardValidatingExecutor(IntegrationBase sb) {
        this.serviceBase = sb;
    }

    public Dashboard get(Long schoolId, Long dashboardId, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getDashboardEndpoint(schoolId, dashboardId),
                null);
        Dashboard dashboard = serviceBase.validateResponse(response, new TypeReference<Dashboard>(){});
        Assert.assertNotNull(dashboard, "Unexpected null dashboard returned for case: " + msg);

        return dashboard;
    }
    public void getNegative(Long schoolId, Long dashboardId, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getDashboardEndpoint(schoolId, dashboardId),
                null);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected status code returned while retrieving dashboard: " + msg);
    }

    public Dashboard create(Long schoolId, Dashboard dashboard, String msg) {
        //Create the dashboard
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getDashboardEndpoint(schoolId), null, dashboard);
        EntityId returnedDashboardId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(returnedDashboardId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedDashboard(schoolId, returnedDashboardId.getId(), dashboard, HttpMethod.POST, msg);
    }

    public void createNegative(Long schoolId, Dashboard dashboard, HttpStatus expectedCode, String msg) {
        //Attempt to create the dashboard
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getDashboardEndpoint(schoolId), null, dashboard);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected status code returned for case: " + msg);
    }
    public void delete(Long schoolId, Long dashboardId, String msg) {
        //Delete the dashboard
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE,
                serviceBase.getDashboardEndpoint(schoolId, dashboardId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), HttpStatus.OK.value(),
                "Non 200 HttpStatus returned on delete for case: " + msg);
        getNegative(schoolId, dashboardId, HttpStatus.NOT_FOUND, msg);
    }

    public void deleteNegative(Long schoolId, Long dashboardId, HttpStatus expectedCode, String msg) {
        //Attempt to delete the dashboard
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE,
                serviceBase.getDashboardEndpoint(schoolId, dashboardId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for DELETE: " + msg);
    }

    public Dashboard replace(Long schoolId, Long dashboardId, Dashboard dashboard, String msg) {
        //Create the dashboard
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT,
                serviceBase.getDashboardEndpoint(schoolId, dashboardId),
                null,
                dashboard);
        EntityId returnedDashboardId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(returnedDashboardId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedDashboard(schoolId, dashboardId, dashboard, HttpMethod.PUT, msg);
    }

    public void replaceNegative(Long schoolId, Long dashboardId, Dashboard dashboard, HttpStatus expectedCode, String msg) {
        //Create the dashboard
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT,
                serviceBase.getDashboardEndpoint(schoolId, dashboardId),
                null,
                dashboard);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for PUT: " + msg);
    }

    public IntegrationBase getServiceBase() {
        return serviceBase;
    }

    /**
     * Given an ID and the value submitted to a GET/PUT endpoint, retrieve the dashboard via GET, validate it, and
     * return it to the caller.
     *
     * @param dashboardId
     * @param submittedDashboard
     * @param msg
     * @return
     */
    protected Dashboard retrieveAndValidateCreatedDashboard( Long schoolId, Long dashboardId, Dashboard submittedDashboard, HttpMethod method, String msg) {
        //Retrieve and validate the created dashboard
        Dashboard createdDashboard = this.get(schoolId, dashboardId, msg);
        Dashboard expectedDashboard = generateExpectationDashboard(submittedDashboard, createdDashboard, method);
        Assert.assertEquals(createdDashboard, expectedDashboard, "Unexpected dashboard created for case: " + msg);

        return createdDashboard;
    }

    /**
     * Given a submitted dashboard object and an dashboard instance returned by the API after creation,
     * this method returns a new Dashboard instance that represents the expected state of the submitted
     * Dashboard after creation.  The reason that there are differences in the submitted and expected
     * instances is that there may be system assigned values not in the initially submitted object, for
     * example, the id property.
     *
     * @param submitted
     * @param created
     * @return
     */
    protected Dashboard generateExpectationDashboard(Dashboard submitted, Dashboard created, HttpMethod method) {
        Dashboard returnDashboard = new Dashboard(submitted);

        if(method == HttpMethod.PATCH) {
            returnDashboard.mergePropertiesIfNull(created);
        } else if(null != returnDashboard && null == returnDashboard.getId()) {
            returnDashboard.setId(created.getId());
            if(null != returnDashboard.getRows()) {
                for (int i = 0; i < returnDashboard.getRows().size(); i++) {
                    returnDashboard.getRows().get(i).setDashboardFk(created.getId());
                    returnDashboard.getRows().get(i).setId(created.getRows().get(i).getId());
                    if(null != returnDashboard.getRows().get(i).getReports()) {
                        for(int j = 0; j < returnDashboard.getRows().get(i).getReports().size(); j++) {
                            Report rpt = returnDashboard.getRows().get(i).getReports().get(j);
                            rpt.setRowFk(created.getRows().get(i).getId());
                            rpt.setId(created.getRows().get(i).getReports().get(j).getId());
                            rpt.getChartQuery().setId(created.getRows().get(i).getReports().get(j).getChartQuery().getId());
                            if(null != rpt.getClickTableQuery()) {
                                rpt.getClickTableQuery().setId(created.getRows().get(i).getReports().get(j).getClickTableQuery().getId());
                            }
                        }
                    }
                }
            }
        }
        if(null == returnDashboard.getRows()) {
            returnDashboard.setRows(new ArrayList<>());
        }

        return returnDashboard;
    }

}
