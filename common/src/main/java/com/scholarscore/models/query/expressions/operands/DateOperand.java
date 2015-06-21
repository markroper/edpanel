package com.scholarscore.models.query.expressions.operands;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@SuppressWarnings("serial")
public class DateOperand implements Serializable, IOperand {
    Date value;
    OperandType type;

    public DateOperand() {
        this.type = OperandType.DATE;
    }
    
    public DateOperand(Date date) {
        this();
        this.value = date;
    }

    public Date getValue() {
        return value;
    }

    public void setValue(Date value) {
        this.value = value;
    }
    
    @Override
    public OperandType getType() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        final DateOperand other = (DateOperand) obj;
        return Objects.equals(this.value, other.value);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(value);
    }
}
