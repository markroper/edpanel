package com.scholarscore.models.query;

import com.scholarscore.models.query.expressions.operands.IOperand;
import com.scholarscore.models.query.expressions.operators.IOperator;

import java.util.Objects;

/**
 * Created by markroper on 2/10/16.
 */
public class SubqueryExpression {
    Integer position;
    IOperator operator;
    IOperand operand;

    public SubqueryExpression() {

    }

    public SubqueryExpression(Integer pos, IOperator operator, IOperand operand) {
        position = pos;
        this.operand = operand;
        this.operator = operator;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public IOperator getOperator() {
        return operator;
    }

    public void setOperator(IOperator operator) {
        this.operator = operator;
    }

    public IOperand getOperand() {
        return operand;
    }

    public void setOperand(IOperand operand) {
        this.operand = operand;
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, operator, operand);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final SubqueryExpression other = (SubqueryExpression) obj;
        return Objects.equals(this.position, other.position)
                && Objects.equals(this.operator, other.operator)
                && Objects.equals(this.operand, other.operand);
    }
}
