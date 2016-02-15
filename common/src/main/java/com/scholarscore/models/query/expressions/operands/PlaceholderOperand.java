package com.scholarscore.models.query.expressions.operands;

import java.io.Serializable;
import java.util.Objects;

/**
 * The placeholder operand represents a literal operand that does not have a static value.
 *
 * For example, a saved query may be used to query according to user-supplied start and end dates or user
 * supplied school IDs.  In these cases, we don't store the literal value, but rather a placeholder
 * indicating the data type and a string that will later be regex replaced with the appropriate value, such as
 * ${schoolId}, ${schoolYear.startDate} and so on.
 *
 * Created by markroper on 2/15/16.
 */
public class PlaceholderOperand implements Serializable, IOperand {
    String value;
    OperandType type;

    public PlaceholderOperand() {
    }

    public PlaceholderOperand(String value, OperandType type) {
        this.value = value;
        this.type = type;
    }
    @Override
    public OperandType getType() {
        return null;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setType(OperandType type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final PlaceholderOperand other = (PlaceholderOperand) obj;
        return Objects.equals(this.value, other.value)
                && Objects.equals(this.type, other.type);
    }
}
