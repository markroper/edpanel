package com.scholarscore.etl.powerschool.api.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mattg on 6/28/15.
 */
@XmlRootElement(name = "section")
public class PsSection {
    protected Long id;
    protected Long school_id;
    protected String course_id;
    protected Long term_id;
    protected String section_number;
    protected String expression;
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
    public String getExpression() {
        return expression;
    }
    public void setExpression(String expression) {
        this.expression = expression;
    }
    public Long getStaff_id() {
        return staff_id;
    }
    public void setStaff_id(Long staff_id) {
        this.staff_id = staff_id;
    }

    /**
     * Static factory method for converting powerschool schedule expression into
     * a map that is workable in EdPanel
     * @param expression
     * @return
     */
    public static Map<String,ArrayList<Long>> evaluateExpression(String expression) {
        //Example expression is this: 2(A-D) 3-4(E) 6(C-D) 7(B)
        //We want to convert that to a map like {A:[2], B:[2,7], C:[2,6], D:[2,6], E:[3,4] }'
        Map<String, ArrayList<Long>> cyclePeriods = new HashMap<>();
        for (String periodExpression : expression.split(" ")) {
            String period = periodExpression.split("\\(")[0];
            ArrayList<Long> periodIds = new ArrayList<>();
            String cycleDays = periodExpression.split("\\(")[1];

            //Check if period has comma or - in it. If not it is done
            if (period.indexOf('-') >= 0) {
                Long start = Long.valueOf(period.substring(0,1));
                Long end = Long.valueOf(period.substring(2,3));
                for (long i = start; i <= end; i++) {
                    periodIds.add(i);
                }

            } else if (period.indexOf(',') >= 0) {
                String[] periods = period.split(",");

                for (String splitPeriod: periods) {
                    if (splitPeriod.length() > 1) {
                        //We have a beautiful thing that looks like this: 3,4-7(A) This is not super likely
                        Long start = Long.valueOf(splitPeriod.substring(0,1));
                        Long end = Long.valueOf(splitPeriod.substring(2,3));
                        for (long i = start; i <= end; i++) {
                            periodIds.add(i);
                        }
                    } else {
                        periodIds.add(Long.valueOf(splitPeriod));
                    }
                }
            } else {
                //Our period is finished (muy bueno), we just need that value
                periodIds.add(Long.valueOf(period));
            }
            //For cleanliness remove the otehr expression
            cycleDays = cycleDays.replace(")", "");

            if (cycleDays.indexOf(',') >= 0) {
                //Add both of these to the map, or if they exist add the periods to the array
                for (String cycle : cycleDays.split(",")) {
                    if (cycle.length() > 1) {
                        //THis occurs if we have soemthing like: 3(A-B,D-E)
                        PsSection.addDashValues(cycle, periodIds, cyclePeriods);
                    } else {
                        if (null == cyclePeriods.get(cycle)) {
                            //Add it to there
                            ArrayList<Long> listToAdd = new ArrayList<>();
                            listToAdd.addAll(periodIds);
                            cyclePeriods.put(cycle, listToAdd);
                        } else {
                            cyclePeriods.get(cycle).addAll(periodIds);
                        }
                    }

                }

            } else if (cycleDays.indexOf('-') >= 0) {
                //Get ascii value of the char so we can do a loop
                PsSection.addDashValues(cycleDays, periodIds, cyclePeriods);

            }  else {
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
}
