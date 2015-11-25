package com.scholarscore.models.gpa;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.StudentSectionGrade;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;

/**
 * @author by markroper on 11/23/15.
 */
@Entity(name = HibernateConsts.GPA_TABLE)
@Table(name = HibernateConsts.GPA_TABLE)
@DiscriminatorColumn(name=HibernateConsts.GPA_TYPE, discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SimpleGpa.class, name="SIMPLE"),
        @JsonSubTypes.Type(value = SimplePercentGpa.class, name = "SIMPLE_PERCENT"),
        @JsonSubTypes.Type(value = WeightedGpa.class, name = "WEIGHTED"),
        @JsonSubTypes.Type(value = AddedValueGpa.class, name = "ADDED_VALUE")
})
public abstract class Gpa {
    protected Long studentId;
    protected Double score;
    protected LocalDate startDate;
    protected LocalDate endDate;
    protected LocalDate calculationDate;
    protected Long id;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = HibernateConsts.GPA_ID)
    public Long getId() { return id; }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = HibernateConsts.STUDENT_FK)
    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    @Column(name = HibernateConsts.GPA_SCORE)
    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    @Column(name = HibernateConsts.GPA_START_DATE)
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    @Column(name = HibernateConsts.GPA_END_DATE)
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Column(name = HibernateConsts.GPA_CALCULATION_DATE)
    public LocalDate getCalculationDate() {
        return calculationDate;
    }

    public void setCalculationDate(LocalDate calculationDate) {
        this.calculationDate = calculationDate;
    }

    /**
     * Must be implemented on the subclass
     *
     * @return
     */
    @Transient
    public abstract GpaTypes getType();

    @Transient
    public void setType(GpaTypes type) {
        //NO OP, we don't really set type
    }

    /**
     * There are various methods for calculating GPA, each subclass should implement its method and return a
     * Double value that is the GPA based on the sections provided it.
     * @param sections
     * @return
     */
    public abstract Double calculateGpa(Collection<StudentSectionGrade> sections);

    @Override
    public int hashCode() {
        return Objects.hash(studentId, score, endDate, startDate, calculationDate, id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Gpa other = (Gpa) obj;
        return Objects.equals(this.studentId, other.studentId)
                && Objects.equals(this.score, other.score)
                && Objects.equals(this.id, other.id)
                && Objects.equals(this.calculationDate, other.calculationDate)
                && Objects.equals(this.endDate, other.endDate)
                && Objects.equals(this.startDate, other.startDate);
    }
}
