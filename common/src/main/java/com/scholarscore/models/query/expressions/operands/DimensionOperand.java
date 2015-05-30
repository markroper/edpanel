package com.scholarscore.models.query.expressions.operands;

import java.io.Serializable;
import java.util.Objects;

import com.scholarscore.models.query.Dimension;

@SuppressWarnings("serial")
public class DimensionOperand implements Serializable, IOperand {
    protected Dimension value;

    public DimensionOperand() {

    }

    public Dimension getValue() {
        return value;
    }

    public void setValue(Dimension value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
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
