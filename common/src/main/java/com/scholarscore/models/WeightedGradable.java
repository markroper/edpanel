package com.scholarscore.models;

/**
 * User: jordan
 * Date: 6/21/15
 * Time: 8:34 PM
 */
public interface WeightedGradable {

    public Number getAwardedPoints();
    public Number getAvailablePoints();

    // relative weight compared to other WeightedGradables
    public int getWeight();
}
