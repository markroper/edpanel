package com.scholarscore.util;

import com.scholarscore.models.WeightedGradable;

import java.util.Collection;

/**
 * User: jordan
 * Date: 7/1/15
 * Time: 8:10 PM
 */
public class GradeUtil {
    
    private static final double ONE_HUNDRED_POINT_MULTIPLIER = 100.0;
    
    // returns the average grade on a scale from 0.0 (0%) to 1.0 (100%)
    public static Double calculateAverageGrade(Collection<? extends WeightedGradable> gradables) {
        double percentageScore = 0D;
        for(WeightedGradable g : gradables) {
            // awardedPoints and availablePoints can be set arbitrarily. The only important 
            // thing is the ratio between them. (4/5 == 80/100 == 160/200)
            if (null != g.getAwardedPoints() && null != g.getAvailablePoints()) {
                percentageScore +=
                        g.getAwardedPoints().doubleValue() / g.getAvailablePoints().doubleValue() * g.getWeight();
            }
        }
        // to avoid NaN. assumption is that no completed classes should be thought of as '100%' and not '0%'
        if (percentageScore == 0D) { return 1D; }
        return (double)Math.round(percentageScore / gradables.size() * 1000D) / 1000D;
    }
    
    public static Double calculateGPA(Integer gpaScale, Collection<? extends WeightedGradable> gradables) {
        return gpaScale * calculateAverageGrade(gradables);
    }
}
