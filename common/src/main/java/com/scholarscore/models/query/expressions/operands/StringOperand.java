package com.scholarscore.models.query.expressions.operands;

import java.io.Serializable;
import java.util.Objects;

@SuppressWarnings("serial")
public class StringOperand implements Serializable, IOperand {
    protected String value;

    public StringOperand() {
    }

    public StringOperand(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        final StringOperand other = (StringOperand) obj;
        return Objects.equals(this.value, other.value);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(value);
    }
}
