package com.scholarscore.models.query;

/**
 * Enumerates the supported aggregate functions within reports.
 * 
 * @author markroper
 *
 */
public enum AggregateFunction {
    SUM,
    AVG,
    STD_DEV,
    COUNT,
    YEARWEEK,
    MONTH,
    YEAR;
}
