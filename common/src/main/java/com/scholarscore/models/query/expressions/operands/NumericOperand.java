package com.scholarscore.models.query.expressions.operands;

import java.io.Serializable;
import java.util.Objects;

@SuppressWarnings("serial")
public class NumericOperand implements Serializable, IOperand {
    protected Number value;
    protected final OperandType type;

    public NumericOperand() {
        this.type = OperandType.NUMERIC;
    }

    public NumericOperand(Number value) {
        this();
        this.value = value;
    }

    public Number getValue() {
        return value;
    }

    public void setValue(Number value) {
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
        final NumericOperand other = (NumericOperand) obj;
        return Objects.equals(this.value, other.value);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(value);
    }
}
