package com.scholarscore.models.query.expressions.operands;

import java.io.Serializable;
import java.util.Objects;

@SuppressWarnings("serial")
public class StringOperand implements Serializable, IOperand {
    protected String value;
    protected OperandType type;

    public StringOperand() {
        this.type = OperandType.STRING;
    }

    public StringOperand(String value) {
        this();
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    @Override
    public OperandType getType() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
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
