package com.scholarscore.models.query.expressions.operands;

import java.io.Serializable;

public enum OperandType implements Serializable {
    DATE,
    DIMENSION,
    MEASURE,
    NUMERIC,
    EXPRESSION,
    STRING,
    LIST_NUMERIC,
    PLACEHOLDER_DATE,
    PLACEHOLDER_NUMERIC,
    PLACEHOLDER_STRING,
    PLACEHOLDER_LIST_NUMERIC,
    NULL;
}
