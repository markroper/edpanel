package com.scholarscore.etl.powerschool.api.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mattg on 6/28/15.
 */
@XmlRootElement(name = "section")
public class PsSection {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(PsSection.class);
     
    protected Long id;
    protected Long school_id;
    protected String course_id;
    protected Long term_id;
    protected String section_number;
    protected String internal_expression;
    protected Long staff_id;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getSchool_id() {
        return school_id;
    }
    public void setSchool_id(Long school_id) {
        this.school_id = school_id;
    }
    public String getCourse_id() {
        return course_id;
    }
    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }
    public Long getTerm_id() {
        return term_id;
    }
    public void setTerm_id(Long term_id) {
        this.term_id = term_id;
    }
    public String getSection_number() {
        return section_number;
    }
    public void setSection_number(String section_number) {
        this.section_number = section_number;
    }
    public String getInternal_expression() {
        return internal_expression;
    }
    public void setInternal_expression(String internal_expression) {
        this.internal_expression = internal_expression;
    }
    public Long getStaff_id() {
        return staff_id;
    }
    public void setStaff_id(Long staff_id) {
        this.staff_id = staff_id;
    }

    /**
     * Static factory method for converting powerschool schedule internal_expression into
     * a map that is workable in EdPanel
     * @param expression
     * @return
     */
    public static Map<String,ArrayList<Long>> evaluateExpression(String expression) {
        //Example internal_expression is this: 2(A-D) 3-4(E) 6(C-D) 7(B)
        //We want to convert that to a map like {A:[2], B:[2,7], C:[2,6], D:[2,6], E:[3,4] }'
        Map<String, ArrayList<Long>> cyclePeriods = new HashMap<>();
        for (String periodExpression : expression.split("\\)")) {
            periodExpression = periodExpression.replace(" ", "");
            String period = periodExpression.split("\\(")[0];
            ArrayList<Long> periodIds = new ArrayList<>();
            String cycleDays = periodExpression.split("\\(")[1];

            addPeriodExpression(period, periodIds);

            if (cycleDays.indexOf(',') >= 0) {
                //Add both of these to the map, or if they exist add the periods to the array
                for (String cycle : cycleDays.split(",")) {
                    if (cycle.length() > 1) {
                        //This occurs if we have something like: 3(A-B,D-E)
                        PsSection.addDashValues(cycle, periodIds, cyclePeriods);
                    } else {
                        if (null == cyclePeriods.get(cycle)) {
                            ArrayList<Long> listToAdd = new ArrayList<>();
                            cyclePeriods.put(cycle, listToAdd);
                        }
                        cyclePeriods.get(cycle).addAll(periodIds);
                    }

                }

            } else if (cycleDays.indexOf('-') >= 0) {
                //Get ascii value of the char so we can do a loop
                PsSection.addDashValues(cycleDays, periodIds, cyclePeriods);

            }  else {
                if (cycleDays.length() > 1) {
                    LOGGER.warn("Unexpected Schedule Expression when parsing periods in PsSection! \n" + 
                            "Expression: " + cycleDays + " - expected length of 1! Using first letter " + 
                            cycleDays.substring(0,1) + " only");
                }
                //Get first character, Stack overflow says its faster than anything else
                String letter = cycleDays.substring(0,1);
                if (null == cyclePeriods.get(letter)) {
                    ArrayList<Long> listToAdd = new ArrayList<>();
                    listToAdd.addAll(periodIds);
                    cyclePeriods.put(letter, listToAdd);
                } else {
                    cyclePeriods.get(letter).addAll(periodIds);
                }
            }
        }
        return cyclePeriods;
    }

    private static void addDashValues(String cycleDays, ArrayList<Long> periodIds, Map<String, ArrayList<Long>> cyclePeriods) {
        if (cycleDays.length() > 3) { LOGGER.warn("Unexpected Schedule Expression seen when trying to parse dash internal_expression in PsSection"); }
        int startingChar = (int) cycleDays.charAt(0);
        int endChar = (int) cycleDays.charAt(2);
        for (int i = startingChar; i <= endChar; i++) {
            char c = (char) i;
            if (null == cyclePeriods.get(String.valueOf(c))) {
                ArrayList<Long> listToAdd = new ArrayList<>();
                listToAdd.addAll(periodIds);
                cyclePeriods.put(String.valueOf(c), listToAdd);
            } else {
                cyclePeriods.get(String.valueOf(c)).addAll(periodIds);
            }
        }
    }
    
    // Takes in the first part of a period internal_expression (referred to above as 'period')
    // which contains numeric strings representing periodIds. This method handles cases when...
    // the period Id is a single integer (e.g. 7)
    // the period Ids contains commas (e.g. 7,10)
    // the period Ids contain dash notation (e.g. 7-10 will add periodIds 7,8,9,10)
    // (it's fine if more than one of these exists in a given period string)
    private static void addPeriodExpression(String periodExpression, ArrayList<Long> periodIds) {
        // order of operations - first split on commas, then handle ranges
        if (periodExpression.contains(",")) {
            String[] periods = periodExpression.split(",");
            for (String thisPeriod : periods) {
                addPeriodExpression(thisPeriod, periodIds);
            }
        } else {
            // all commas have been split upon at this point, but we may still have a dash-separated range within

            // check if period string has dash in it
            int indexOfDashSeparator = periodExpression.indexOf('-');
            if (indexOfDashSeparator >= 0) {
                if (indexOfDashSeparator == 0) {
                    LOGGER.warn("Separator Index of 0 found, which should probably be an error. Expect a problem parsing this value...");
                }
                Long start = Long.valueOf(periodExpression.substring(0, indexOfDashSeparator));
                Long end = Long.valueOf(periodExpression.substring(indexOfDashSeparator + 1, periodExpression.length()));
                for (long i = start; i <= end; i++) {
                    periodIds.add(i);
                }
            } else {
                //Our period is finished (muy bueno), we just need that value
                periodIds.add(Long.valueOf(periodExpression));
            }
        }
    }
}
