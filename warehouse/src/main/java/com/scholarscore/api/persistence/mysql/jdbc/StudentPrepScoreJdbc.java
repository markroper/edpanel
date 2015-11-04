package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.StudentPrepScorePersistence;
import com.scholarscore.api.persistence.mysql.mapper.PrepScoreMapper;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.PrepScore;
import com.scholarscore.util.EdPanelDateUtil;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Hibernate;
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

        // define 'week' buckets -- each eligible prep score contributor (i.e. behavior event) will end up in one of these buckets
        // each date is a saturday that represents the entire following week (through to friday)
        Date[] allWeeks = EdPanelDateUtil.getSaturdayDatesForWeeksBetween(startDate, endDate);
        StringBuilder queryBuilder = new StringBuilder();
        
        // select student_fk(s) and their prep score(s) for each week that any behavior events exist
        queryBuilder.append("SELECT " + HibernateConsts.STUDENT_FK + ", ");
        queryBuilder.append(PrepScore.INITIAL_PREP_SCORE + " + sum(" + HibernateConsts.BEHAVIOR_POINT_VALUE + ")"
                + " as " + HibernateConsts.BEHAVIOR_POINT_VALUE + ", ");
        
        // build the CASES fragment of the query to bucket the behavior events by week
        queryBuilder.append(buildCaseSqlFragment(allWeeks));
        // FROM behavior
        queryBuilder.append(" FROM " + HibernateConsts.BEHAVIOR_TABLE);
        
        queryBuilder.append(" RIGHT OUTER JOIN " + HibernateConsts.STUDENT_TABLE + " ON " 
                + HibernateConsts.STUDENT_TABLE + "." + HibernateConsts.STUDENT_USER_FK + "="
                + HibernateConsts.BEHAVIOR_TABLE + "." + HibernateConsts.STUDENT_FK);
        
        // WHERE: filter by date range...
        queryBuilder.append(" WHERE (" + buildDateWhereClauseSqlFragment(allWeeks) + ")");
        // ... and filter by student
        queryBuilder.append(" AND (" + buildStudentWhereClauseSqlFragment(studentIds) + ")");
        // group by student+date(s)
        queryBuilder.append(" GROUP BY " + HibernateConsts.STUDENT_FK + ", " + HibernateConsts.START_DATE + ", " + HibernateConsts.END_DATE);
        logger.info("Built query for prepscore: " + queryBuilder.toString());
        // System.out.println("Built query for prepscore: " + queryBuilder.toString());

        StringBuilder newQueryBuilder = new StringBuilder();
        newQueryBuilder.append("select student_user_fk, derived_weeks.start_date, derived_weeks.end_date, (90 + coalesce(inner_point_value,0)) as point_value ");
        newQueryBuilder.append(" FROM student");
        
        // join on derived table 
        newQueryBuilder.append(" JOIN (SELECT ");
        boolean first = true;
        for (Date week : allWeeks) {
            if (!first) {
                newQueryBuilder.append("UNION SELECT ");
            }
            newQueryBuilder.append("'" + getFormatter().format(week) + "' ");
            if (first) { 
                newQueryBuilder.append("as start_date "); 
            }
            newQueryBuilder.append(", ");
            newQueryBuilder.append("'" + getFormatter().format(DateUtils.addDays(week, 6)) + "' ");
            if (first) {
                newQueryBuilder.append("as end_date ");
                first = false;
            }
        }
        newQueryBuilder.append(") as derived_weeks ");
        newQueryBuilder.append("LEFT OUTER JOIN ( ");

        newQueryBuilder.append("SELECT student_fk, sum(coalesce(point_value,0)) as inner_point_value, " + buildCaseSqlFragment(allWeeks) 
                + " FROM behavior " 
                + " group by student_fk, start_date ");
        
        newQueryBuilder.append(") as bucketed_behavior_events ");
        newQueryBuilder.append("ON bucketed_behavior_events.student_fk=student_user_fk AND bucketed_behavior_events.start_date=derived_weeks.start_date");
        
        // run query, return results
        System.out.println("Built query for prepscore: " + newQueryBuilder.toString());
        return jdbcTemplate.query(newQueryBuilder.toString(), new HashMap<>(), new PrepScoreMapper());
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
        caseOneBuilder.append(" END as " + HibernateConsts.START_DATE);
        caseTwoBuilder.append(" END as " + HibernateConsts.END_DATE);

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
            sb.append(HibernateConsts.STUDENT_FK + " = '" + studentId + "'");
        }
        return sb.toString();
    }
    
    private SimpleDateFormat getFormatter() {
        return formatter.get();
    }
}
