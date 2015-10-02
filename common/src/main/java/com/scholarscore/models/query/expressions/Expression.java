package com.scholarscore.models.query.expressions;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.query.expressions.operands.IOperand;
import com.scholarscore.models.query.expressions.operands.OperandType;
import com.scholarscore.models.query.expressions.operators.IOperator;

@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Expression implements Serializable, IOperand {
    final protected OperandType type;
    protected IOperand leftHandSide;
    protected IOperator operator;
    protected IOperand rightHandSide;
    
    public Expression() {
        type = OperandType.EXPRESSION;
    }
    
    public Expression(IOperand lhs, IOperator operator, IOperand rhs) {
        this();
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
        final Expression other = (Expression) obj;
        return Objects.equals(this.leftHandSide, other.leftHandSide)
                && Objects.equals(this.operator, other.operator)
                && Objects.equals(this.rightHandSide, other.rightHandSide)
                && Objects.equals(this.type, other.type);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode()
                + Objects.hash(leftHandSide, operator, rightHandSide, type);
    }
}
