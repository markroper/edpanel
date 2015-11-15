package com.scholarscore.models.gradeformula;

import org.apache.commons.lang3.tuple.MutablePair;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by markroper on 11/15/15.
 */
public class AssignmentAndTypeGrades {
    protected Map<String, MutablePair<Double, Double>> typeToAwardedAndAvailPoints = new HashMap<>();
    protected Map<Long, MutablePair<Double, Double>> assignmentIdToPoints = new HashMap<>();

    public AssignmentAndTypeGrades() {}

    public AssignmentAndTypeGrades(
            Map<String, MutablePair<Double, Double>> typeMap, Map<Long,
            MutablePair<Double,Double>> assignmentMap) {
        this.typeToAwardedAndAvailPoints = typeMap;
        this.assignmentIdToPoints = assignmentMap;
    }
    public Map<String, MutablePair<Double, Double>> getTypeToAwardedAndAvailPoints() {
        return typeToAwardedAndAvailPoints;
    }

    public void setTypeToAwardedAndAvailPoints(Map<String, MutablePair<Double, Double>> typeToAwardedAndAvailPoints) {
        this.typeToAwardedAndAvailPoints = typeToAwardedAndAvailPoints;
    }

    public Map<Long, MutablePair<Double, Double>> getAssignmentIdToPoints() {
        return assignmentIdToPoints;
    }

    public void setAssignmentIdToPoints(Map<Long, MutablePair<Double, Double>> assignmentIdToPoints) {
        this.assignmentIdToPoints = assignmentIdToPoints;
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeToAwardedAndAvailPoints, assignmentIdToPoints);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final AssignmentAndTypeGrades other = (AssignmentAndTypeGrades) obj;
        return Objects.equals(this.typeToAwardedAndAvailPoints, other.typeToAwardedAndAvailPoints)
                && Objects.equals(this.assignmentIdToPoints, other.assignmentIdToPoints);
    }
}
