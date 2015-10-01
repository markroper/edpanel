package com.scholarscore.models.query.expressions.operands;

import java.io.Serializable;
import java.util.Objects;

import com.scholarscore.models.query.DimensionField;

@SuppressWarnings("serial")
public class DimensionOperand implements Serializable, IOperand {
    protected DimensionField value;
    protected final OperandType type;

    public DimensionOperand() {
        this.type = OperandType.DIMENSION;
    }
    
    public DimensionOperand(DimensionField dimension) {
        this();
        this.value = dimension;
    }

    public DimensionField getValue() {
        return value;
    }

    public void setValue(DimensionField value) {
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
        final DimensionOperand other = (DimensionOperand) obj;
        return Objects.equals(this.value, other.value);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(value);
    }
}
