package com.scholarscore.etl.powerschool.sync.student.gpa;

import com.scholarscore.models.gpa.*;

import java.time.LocalDate;
import java.util.HashMap;

/**
 * Created by mattg on 11/24/15.
 */
public class RawGpaValue {
    private GpaType type;
    private Double gpaValue;
    private Long id;
    private HashMap<String, Double> termValues = new HashMap<>();
    private Double value;
    private Long studentId;

    public void setType(GpaType type) {
        this.type = type;
    }

    public GpaType getType() {
        return type;
    }

    public Double getGpaValue() {
        return gpaValue;
    }

    public void setGpaValue(Double gpaValue) {
        this.gpaValue = gpaValue;
    }

    public HashMap<String, Double> getTermValues() {
        return termValues;
    }

    public void setTermValues(HashMap<String, Double> termValues) {
        this.termValues = termValues;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Gpa emit() {
        Gpa gpa = null;
        if(null != type) {
            switch (type) {
                case ADDED_VALUE:
                    gpa = new AddedValueGpa();
                    gpa.setType(GpaTypes.ADDED_VALUE);
                    break;
                case SIMPLE_PERCENT:
                    gpa = new SimplePercentGpa();
                    gpa.setType(GpaTypes.SIMPLE_PERCENT);
                    break;
                case SIMPLE:
                    gpa = new SimpleGpa();
                    gpa.setType(GpaTypes.SIMPLE);
                    break;
                case SIMPLE_ADDED_VALUE:
                    gpa = new SimpleAddedValueGpa();
                    gpa.setType(GpaTypes.SIMPLE);
                case WEIGHTED_ADDED_VALUE:
                    gpa = new SimpleAddedValueGpa();
                    gpa.setType(GpaTypes.WEIGHTED_ADDED_VALUE);
                    break;
                default:
                    return null;
            }
            gpa.setScore(value);
            gpa.setCalculationDate(LocalDate.now());
        }
        return gpa;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getStudentId() {
        return studentId;
    }
}
