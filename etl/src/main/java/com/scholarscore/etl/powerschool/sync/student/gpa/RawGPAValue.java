package com.scholarscore.etl.powerschool.sync.student.gpa;

import com.scholarscore.models.gpa.*;

import java.time.LocalDate;
import java.util.HashMap;

/**
 * Created by mattg on 11/24/15.
 */
public class RawGPAValue {
    private GPAType type;
    private Double gpaValue;
    private Long id;
    private HashMap<String, Double> termValues = new HashMap<>();
    private Double value;
    private Long studentId;

    public void setType(GPAType type) {
        this.type = type;
    }

    public GPAType getType() {
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
        switch (type) {
            case added_value:
                gpa = new AddedValueGpa();
                gpa.setStudentId(getStudentId());
                gpa.setScore(value);
                gpa.setCalculationDate(LocalDate.now());
                gpa.setType(GpaTypes.ADDED_VALUE);
                break;
            case simple_percent:
                gpa = new SimplePercentGpa();
                gpa.setStudentId(getStudentId());
                gpa.setScore(value);
                gpa.setCalculationDate(LocalDate.now());
                gpa.setType(GpaTypes.SIMPLE_PERCENT);
                break;
            case simple:
                gpa = new SimpleGpa();
                gpa.setStudentId(getStudentId());
                gpa.setScore(value);
                gpa.setCalculationDate(LocalDate.now());
                gpa.setType(GpaTypes.SIMPLE);
                break;
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
