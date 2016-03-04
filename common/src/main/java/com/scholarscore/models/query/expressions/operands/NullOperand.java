package com.scholarscore.models.query.expressions.operands;

import java.io.Serializable;

/**
 * Created by cwallace on 3/3/16.
 */
public class NullOperand implements Serializable, IOperand {
    protected final OperandType type;

    public NullOperand() {
        this.type = OperandType.NULL;
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
        return true;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode();
    }
}