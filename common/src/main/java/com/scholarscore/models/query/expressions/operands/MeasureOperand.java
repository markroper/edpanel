package com.scholarscore.models.query.expressions.operands;

import java.io.Serializable;
import java.util.Objects;

import com.scholarscore.models.query.Measure;
import com.scholarscore.models.query.MeasureField;

@SuppressWarnings("serial")
public class MeasureOperand implements Serializable, IOperand {
    protected MeasureField value;
    protected OperandType type;
    
    public MeasureOperand() {
        this.type = OperandType.DIMENSION;
    }
    
    public MeasureOperand(MeasureField measure) {
        this.value = measure;
    }
    
    public MeasureField getValue() {
        return value;
    }

    public void setValue(MeasureField value) {
        this.value = value;
    }
    
    @Override
    public OperandType getType() {
        return OperandType.MEASURE;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final MeasureOperand other = (MeasureOperand) obj;
        return Objects.equals(this.value, other.value);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(value);
    }
}