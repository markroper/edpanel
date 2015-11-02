package com.scholarscore.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Named 'EdPanelDateUtil' because this stuff is probably not generalizable beyond edpanel
 *  
 * The reason these methods are currently centered around weeks, with the saturday-friday week holding
 * special significance, is because this is how the formula of 'prepscore' at nina's school is calculated.
 *  
 * User: jordan
 * Date: 11/1/15
 * Time: 3:12 PM
 */
public class EdPanelDateUtil {
    
    public static final String EDPANEL_DATE_FORMAT = "yyyy-MM-dd";
    
    // constructs an array containing zero or more dates.
    // each of these dates will occur on a saturday, which for our purposes represents a week (saturday - friday)
    // if the start date provided is not a saturday, the most recent saturday BEFORE the specified date will be used
    // if the end date provided is not a friday, the soonest friday AFTER the specified date will be used
    public static Date[] getSaturdayDatesForWeeksBetween(Date startDate, Date endDate) {
        // if date is saturday, use date
        Date currentSaturday;
        if (isDayOfWeek(startDate, Calendar.SATURDAY)) {
            currentSaturday = startDate;
        } else {
            // otherwise, get the preceding saturday from the non-saturday
            currentSaturday = getRecentSaturdayForDate(startDate);
        }

        ArrayList<Date> validSaturdayDates = new ArrayList<>();
        // if the date is in the past, save it and increment a week
        // keep going until we hit (pass, more likely) our end date)
        while (!currentSaturday.after(endDate)) {
            validSaturdayDates.add(currentSaturday);
            Calendar c = Calendar.getInstance();
            c.setTime(currentSaturday);
            c.add(Calendar.DAY_OF_MONTH, 7);
            currentSaturday = c.getTime();
        }

        return validSaturdayDates.toArray(new Date[0]);
    }

    public static Date getRecentSaturdayForDate(Date allPrepScoresSince) {
        if (allPrepScoresSince == null) { return null; }
        if (isDayOfWeek(allPrepScoresSince, Calendar.SATURDAY)) { return allPrepScoresSince; }

        Calendar c = Calendar.getInstance();
        c.setTime(allPrepScoresSince);
        while (!(c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)) {
            c.add(Calendar.DAY_OF_MONTH, -1);
        }
        return c.getTime();
    }

    public static boolean isDayOfWeek(Date allPrepScoresSince, int dayOfWeek) {
        Calendar c = Calendar.getInstance();
        c.setTime(allPrepScoresSince);
        return (c.get(Calendar.DAY_OF_WEEK) == dayOfWeek);
    }

}
