package com.scholarscore.models.query.expressions.operands;

/**
 * Created by markroper on 2/18/16.
 */
public class NumericListPlaceholder extends PlaceholderOperand {
    public NumericListPlaceholder() {}

    public NumericListPlaceholder(String val) {
        super(val);
    }
    @Override
    public OperandType getType() {
        return OperandType.PLACEHOLDER_LIST_NUMERIC;
    }
}
