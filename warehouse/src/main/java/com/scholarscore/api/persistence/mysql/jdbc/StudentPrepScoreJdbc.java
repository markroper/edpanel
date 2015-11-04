package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.StudentPrepScorePersistence;
import com.scholarscore.api.persistence.mysql.mapper.PrepScoreMapper;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.PrepScore;
import com.scholarscore.util.EdPanelDateUtil;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * User: jordan
 * Date: 11/1/15
 * Time: 11:14 PM
 */
public class StudentPrepScoreJdbc extends BaseJdbc implements StudentPrepScorePersistence {
    
    private final static Logger logger = LoggerFactory.getLogger(StudentPrepScoreJdbc.class);

    // SimpleDateFormat is not thread-safe, so give one to each thread
    private static final ThreadLocal<SimpleDateFormat> formatter = new ThreadLocal<SimpleDateFormat>(){
        @Override
        protected SimpleDateFormat initialValue()
        {
            return new SimpleDateFormat(EdPanelDateUtil.EDPANEL_DATE_FORMAT);
        }
    };
    
    @Override
    public List<PrepScore> selectStudentPrepScore(Long[] studentIds, Date startDate, Date endDate) {
        // begin (attempted) sensible default handling
        if (startDate == null && endDate == null) {
            startDate = Calendar.getInstance().getTime();   // both null - set StartDate (EndDate set below)
        } else if (startDate == null) {
            startDate = endDate;            // startDate null, endDate not - use EndDate
        }
        if (endDate == null) {
            endDate = startDate;            // endDate null, startDate not (possibly set above if both were null)
        }
        if (endDate.before(startDate)) {
            endDate = startDate;            // if endDate is before startDate, move it up to startDate
        }
        // end sensible default handling

        // define 'week' buckets -- each eligible prep score contributor (i.e. behavior event) will end up in one of these buckets
        // each date is a saturday that represents the entire following week (through to friday)
        Date[] allWeeks = EdPanelDateUtil.getSaturdayDatesForWeeksBetween(startDate, endDate);
        
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT " + HibernateConsts.STUDENT_USER_FK + ", " 
                + HibernateConsts.PREPSCORE_DERIVED_WEEKS_TABLE + "." + HibernateConsts.PREPSCORE_START_DATE + ", " 
                + HibernateConsts.PREPSCORE_DERIVED_WEEKS_TABLE + "." + HibernateConsts.PREPSCORE_END_DATE + ", "
                + "(" + PrepScore.INITIAL_PREP_SCORE + " + coalesce(" + HibernateConsts.PREPSCORE_DERIVED_INNER_POINT_VALUE + ",0)) as " + HibernateConsts.BEHAVIOR_POINT_VALUE + " ");
        queryBuilder.append(" FROM " + HibernateConsts.STUDENT_TABLE);
        
        // JOIN on derived table that contains list of weeks requested to get all student-weeks
        queryBuilder.append(" " + buildDerivedDateTableSqlFragment(allWeeks));

        // JOIN on aggregated behavior events (grouped by student-week, so they can be joined to the above)
        queryBuilder.append(" " + buildBehaviorJoinClauseSqlFragment());
        
        // WHERE to filter only requested student
        queryBuilder.append(" WHERE " + buildStudentWhereClauseSqlFragment(studentIds));
        
        // run query, return results
        logger.info("Built query for prepscore: " + queryBuilder.toString());
        return jdbcTemplate.query(queryBuilder.toString(), new HashMap<>(), new PrepScoreMapper());
    }

    private String buildBehaviorJoinClauseSqlFragment() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("LEFT OUTER JOIN ( ");
        stringBuilder.append("SELECT "
                + HibernateConsts.STUDENT_FK + ", "
                + "sum(coalesce(" + HibernateConsts.BEHAVIOR_POINT_VALUE + ",0)) as " + HibernateConsts.PREPSCORE_DERIVED_INNER_POINT_VALUE + ", "
                + buildCaseSqlFragment(allWeeks));
        stringBuilder.append(" FROM " + HibernateConsts.BEHAVIOR_TABLE
                + " group by " + HibernateConsts.STUDENT_FK
                + ", " + HibernateConsts.PREPSCORE_START_DATE + " ");
        stringBuilder.append(") as " + HibernateConsts.PREPSCORE_DERIVED_BUCKETED_BEHAVIOR_TABLE + " ");

        stringBuilder.append("ON " + HibernateConsts.PREPSCORE_DERIVED_BUCKETED_BEHAVIOR_TABLE + "." + HibernateConsts.STUDENT_FK
                + "=" + HibernateConsts.STUDENT_USER_FK
                + " AND "
                + HibernateConsts.PREPSCORE_DERIVED_BUCKETED_BEHAVIOR_TABLE + "." + HibernateConsts.PREPSCORE_START_DATE
                + "=" + HibernateConsts.PREPSCORE_DERIVED_WEEKS_TABLE + "." + HibernateConsts.PREPSCORE_START_DATE);
        return stringBuilder.toString();
    }

    private String buildDerivedDateTableSqlFragment(Date[] allWeeks) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" JOIN (SELECT ");
        boolean first = true;
        for (Date week : allWeeks) {
            if (!first) {
                stringBuilder.append("UNION SELECT ");
            }
            stringBuilder.append("'" + getFormatter().format(week) + "' ");
            if (first) {
                stringBuilder.append("as " + HibernateConsts.PREPSCORE_START_DATE + " ");
            }
            stringBuilder.append(", ");
            stringBuilder.append("'" + getFormatter().format(DateUtils.addDays(week, 6)) + "' ");
            if (first) {
                stringBuilder.append("as " + HibernateConsts.PREPSCORE_END_DATE + " ");
                first = false;
            }
        }
        stringBuilder.append(") as " + HibernateConsts.PREPSCORE_DERIVED_WEEKS_TABLE);
        return stringBuilder.toString();
    }

    private String buildCaseSqlFragment(Date[] allWeeks) {
        SimpleDateFormat dateFormatter = getFormatter();
        // here, two CASE statements are used to bucket behavior events to specific weeks.
        // for each week, one case will return the saturday before the behavioral event, if not already a saturday
        // the other case will return the friday after the event, if not already a friday
        StringBuilder caseOneBuilder = new StringBuilder();
        StringBuilder caseTwoBuilder = new StringBuilder();
        caseOneBuilder.append("CASE ");
        caseTwoBuilder.append("CASE ");
        for (Date week : allWeeks) {
            // need the start date (saturday)...
            String saturdayDateString = dateFormatter.format(week);
            // and the end date (friday)
            String fridayDateString = dateFormatter.format(DateUtils.addDays(week, 6));

            //query for all records within this week, and bucket them appropriately
            // start_date column will be the saturday (beginning of the week of the prepscore in question)
            caseOneBuilder.append(" WHEN " + HibernateConsts.BEHAVIOR_DATE + " >= '" + saturdayDateString
                    + "' AND " + HibernateConsts.BEHAVIOR_DATE + " < '" + fridayDateString
                    + "' THEN '" + saturdayDateString + "'");
            // end_date column will be the friday (ending of the week of the prepscore in question)
            caseTwoBuilder.append(" WHEN " + HibernateConsts.BEHAVIOR_DATE + " >= '" + saturdayDateString
                    + "' AND " + HibernateConsts.BEHAVIOR_DATE + " < '" + fridayDateString
                    + "' THEN '" + fridayDateString + "'");

        }
        caseOneBuilder.append(" END as " + HibernateConsts.PREPSCORE_START_DATE);
        caseTwoBuilder.append(" END as " + HibernateConsts.PREPSCORE_END_DATE);

        return caseOneBuilder.toString() + ", " + caseTwoBuilder.toString();
    }

    private String buildDateWhereClauseSqlFragment(Date[] dates) {
        // filter by date range - sort weeks to get earliest/latest for outer bounds of query
        List<Date> allWeeksList = Arrays.asList(dates);
        Collections.sort(allWeeksList);
        String firstSaturdayString = getFormatter().format(allWeeksList.get(0)); // first saturday
        Date latestSaturday = allWeeksList.get(allWeeksList.size() - 1);
        String lastFridayString = getFormatter().format(DateUtils.addDays(latestSaturday, 6)); // add 6 days to go from sat -> fri
        return HibernateConsts.BEHAVIOR_DATE + " >= '" + firstSaturdayString + "'"
                + " AND " + HibernateConsts.BEHAVIOR_DATE + " < '" + lastFridayString + "'";

    }
    
    private String buildStudentWhereClauseSqlFragment(Long[] studentIds) {
        StringBuilder sb = new StringBuilder();
        boolean oneAdded = false;
        for (long studentId : studentIds) {
            if (oneAdded) {
                sb.append(" OR ");
            } else {
                oneAdded = true;
            }
            sb.append(HibernateConsts.STUDENT_USER_FK + " = '" + studentId + "'");
        }
        return sb.toString();
    }
    
    private SimpleDateFormat getFormatter() {
        return formatter.get();
    }
}
