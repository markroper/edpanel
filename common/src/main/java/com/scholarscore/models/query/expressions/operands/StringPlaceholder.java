package com.scholarscore.models.query.expressions.operands;

/**
 * Created by markroper on 2/18/16.
 */
public class StringPlaceholder extends PlaceholderOperand {
    public StringPlaceholder() {}

    public StringPlaceholder(String val) {
        super(val);
    }
    @Override
    public OperandType getType() {
        return OperandType.PLACEHOLDER_STRING;
    }
}
