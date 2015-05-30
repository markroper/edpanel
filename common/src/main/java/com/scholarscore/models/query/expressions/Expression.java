package com.scholarscore.models.query.expressions;

import java.io.Serializable;
import java.util.Objects;

import com.scholarscore.models.query.expressions.operands.IOperand;
import com.scholarscore.models.query.expressions.operators.IOperator;

@SuppressWarnings("serial")
public class Expression implements Serializable, IOperand {
    protected IOperand leftHandSide;
    protected IOperator operator;
    protected IOperand rightHandSide;
    
    public Expression() {
    }
    
    public Expression(IOperand lhs, IOperator operator, IOperand rhs) {
        this.leftHandSide = lhs;
        this.operator = operator;
        this.rightHandSide = rhs;
    }
    
    //Getters, setters, equals(), hashCode()
    public IOperand getLeftHandSide() {
        return leftHandSide;
    }

    public void setLeftHandSide(IOperand leftHandSide) {
        this.leftHandSide = leftHandSide;
    }

    public IOperator getOperator() {
        return operator;
    }

    public void setOperator(IOperator operator) {
        this.operator = operator;
    }

    public IOperand getRightHandSide() {
        return rightHandSide;
    }

    public void setRightHandSide(IOperand rightHandSide) {
        this.rightHandSide = rightHandSide;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        final Expression other = (Expression) obj;
        return Objects.equals(this.leftHandSide, other.leftHandSide)
                && Objects.equals(this.operator, other.operator)
                && Objects.equals(this.rightHandSide, other.rightHandSide);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode()
                + Objects.hash(leftHandSide, operator, rightHandSide);
    }
}
