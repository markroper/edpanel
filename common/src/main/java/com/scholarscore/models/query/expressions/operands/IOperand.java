package com.scholarscore.models.query.expressions.operands;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.scholarscore.models.query.expressions.Expression;
/**
 * Marker interface indicating that the implementing entity is a valid operand
 * in the warehouse reporting filter expression model.
 * 
 * @author markroper
 *
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = DimensionOperand.class, name="DIMENSION"),
    @JsonSubTypes.Type(value = DateOperand.class, name = "DATE"),
    @JsonSubTypes.Type(value = NumericOperand.class, name = "NUMERIC"),
    @JsonSubTypes.Type(value = StringOperand.class, name = "STRING"),
    @JsonSubTypes.Type(value = Expression.class, name = "EXPRESSION"),
    @JsonSubTypes.Type(value = MeasureOperand.class, name = "MEASURE"),
    @JsonSubTypes.Type(value = ListNumericOperand.class, name = "LIST_NUMERIC"),
    @JsonSubTypes.Type(value = StringPlaceholder.class, name = "PLACEHOLDER_STRING"),
    @JsonSubTypes.Type(value = DatePlaceholder.class, name = "PLACEHOLDER_DATE"),
    @JsonSubTypes.Type(value = NumericPlaceholder.class, name = "PLACEHOLDER_NUMERIC"),
    @JsonSubTypes.Type(value = NumericListPlaceholder.class, name = "PLACEHOLDER_LIST_NUMERIC")
})
public interface IOperand {
    /**
     * Returns the type of the operand
     * @return
     */
    public OperandType getType();
}
