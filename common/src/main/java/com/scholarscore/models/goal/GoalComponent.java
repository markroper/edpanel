package com.scholarscore.models.goal;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.scholarscore.models.user.Student;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by cwallace on 10/15/2015.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "componentType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = BehaviorComponent.class, name="BEHAVIOR"),
        @JsonSubTypes.Type(value = AttendanceComponent.class, name = "ATTENDANCE"),
        @JsonSubTypes.Type(value = ComplexComponent.class, name = "COMPLEX")
})
public abstract class GoalComponent implements Serializable {

    private Student student;
    private GoalType componentType;
    private Long modifier;

    public Long getModifier() {
        return modifier;
    }

    public void setModifier(Long modifier) {
        this.modifier = modifier;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public GoalType getComponentType() {
        return componentType;
    }

    public void setComponentType(GoalType componentType) {
        this.componentType = componentType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GoalComponent that = (GoalComponent) o;
        return Objects.equals(student, that.student) &&
                Objects.equals(modifier, that.modifier) &&
                Objects.equals(componentType, that.componentType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), componentType, student, modifier);
    }
}
