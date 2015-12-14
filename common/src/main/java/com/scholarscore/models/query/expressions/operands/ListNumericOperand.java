package com.scholarscore.models.query.expressions.operands;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Expressions can have operands that are collections, for example, a statement like
 * select * from student where student.id in (1, 3,, 5, 7, 9).  This class represents 
 * the operand type that supports numeric data type lists as in the example above.
 * 
 * @author markroper
 *
 */
@SuppressWarnings("serial")
public class ListNumericOperand implements Serializable, IOperand {
    protected List<Number> value;
    protected OperandType type;
    
    public ListNumericOperand() {
        this.type = OperandType.LIST_NUMERIC;
    }
    
    @Override
    public OperandType getType() {
        return this.type;
    }

    public List<Number> getValue() {
        return value;
    }

    public void setValue(List<Number> value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ListNumericOperand other = (ListNumericOperand) obj;
        return Objects.equals(this.value, other.value) && Objects.equals(this.type, other.type);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(value);
    }
}
