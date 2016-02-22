package com.scholarscore.models.query.expressions.operands;

/**
 * Created by markroper on 2/18/16.
 */
public class NumericPlaceholder extends PlaceholderOperand {

    public NumericPlaceholder() {}

    public NumericPlaceholder(String val) {
        super(val);
    }
    @Override
    public OperandType getType() {
        return OperandType.PLACEHOLDER_NUMERIC;
    }
}
