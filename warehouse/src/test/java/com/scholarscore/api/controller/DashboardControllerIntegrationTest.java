package com.scholarscore.api.controller;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.School;
import com.scholarscore.models.dashboard.ColumnDef;
import com.scholarscore.models.dashboard.Dashboard;
import com.scholarscore.models.dashboard.DashboardRow;
import com.scholarscore.models.dashboard.Report;
import com.scholarscore.models.query.AggregateFunction;
import com.scholarscore.models.query.AggregateMeasure;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.DimensionField;
import com.scholarscore.models.query.Measure;
import com.scholarscore.models.query.MeasureField;
import com.scholarscore.models.query.Query;
import com.scholarscore.models.query.SubqueryColumnRef;
import com.scholarscore.models.query.dimension.StudentDimension;
import com.scholarscore.models.query.expressions.Expression;
import com.scholarscore.models.query.expressions.operands.MeasureOperand;
import com.scholarscore.models.query.expressions.operands.NumericPlaceholder;
import com.scholarscore.models.query.expressions.operands.PlaceholderOperand;
import com.scholarscore.models.query.expressions.operators.ComparisonOperator;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by markroper on 2/17/16.
 */
@Test(groups = { "integration" })
public class DashboardControllerIntegrationTest extends IntegrationBase {
    private int numberOfItemsCreated = 0;
    private School school;

    @BeforeClass
    public void init() {
        authenticate();
        numberOfItemsCreated = 0;
        school = new School();
        school.setName(localeServiceUtil.generateName());
        school = schoolValidatingExecutor.create(school, "Create base school");
    }

    @DataProvider
    public Object[][] createDashboardProvider() {
        Dashboard emptyDash = new Dashboard();
        emptyDash.setSchoolId(school.getId());

        Dashboard completeDash = new Dashboard();
        completeDash.setSchoolId(school.getId());
        completeDash.setUserId(1L);
        DashboardRow row1 = new DashboardRow();
        Query referralQuery = new Query();
        AggregateMeasure referralMeasure = new AggregateMeasure(Measure.REFERRAL, AggregateFunction.SUM);
        List<AggregateMeasure> referrals = new ArrayList<>();
        referrals.add(referralMeasure);
        referralQuery.setAggregateMeasures(referrals);
        referralQuery.addField(new DimensionField(Dimension.STUDENT, StudentDimension.ID));
        List<SubqueryColumnRef> referralWrappers = new ArrayList<>();
        referralWrappers.add(new SubqueryColumnRef(-1, AggregateFunction.COUNT));
        referralWrappers.add(new SubqueryColumnRef(1, null));
        referralQuery.setSubqueryColumnsByPosition(referralWrappers);
        Query referralClick = new Query();
        AggregateMeasure referralClickMeasure = new AggregateMeasure(Measure.REFERRAL, AggregateFunction.SUM);
        List<AggregateMeasure> referralsClick = new ArrayList<>();
        referralsClick.add(referralClickMeasure);
        referralClick.setAggregateMeasures(referralsClick);
        referralClick.addField(new DimensionField(Dimension.STUDENT, StudentDimension.ID));
        referralClick.addField(new DimensionField(Dimension.STUDENT, StudentDimension.NAME));
        MeasureOperand mo = new MeasureOperand(new MeasureField(Measure.REFERRAL, AggregateFunction.SUM.name()));
        PlaceholderOperand po = new NumericPlaceholder("${clickValue}");
        Expression referralClickExp = new Expression(mo, ComparisonOperator.EQUAL, po);
        referralClick.setHaving(referralClickExp);
        ColumnDef col1 = new ColumnDef("values[1]", "name");
        ColumnDef col2 = new ColumnDef("values[2]", "referrals");
        List<ColumnDef> defs = new ArrayList<ColumnDef>(){{add(col1); add(col2); }};
        Report referralReport = new Report();
        referralReport.setChartQuery(referralQuery);
        referralReport.setClickTableQuery(referralClick);
        referralReport.setColumnDefs(defs);

        row1.setReports(new ArrayList<Report>(){{ add(referralReport); }});
        completeDash.setRows(new ArrayList<DashboardRow>(){{ add(row1); }});


        return new Object[][] {
                { "empty dashboard", emptyDash },
                { "complete dashboard", completeDash }
        };
    }

    @Test(dataProvider = "createDashboardProvider")
    public void createDeleteDashboard(String msg, Dashboard dash) {
        Dashboard d = this.dashboardValidatingExecutor.create(school.getId(), dash, msg);
        this.dashboardValidatingExecutor.delete(school.getId(), d.getId(), msg);
    }
}
