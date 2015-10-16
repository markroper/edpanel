package com.scholarscore.models.goal;

import java.util.Objects;

/**
 * Component that is just a value. In case you wish to set a complex goal that starts with come initial Value
 * this component can be used
 * Created by cwallace on 10/16/2015.
 */
public class ConstantComponent extends GoalComponent {

    private Double initialValue;

    public ConstantComponent() {
        setComponentType(GoalType.CONSTANT);
    }

    public Double getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(Double initialValue) {
        this.initialValue = initialValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ConstantComponent that = (ConstantComponent) o;
        return Objects.equals(initialValue, that.initialValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), initialValue);
    }

    @Override
    public String toString() {
        return
                "GOAL " + "\n"
                        + "InitialValue: " + getInitialValue() +"\n"
                        + "ComponentType:" + getComponentType() + "\n"
                        + "Student: " + getStudent() + "\n"
                        + "Modifier: " + getModifier();
    }
}
