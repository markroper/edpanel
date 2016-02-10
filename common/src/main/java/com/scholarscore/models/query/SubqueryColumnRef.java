package com.scholarscore.models.query;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by markroper on 2/10/16.
 */
public class SubqueryColumnRef implements Serializable {
    Integer position;
    AggregateFunction function;

    public SubqueryColumnRef() {}

    public SubqueryColumnRef(Integer pos, AggregateFunction func) {
        position = pos;
        function = func;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public AggregateFunction getFunction() {
        return function;
    }

    public void setFunction(AggregateFunction function) {
        this.function = function;
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, function);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final SubqueryColumnRef other = (SubqueryColumnRef) obj;
        return Objects.equals(this.position, other.position)
                && Objects.equals(this.function, other.function);
    }
}

