package com.scholarscore.api.manager;

import com.scholarscore.api.persistence.DashboardPersistence;
import com.scholarscore.api.security.config.UserDetailsProxy;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.attendance.AttendanceTypes;
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
import com.scholarscore.models.query.bucket.AggregationBucket;
import com.scholarscore.models.query.bucket.NumericBucket;
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
import com.scholarscore.models.query.measure.CourseGradeMeasure;
import com.scholarscore.models.query.measure.CurrentGpaMeasure;
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
        DEFAULT_DASH.setRows(new ArrayList<>());
        DEFAULT_DASH.getRows().add(row1);
        DEFAULT_DASH.getRows().add(row2);
        DEFAULT_DASH.getRows().add(row3);

        //Fill up row 1 with 3 reports
        Report gpa = new Report();
        gpa.setName("Students by GPA Range");
        Query gpaBucketQuery = new Query();
        ArrayList<AggregateMeasure> gpaMeasures = new ArrayList<>();
        gpaBucketQuery.setAggregateMeasures(gpaMeasures);
        AggregateMeasure gpaMeasure = new AggregateMeasure(Measure.CURRENT_GPA, AggregateFunction.COUNT);
        List<AggregationBucket> buckets = new ArrayList<>();
        buckets.add(new NumericBucket(0D, 1D, "0-1"));
        buckets.add(new NumericBucket(1D, 2D, "1-2"));
        buckets.add(new NumericBucket(2D, 2.5D, "2-2.5"));
        buckets.add(new NumericBucket(2.5D, 3D, "2.5-3"));
        buckets.add(new NumericBucket(3D, 3.5D, "3-3.5"));
        buckets.add(new NumericBucket(3.5D, 4D, "3.5-4"));
        buckets.add(new NumericBucket(4D, null, "4+"));
        gpaMeasure.setBuckets(buckets);
        gpaMeasures.add(gpaMeasure);
        Expression studentClause = new Expression(
                new NumericOperand(0L),
                ComparisonOperator.LESS_THAN,
                new DimensionOperand(new DimensionField(Dimension.STUDENT, StudentDimension.ID))
        );
        Expression whereClause = new Expression(
                new NumericPlaceholder("${schoolId}"),
                ComparisonOperator.EQUAL,
                new DimensionOperand(new DimensionField(Dimension.SCHOOL, SchoolDimension.ID)));
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
                new NumericPlaceholder("${clickValueMin}"),
                ComparisonOperator.LESS_THAN_OR_EQUAL,
                new MeasureOperand(new MeasureField(Measure.CURRENT_GPA, CurrentGpaMeasure.GPA))
        );
        Expression clickWhereMax = new Expression(
                new NumericPlaceholder("${clickValueMax}"),
                ComparisonOperator.GREATER_THAN,
                new MeasureOperand(new MeasureField(Measure.CURRENT_GPA, CurrentGpaMeasure.GPA))
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
        Query attendanceQ = new Query();
        attendance.setName("Students by Absences");
        ArrayList<AggregateMeasure> attendanceMeasures = new ArrayList<>();
        attendanceQ.setAggregateMeasures(attendanceMeasures);
        AggregateMeasure attendanceMeasure = new AggregateMeasure(Measure.ATTENDANCE, AggregateFunction.COUNT);
        attendanceMeasures.add(attendanceMeasure);
        attendanceQ.addField(new DimensionField(Dimension.STUDENT, StudentDimension.ID));
        List<SubqueryColumnRef> attWrappers = new ArrayList<>();
        attWrappers.add(new SubqueryColumnRef(-1, AggregateFunction.COUNT));
        attWrappers.add(new SubqueryColumnRef(1, null));
        attendanceQ.setSubqueryColumnsByPosition(attWrappers);
        Expression attType = new Expression(
                new MeasureOperand(new MeasureField(Measure.ATTENDANCE, AttendanceMeasure.TYPE)),
                ComparisonOperator.EQUAL,
                new StringOperand(AttendanceTypes.DAILY.name())
        );
        attendanceQ.setFilter(new Expression(whereClause, BinaryOperator.AND, attType));
        attendance.setChartQuery(attendanceQ);
        Query attendanceClick = new Query();
        attendanceClick.setAggregateMeasures(attendanceMeasures);
        attendanceClick.addField(new DimensionField(Dimension.STUDENT, StudentDimension.ID));
        attendanceClick.addField(new DimensionField(Dimension.STUDENT, StudentDimension.NAME));
        attendanceClick.setFilter(new Expression(whereClause, BinaryOperator.AND, attType));
        Expression attHaving = new Expression(
                new MeasureOperand(new MeasureField(Measure.ATTENDANCE, AggregateFunction.SUM.name())),
                ComparisonOperator.EQUAL,
                new NumericPlaceholder("${clickValue}")
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
        Query failingQ = new Query();
        failingClasses.setName("Count of Students Failing Classes");
        ArrayList<AggregateMeasure> failingMeasures = new ArrayList<>();
        failingQ.setAggregateMeasures(failingMeasures);
        AggregateMeasure failingMeasure = new AggregateMeasure(Measure.COURSE_GRADE, AggregateFunction.COUNT);
        failingMeasures.add(failingMeasure);
        failingQ.addField(new DimensionField(Dimension.STUDENT, StudentDimension.ID));
        List<SubqueryColumnRef> failingWrappers = new ArrayList<>();
        failingWrappers.add(new SubqueryColumnRef(-1, AggregateFunction.COUNT));
        failingWrappers.add(new SubqueryColumnRef(1, null));
        failingQ.setSubqueryColumnsByPosition(failingWrappers);
        Expression sc = new Expression(
                new MeasureOperand(new MeasureField(Measure.COURSE_GRADE, CourseGradeMeasure.GRADE)),
                ComparisonOperator.LESS_THAN_OR_EQUAL,
                new NumericOperand(70D)
        );
        Expression startDate = new Expression(
                new DimensionOperand(new DimensionField(Dimension.SECTION, SectionDimension.START_DATE)),
                ComparisonOperator.GREATER_THAN_OR_EQUAL,
                new DatePlaceholder("${startDate}")
        );
        Expression endDate = new Expression(
                new DimensionOperand(new DimensionField(Dimension.SECTION, SectionDimension.START_DATE)),
                ComparisonOperator.LESS_THAN_OR_EQUAL,
                new DatePlaceholder("${endDate}")
        );
        Expression dateRange = new Expression(startDate, BinaryOperator.AND, endDate);
        Expression scoreAndDate = new Expression(dateRange, BinaryOperator.AND, sc);
        failingQ.setFilter(new Expression(whereClause, BinaryOperator.AND, scoreAndDate));
        failingClasses.setChartQuery(failingQ);
        row1.getReports().add(failingClasses);

        Query failingClick = new Query();
        failingClick.setAggregateMeasures(failingMeasures);
        failingClick.addField(new DimensionField(Dimension.STUDENT, StudentDimension.ID));
        failingClick.addField(new DimensionField(Dimension.STUDENT, StudentDimension.NAME));
        Expression failingHaving = new Expression(
                new MeasureOperand(new MeasureField(Measure.COURSE_GRADE, AggregateFunction.COUNT.name())),
                ComparisonOperator.EQUAL,
                new NumericPlaceholder("${clickValue}"));
        failingClick.setFilter(new Expression(whereClause, BinaryOperator.AND, scoreAndDate));
        failingClick.setHaving(failingHaving);
        List<ColumnDef> failingDefs = new ArrayList<>();
        failingDefs.add(new ColumnDef("values[1]", "Name"));
        failingDefs.add(new ColumnDef("values[2]", "Failing Classes"));
        failingClasses.setColumnDefs(failingDefs);
        failingClasses.setClickTableQuery(failingClick);

        //Fill up row 2 with 1 report
        Report meritDemerit = new Report();
        meritDemerit.setName("Demerit & Merit Counts by Staff");
        Query meritDemeritQ = new Query();
        AggregateMeasure meritMeasure = new AggregateMeasure(Measure.MERIT, AggregateFunction.SUM);
        AggregateMeasure demeritMeasure = new AggregateMeasure(Measure.DEMERIT, AggregateFunction.SUM);
        List<AggregateMeasure> meritDemerits = new ArrayList<>();
        meritDemerits.add(meritMeasure);
        meritDemerits.add(demeritMeasure);
        meritDemeritQ.setAggregateMeasures(meritDemerits);
        meritDemeritQ.addField(new DimensionField(Dimension.TEACHER, TeacherDimension.NAME));
        meritDemeritQ.setFilter(whereClause);
        row2.setReports(new ArrayList<>());
        meritDemerit.setChartQuery(meritDemeritQ);
        row2.getReports().add(meritDemerit);

        //Row 3 with one report
        Report ref = new Report();
        ref.setName("Number of Students by Referral Count");
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
        referralQuery.setFilter(whereClause);
        ref.setChartQuery(referralQuery);

        Query refClick = new Query();
        refClick.setAggregateMeasures(referrals);
        refClick.addField(new DimensionField(Dimension.STUDENT, StudentDimension.ID));
        refClick.addField(new DimensionField(Dimension.STUDENT, StudentDimension.NAME));
        Expression having = new Expression(
                new MeasureOperand(new MeasureField(Measure.REFERRAL, AggregateFunction.SUM.name())),
                ComparisonOperator.EQUAL,
                new NumericPlaceholder("${clickValue}"));
        refClick.setFilter(whereClause);
        refClick.setHaving(having);
        ref.setClickTableQuery(refClick);

        List<ColumnDef> refDefs = new ArrayList<>();
        refDefs.add(new ColumnDef("values[1]", "Name"));
        refDefs.add(new ColumnDef("values[2]", "Referrals"));
        ref.setColumnDefs(refDefs);
        row3.setReports(new ArrayList<>());
        row3.getReports().add(ref);
    }
}
