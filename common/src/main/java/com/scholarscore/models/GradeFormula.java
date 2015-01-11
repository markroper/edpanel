package com.scholarscore.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.tuple.MutablePair;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GradeFormula implements Serializable {
    Map<AssignmentType, Integer> assignmentTypeWeights;
    
    public GradeFormula() { 
    }
    
    public GradeFormula(Map<AssignmentType, Integer> weights) {
        assignmentTypeWeights = weights;
    }
    
    @JsonIgnore
    public boolean isValid() {
        if(null == assignmentTypeWeights) {
            return true;
        }
        int totalWeights = 0;
        for(Map.Entry<AssignmentType, Integer> entry : assignmentTypeWeights.entrySet()) {
            totalWeights += entry.getValue();
        }
        return (totalWeights == 100);
    }
    
    public Double calculateGrade(Set<StudentAssignment> studentAssignments) {
        if(null == studentAssignments || !isValid()) {
            return null;
        }
        double calculatedGrade = 0D;
        if(null == assignmentTypeWeights) {
            long availablePoints = 0L;
            long awardedPoints = 0L;
            for(StudentAssignment sa : studentAssignments) {
                if(null != sa.getAwardedPoints()) {
                    awardedPoints += sa.getAwardedPoints();
                }
                if(null != sa.getAssignment() && null != sa.getAssignment().getAvailablePoints()) {
                    availablePoints += sa.getAssignment().getAvailablePoints();
                }
            }
            calculatedGrade = awardedPoints * 1.0 / availablePoints;
        } else {
            Map<AssignmentType, MutablePair<Long, Long>> calculatedGradeByType = new HashMap<>();
            for(StudentAssignment sa : studentAssignments) {
                AssignmentType type = sa.getAssignment().getType();
                if(!assignmentTypeWeights.containsKey(type)) {
                    continue;
                }
                if(!calculatedGradeByType.containsKey(type)) {
                    calculatedGradeByType.put(type, new MutablePair<>(0L, 0L));
                }
                if(null != sa.getAwardedPoints()) {
                    calculatedGradeByType.get(type).left += sa.getAwardedPoints();
                }
                if(null != sa.getAssignment() && null != sa.getAssignment().getAvailablePoints()) {
                    calculatedGradeByType.get(type).right += sa.getAssignment().getAvailablePoints();
                }
            }
            //Now we calculate the final score as the sum of AssignmentTypeAwardedPoints/AssignmentTypeAvailPoints * PercentOfGradeAsLong
            for(Map.Entry<AssignmentType, MutablePair<Long, Long>> entry : calculatedGradeByType.entrySet()) {
                calculatedGrade += (entry.getValue().left * 1.0 / entry.getValue().right * assignmentTypeWeights.get(entry.getKey()));
            }
        }
        return calculatedGrade;
    }

    public Map<AssignmentType, Integer> getAssignmentTypeWeights() {
        return assignmentTypeWeights;
    }

    public void setAssignmentTypeWeights(
            Map<AssignmentType, Integer> assignmentTypeWeights) {
        this.assignmentTypeWeights = assignmentTypeWeights;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        if(!super.equals(obj)) {
            return false;
        }
        final GradeFormula other = (GradeFormula) obj;
        return Objects.equals(this.assignmentTypeWeights, other.assignmentTypeWeights);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(assignmentTypeWeights);
    }
}
