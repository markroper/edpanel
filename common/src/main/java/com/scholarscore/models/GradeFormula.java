package com.scholarscore.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.tuple.MutablePair;

import com.fasterxml.jackson.annotation.JsonInclude;

@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GradeFormula implements Serializable {
    Map<AssignmentType, Integer> assignmentTypeWeights;
    
    public GradeFormula() { 
    }
    
    public boolean isValid() {
        if(null == assignmentTypeWeights) {
            return true;
        }
        Integer totalWeights = 0;
        for(Map.Entry<AssignmentType, Integer> entry : assignmentTypeWeights.entrySet()) {
            totalWeights += entry.getValue();
        }
        return (totalWeights == 100);
    }
    
    public Double calculateGrade(Set<StudentAssignment> studentAssignments) {
        if(null == studentAssignments || !isValid()) {
            return null;
        }
        Double calculatedGrade = 0D;
        if(null == assignmentTypeWeights) {
            Long availablePoints = 0l;
            Long awardedPoints = 0l;
            for(StudentAssignment sa : studentAssignments) {
                if(null != sa.getAwardedPoints()) {
                    awardedPoints += sa.getAwardedPoints();
                }
                if(null != sa.getSectionAssignment() && null != sa.getSectionAssignment().getAvailablePoints()) {
                    availablePoints += sa.getSectionAssignment().getAvailablePoints();
                }
            }
            calculatedGrade = awardedPoints * 1.0 / availablePoints;
        } else {
            Map<AssignmentType, MutablePair<Long, Long>> calculatedGradeByType = new HashMap<>();
            for(StudentAssignment sa : studentAssignments) {
                AssignmentType type = sa.getSectionAssignment().getAssignment().getType();
                if(!assignmentTypeWeights.containsKey(type)) {
                    continue;
                }
                if(!calculatedGradeByType.containsKey(type)) {
                    calculatedGradeByType.put(type, new MutablePair<>(0L, 0L));
                }
                if(null != sa.getAwardedPoints()) {
                    calculatedGradeByType.get(type).left += sa.getAwardedPoints();
                }
                if(null != sa.getSectionAssignment() && null != sa.getSectionAssignment().getAvailablePoints()) {
                    calculatedGradeByType.get(type).right += sa.getSectionAssignment().getAvailablePoints();
                }
            }
            //Now we calculate the final score as the sum of AssignmentTypeAwardedPoints/AssignmentTypeAvailPoints / 100 * PercentOfGradeAsLong
            for(Map.Entry<AssignmentType, MutablePair<Long, Long>> entry : calculatedGradeByType.entrySet()) {
                calculatedGrade += (entry.getValue().left * 1.0 / entry.getValue().right / 1.0 * assignmentTypeWeights.get(entry.getKey()));
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
