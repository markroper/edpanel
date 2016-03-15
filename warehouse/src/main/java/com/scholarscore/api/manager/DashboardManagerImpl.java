package com.scholarscore.api.manager;

import com.scholarscore.api.persistence.DashboardPersistence;
import com.scholarscore.api.security.config.UserDetailsProxy;
import com.scholarscore.api.util.QueryPlaceholders;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.attendance.AttendanceStatus;
import com.scholarscore.models.attendance.AttendanceType;
import com.scholarscore.models.dashboard.ColumnDef;
import com.scholarscore.models.dashboard.Dashboard;
import com.scholarscore.models.dashboard.DashboardRow;
import com.scholarscore.models.dashboard.Report;
import com.scholarscore.models.dashboard.ReportType;
import com.scholarscore.models.query.AggregateFunction;
import com.scholarscore.models.query.AggregateMeasure;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.DimensionField;
import com.scholarscore.models.query.Measure;
import com.scholarscore.models.query.MeasureField;
import com.scholarscore.models.query.Query;
import com.scholarscore.models.query.SubqueryColumnRef;
import com.scholarscore.models.query.bucket.AggregationBucket;
import com.scholarscore.models.query.bucket.NumericBucket;
import com.scholarscore.models.query.dimension.GoalDimension;
import com.scholarscore.models.query.dimension.SchoolDimension;
import com.scholarscore.models.query.dimension.SectionDimension;
import com.scholarscore.models.query.dimension.StudentDimension;
import com.scholarscore.models.query.dimension.TeacherDimension;
import com.scholarscore.models.query.expressions.Expression;
import com.scholarscore.models.query.expressions.operands.DatePlaceholder;
import com.scholarscore.models.query.expressions.operands.DimensionOperand;
import com.scholarscore.models.query.expressions.operands.MeasureOperand;
import com.scholarscore.models.query.expressions.operands.NumericOperand;
import com.scholarscore.models.query.expressions.operands.NumericPlaceholder;
import com.scholarscore.models.query.expressions.operands.StringOperand;
import com.scholarscore.models.query.expressions.operators.BinaryOperator;
import com.scholarscore.models.query.expressions.operators.ComparisonOperator;
import com.scholarscore.models.query.measure.AttendanceMeasure;
import com.scholarscore.models.query.measure.CurrentGpaMeasure;
import com.scholarscore.models.query.measure.behavior.BehaviorMeasure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

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
            Dashboard newD = new Dashboard(DEFAULT_DASH);
            return new ServiceResponse<>(newD);
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

    private static final Dashboard DEFAULT_DASH = new Dashboard();
    {
        DashboardRow row1 = new DashboardRow();
        DashboardRow row2 = new DashboardRow();
        DashboardRow row3 = new DashboardRow();
        DashboardRow row4 = new DashboardRow();
        DEFAULT_DASH.setRows(new ArrayList<>());
        DEFAULT_DASH.getRows().add(row1);
        DEFAULT_DASH.getRows().add(row2);
        DEFAULT_DASH.getRows().add(row3);
        DEFAULT_DASH.getRows().add(row4);

        //Fill up row 1 with 3 reports
        Report gpa = new Report();
        gpa.setType(ReportType.BAR);
        gpa.setSupportDemographicFilter(true);
        gpa.setName("Students by GPA Range");
        Query gpaBucketQuery = new Query();
        ArrayList<AggregateMeasure> gpaMeasures = new ArrayList<>();
        gpaBucketQuery.setAggregateMeasures(gpaMeasures);
        AggregateMeasure gpaMeasure = new AggregateMeasure(Measure.CURRENT_GPA, AggregateFunction.COUNT);
        List<AggregationBucket> buckets = new ArrayList<>();
        buckets.add(new NumericBucket(0D, 1D, "0 - 1"));
        buckets.add(new NumericBucket(1D, 2D, "1 - 2"));
        buckets.add(new NumericBucket(2D, 2.5D, "2 - 2.5"));
        buckets.add(new NumericBucket(2.5D, 3D, "2.5 - 3"));
        buckets.add(new NumericBucket(3D, 3.5D, "3 - 3.5"));
        buckets.add(new NumericBucket(3.5D, 4D, "3.5 - 4"));
        buckets.add(new NumericBucket(4D, null, "4+"));
        gpaMeasure.setBuckets(buckets);
        gpaMeasures.add(gpaMeasure);
        Expression studentClause = new Expression(
                new DimensionOperand(new DimensionField(Dimension.STUDENT, StudentDimension.ID)),
                ComparisonOperator.GREATER_THAN,
                new NumericOperand(0L)
        );
        Expression whereClause = new Expression(
                new DimensionOperand(new DimensionField(Dimension.SCHOOL, SchoolDimension.ID)),
                ComparisonOperator.EQUAL,
                new NumericPlaceholder("${schoolId}"));
        gpaBucketQuery.setFilter(new Expression(studentClause, BinaryOperator.AND, whereClause));
        gpa.setChartQuery(gpaBucketQuery);
        Query gpaClick = new Query();
        AggregateMeasure gpaMeasureSum = new AggregateMeasure(Measure.CURRENT_GPA, AggregateFunction.SUM);
        List<AggregateMeasure> ms = new ArrayList<>();
        ms.add(gpaMeasureSum);
        gpaClick.setAggregateMeasures(ms);
        gpaClick.addField(new DimensionField(Dimension.STUDENT, StudentDimension.ID));
        gpaClick.addField(new DimensionField(Dimension.STUDENT, StudentDimension.NAME));
        Expression clickWhereMin = new Expression(
                new MeasureOperand(new MeasureField(Measure.CURRENT_GPA, CurrentGpaMeasure.GPA)),
                ComparisonOperator.GREATER_THAN_OR_EQUAL,
                new NumericPlaceholder(QueryPlaceholders.CLICK_VALUE_MIN)
        );
        Expression clickWhereMax = new Expression(
                new MeasureOperand(new MeasureField(Measure.CURRENT_GPA, CurrentGpaMeasure.GPA)),
                ComparisonOperator.LESS_THAN,
                new NumericPlaceholder(QueryPlaceholders.CLICK_VALUE_MAX)
        );
        Expression clickWhere = new Expression(clickWhereMin, BinaryOperator.AND, clickWhereMax);
        gpaClick.setFilter(new Expression(whereClause, BinaryOperator.AND, clickWhere));
        gpa.setClickTableQuery(gpaClick);
        List<ColumnDef> gpaDefs = new ArrayList<>();
        gpaDefs.add(new ColumnDef("values[1]", "Name"));
        gpaDefs.add(new ColumnDef("values[2]", "GPA"));
        gpa.setColumnDefs(gpaDefs);

        row1.setReports(new ArrayList<>());
        row1.getReports().add(gpa);
        //ATTENDANCE QUERY
        Report attendance = new Report();
        attendance.setType(ReportType.BAR);
        attendance.setSupportDemographicFilter(true);
        attendance.setSupportDateFilter(true);
        Query attendanceQ = new Query();
        attendance.setName("Students by Absences");
        ArrayList<AggregateMeasure> attendanceMeasures = new ArrayList<>();
        attendanceQ.setAggregateMeasures(attendanceMeasures);
        AggregateMeasure attendanceMeasure = new AggregateMeasure(Measure.ATTENDANCE, AggregateFunction.COUNT);
        attendanceMeasures.add(attendanceMeasure);
        attendanceQ.addField(new DimensionField(Dimension.STUDENT, StudentDimension.ID));
        List<SubqueryColumnRef> attWrappers = new ArrayList<>();
        attWrappers.add(new SubqueryColumnRef(1, null));
        attWrappers.add(new SubqueryColumnRef(-1, AggregateFunction.COUNT));
        attendanceQ.setSubqueryColumnsByPosition(attWrappers);
        Expression attType = new Expression(
                new MeasureOperand(new MeasureField(Measure.ATTENDANCE, AttendanceMeasure.TYPE)),
                ComparisonOperator.EQUAL,
                new StringOperand(AttendanceType.DAILY.name())
        );
        Expression attStatus = new Expression(
                new MeasureOperand(new MeasureField(Measure.ATTENDANCE, AttendanceMeasure.STATUS)),
                ComparisonOperator.EQUAL,
                new StringOperand(AttendanceStatus.ABSENT.name())
        );
        Expression dateMin = new Expression(
                new MeasureOperand(new MeasureField(Measure.ATTENDANCE, AttendanceMeasure.DATE)),
                ComparisonOperator.GREATER_THAN_OR_EQUAL,
                new DatePlaceholder(QueryPlaceholders.START_DATE)
        );
        Expression dateMax = new Expression(
                new MeasureOperand(new MeasureField(Measure.ATTENDANCE, AttendanceMeasure.DATE)),
                ComparisonOperator.LESS_THAN_OR_EQUAL,
                new DatePlaceholder(QueryPlaceholders.END_DATE)
        );
        Expression dateExp = new Expression(dateMin, BinaryOperator.AND, dateMax);
        Expression dateAndAtt = new Expression(dateExp, BinaryOperator.AND, attType);
        Expression dateAndAttAndType = new Expression(dateAndAtt, BinaryOperator.AND, attStatus);
        Expression attendanceFilter = new Expression(whereClause, BinaryOperator.AND, dateAndAttAndType);
        attendanceQ.setFilter(attendanceFilter);
        attendance.setChartQuery(attendanceQ);
        Query attendanceClick = new Query();
        attendanceClick.setAggregateMeasures(attendanceMeasures);
        attendanceClick.addField(new DimensionField(Dimension.STUDENT, StudentDimension.ID));
        attendanceClick.addField(new DimensionField(Dimension.STUDENT, StudentDimension.NAME));
        attendanceClick.setFilter(attendanceFilter);
        Expression attHaving = new Expression(
                new MeasureOperand(new MeasureField(Measure.ATTENDANCE, AggregateFunction.SUM.name())),
                ComparisonOperator.EQUAL,
                new NumericPlaceholder(QueryPlaceholders.CLICK_VALUE)
        );
        attendanceClick.setHaving(attHaving);
        attendance.setClickTableQuery(attendanceClick);
        List<ColumnDef> attDefs = new ArrayList<>();
        attDefs.add(new ColumnDef("values[1]", "Name"));
        attDefs.add(new ColumnDef("values[2]", "Absences"));
        attendance.setColumnDefs(attDefs);
        row1.getReports().add(attendance);

        //FAILING COURSES QUERY
        Report failingClasses = new Report();
        failingClasses.setType(ReportType.BAR);
        failingClasses.setSupportDateFilter(false);
        failingClasses.setSupportDemographicFilter(true);
        Query failingQ = new Query();
        failingClasses.setName("Count of Students Failing Classes");
        ArrayList<AggregateMeasure> failingMeasures = new ArrayList<>();
        failingQ.setAggregateMeasures(failingMeasures);
        AggregateMeasure failingMeasure = new AggregateMeasure(Measure.COURSE_GRADE, AggregateFunction.SUM);
        List<AggregationBucket> failingBuckets = new ArrayList<>();
        failingBuckets.add(new NumericBucket(0D, 70D, "1"));
        failingBuckets.add(new NumericBucket(70D, null, "0"));
        failingMeasure.setBuckets(failingBuckets);
        failingMeasure.setBucketAggregation(AggregateFunction.SUM);
        failingMeasures.add(failingMeasure);
        failingQ.addField(new DimensionField(Dimension.STUDENT, StudentDimension.ID));
        List<SubqueryColumnRef> failingWrappers = new ArrayList<>();
        failingWrappers.add(new SubqueryColumnRef(2, null));
        failingWrappers.add(new SubqueryColumnRef(-1, AggregateFunction.COUNT));
        failingQ.setSubqueryColumnsByPosition(failingWrappers);
        Expression startDate = new Expression(
                new DimensionOperand(new DimensionField(Dimension.SECTION, SectionDimension.START_DATE)),
                ComparisonOperator.GREATER_THAN_OR_EQUAL,
                new DatePlaceholder(QueryPlaceholders.START_DATE)
        );
        Expression endDate = new Expression(
                new DimensionOperand(new DimensionField(Dimension.SECTION, SectionDimension.START_DATE)),
                ComparisonOperator.LESS_THAN_OR_EQUAL,
                new DatePlaceholder(QueryPlaceholders.END_DATE)
        );
        Expression dateRange = new Expression(startDate, BinaryOperator.AND, endDate);
        failingQ.setFilter(new Expression(whereClause, BinaryOperator.AND, dateRange));
        failingClasses.setChartQuery(failingQ);
        row1.getReports().add(failingClasses);

        Query failingClick = new Query();
        failingClick.setAggregateMeasures(failingMeasures);
        failingClick.addField(new DimensionField(Dimension.STUDENT, StudentDimension.ID));
        failingClick.addField(new DimensionField(Dimension.STUDENT, StudentDimension.NAME));
        MeasureField mf = new MeasureField(Measure.COURSE_GRADE, null);
        mf.setBucketAggregation(AggregateFunction.SUM);
        mf.setBuckets(failingBuckets);
        Expression failingHaving = new Expression(
                new MeasureOperand(mf),
                ComparisonOperator.EQUAL,
                new NumericPlaceholder(QueryPlaceholders.CLICK_VALUE));
        failingClick.setFilter(new Expression(whereClause, BinaryOperator.AND, dateRange));
        failingClick.setHaving(failingHaving);
        List<ColumnDef> failingDefs = new ArrayList<>();
        failingDefs.add(new ColumnDef("values[1]", "Name"));
        failingDefs.add(new ColumnDef("values[2]", "Failing Classes"));
        failingClasses.setColumnDefs(failingDefs);
        failingClasses.setClickTableQuery(failingClick);

        //Fill up row 2 with 1 report
        Report meritDemerit = new Report();
        meritDemerit.setType(ReportType.BAR);
        meritDemerit.setName("Demerit & Merit Counts by Staff");
        Query meritDemeritQ = new Query();
        AggregateMeasure meritMeasure = new AggregateMeasure(Measure.MERIT, AggregateFunction.SUM);
        AggregateMeasure demeritMeasure = new AggregateMeasure(Measure.DEMERIT, AggregateFunction.SUM);
        List<AggregateMeasure> meritDemerits = new ArrayList<>();
        meritDemerits.add(meritMeasure);
        meritDemerits.add(demeritMeasure);
        meritDemeritQ.setAggregateMeasures(meritDemerits);
        meritDemeritQ.addField(new DimensionField(Dimension.STAFF, TeacherDimension.NAME));
        meritDemeritQ.setFilter(whereClause);
        row2.setReports(new ArrayList<>());
        meritDemerit.setChartQuery(meritDemeritQ);
        row2.getReports().add(meritDemerit);

        //Row 3 with one report
        Report ref = new Report();
        ref.setType(ReportType.BAR);
        ref.setSupportDemographicFilter(true);
        ref.setSupportDateFilter(true);
        ref.setName("Number of Students by Referral Count");
        Query referralQuery = new Query();
        AggregateMeasure referralMeasure = new AggregateMeasure(Measure.REFERRAL, AggregateFunction.SUM);
        List<AggregateMeasure> referrals = new ArrayList<>();
        referrals.add(referralMeasure);
        referralQuery.setAggregateMeasures(referrals);
        referralQuery.addField(new DimensionField(Dimension.STUDENT, StudentDimension.ID));
        List<SubqueryColumnRef> referralWrappers = new ArrayList<>();
        referralWrappers.add(new SubqueryColumnRef(1, null));
        referralWrappers.add(new SubqueryColumnRef(-1, AggregateFunction.COUNT));
        referralQuery.setSubqueryColumnsByPosition(referralWrappers);
        Expression refStart = new Expression(
                new MeasureOperand(new MeasureField(Measure.REFERRAL, BehaviorMeasure.DATE)),
                ComparisonOperator.GREATER_THAN_OR_EQUAL,
                new DatePlaceholder(QueryPlaceholders.START_DATE)
        );
        Expression refEnd = new Expression(
                new MeasureOperand(new MeasureField(Measure.REFERRAL, BehaviorMeasure.DATE)),
                ComparisonOperator.LESS_THAN_OR_EQUAL,
                new DatePlaceholder(QueryPlaceholders.END_DATE)
        );
        Expression refRange = new Expression(refStart, BinaryOperator.AND, refEnd);
        Expression refExp = new Expression(whereClause, BinaryOperator.AND, refRange);
        referralQuery.setFilter(refExp);
        ref.setChartQuery(referralQuery);

        Query refClick = new Query();
        refClick.setAggregateMeasures(referrals);
        refClick.addField(new DimensionField(Dimension.STUDENT, StudentDimension.ID));
        refClick.addField(new DimensionField(Dimension.STUDENT, StudentDimension.NAME));
        Expression having = new Expression(
                new MeasureOperand(new MeasureField(Measure.REFERRAL, AggregateFunction.SUM.name())),
                ComparisonOperator.EQUAL,
                new NumericPlaceholder(QueryPlaceholders.CLICK_VALUE));
        refClick.setFilter(refExp);
        refClick.setHaving(having);
        ref.setClickTableQuery(refClick);

        List<ColumnDef> refDefs = new ArrayList<>();
        refDefs.add(new ColumnDef("values[1]", "Name"));
        refDefs.add(new ColumnDef("values[2]", "Referrals"));
        ref.setColumnDefs(refDefs);
        row3.setReports(new ArrayList<>());
        row3.getReports().add(ref);

        //Goal
        Report goal = new Report();
        goal.setType(ReportType.SPLINE);
        goal.setSupportDemographicFilter(false);
        goal.setSupportDateFilter(true);
        goal.setName("Goal Status by Week");
        Query goalQuery = new Query();
        AggregateMeasure goalMeasure = new AggregateMeasure(Measure.GOAL, AggregateFunction.COUNT);
        List<AggregateMeasure> goals = new ArrayList<>();
        goals.add(goalMeasure);
        goalQuery.setAggregateMeasures(goals);
        DimensionField df = new DimensionField(Dimension.GOAL, GoalDimension.START_DATE);
        df.setBucketAggregation(AggregateFunction.YEARWEEK);
        goalQuery.addField(df);
        goalQuery.addField(new DimensionField(Dimension.GOAL, GoalDimension.PROGRESS));
        Expression goalDateMin = new Expression(
                new MeasureOperand(new MeasureField(Measure.GOAL, GoalDimension.START_DATE)),
                ComparisonOperator.GREATER_THAN_OR_EQUAL,
                new DatePlaceholder(QueryPlaceholders.START_DATE)
        );
        Expression goalDateMax = new Expression(
                new MeasureOperand(new MeasureField(Measure.GOAL, GoalDimension.START_DATE)),
                ComparisonOperator.LESS_THAN_OR_EQUAL,
                new DatePlaceholder(QueryPlaceholders.END_DATE)
        );
        goalQuery.setFilter(new Expression(goalDateMin, BinaryOperator.AND, goalDateMax));
        goal.setChartQuery(goalQuery);
        row4.setReports(new ArrayList<>());
        row4.getReports().add(goal);
    }
}
