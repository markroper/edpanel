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
public abstract class PlaceholderOperand implements Serializable, IOperand {
    String value;

    public PlaceholderOperand() {
    }

    public PlaceholderOperand(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setType(OperandType t){
        //no op
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
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
        return Objects.equals(this.value, other.value);
    }
}
