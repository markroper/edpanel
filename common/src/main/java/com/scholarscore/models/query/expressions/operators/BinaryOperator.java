package com.scholarscore.models.query.expressions.operators;

import java.io.Serializable;

/**
 * Enumerates the supported BinaryOperators in the warehouse reporting
 * filter expression criteria.
 * 
 * @author markroper
 *
 */
public enum BinaryOperator implements IOperator, Serializable {
    OR,
    AND
}
