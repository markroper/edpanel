package com.scholarscore.models.query.expressions.operands;

/**
 * Created by markroper on 2/18/16.
 */
public class DatePlaceholder extends PlaceholderOperand {
    public DatePlaceholder() {}

    public DatePlaceholder(String val) {
        super(val);
    }
    @Override
    public OperandType getType() {
        return OperandType.PLACEHOLDER_DATE;
    }
}
