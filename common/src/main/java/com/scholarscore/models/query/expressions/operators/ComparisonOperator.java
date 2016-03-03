package com.scholarscore.models.query.expressions.operators;

import java.io.Serializable;

/**
 * Enumeration of the supported comparison operators in the warehouse
 * reporting model.
 * 
 * @author markroper
 *
 */
public enum ComparisonOperator implements IOperator, Serializable {
    EQUAL,
    NOT_EQUAL,
    GREATER_THAN,
    GREATER_THAN_OR_EQUAL,
    LESS_THAN,
    LESS_THAN_OR_EQUAL,
    IN,
    LIKE,
    IS,
    IS_NOT
}
