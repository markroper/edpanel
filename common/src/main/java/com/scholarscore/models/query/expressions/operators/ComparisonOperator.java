package com.scholarscore.models.query.expressions.operators;

/**
 * Enumeration of the supported comparison operators in the warehouse
 * reporting model.
 * 
 * @author markroper
 *
 */
public enum ComparisonOperator implements IOperator {
    EQUAL,
    NOT_EQUAL,
    GREATER_THAN,
    GREATER_THAN_OR_EQUAL,
    LESS_THAN,
    LESS_THAN_OR_EQUAL,
    IN,
    LIKE;
}
