package com.scholarscore.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.assignment.AssignmentType;
import com.scholarscore.models.assignment.StudentAssignment;
import com.scholarscore.util.GradeUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GradeFormula implements Serializable {
    Map<AssignmentType, Integer> assignmentTypeWeights;
    
    public GradeFormula() {
        assignmentTypeWeights = new HashMap<>();
    }
    
    public GradeFormula(Map<AssignmentType, Integer> weights) {
        assignmentTypeWeights = weights;
    }
    
    @JsonIgnore
    public boolean isValid() {
        if(null == assignmentTypeWeights || assignmentTypeWeights.isEmpty()) {
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
        double newlyCalculatedGrade = 0D;
        if(null == assignmentTypeWeights || assignmentTypeWeights.isEmpty()) {
            newlyCalculatedGrade = GradeUtil.calculateAverageGrade(studentAssignments);
        } else {
            Map<AssignmentType, ArrayList<Double>> assignmentTypeToGrades = new HashMap<>();
            for(StudentAssignment sa : studentAssignments) {
                AssignmentType type = sa.getAssignment().getType();
                if(!assignmentTypeWeights.containsKey(type)) {
                    continue;
                }
                if(!assignmentTypeToGrades.containsKey(type)) {
                    assignmentTypeToGrades.put(type, new ArrayList<Double>());
                }
                if((type.equals(AssignmentType.ATTENDANCE) || type.equals(AssignmentType.HOMEWORK)) && 
                        null != sa.getCompleted()) {
                    double awarded = 0D;
                    if(sa.getCompleted()) {
                        awarded = 1D;
                    }
                    assignmentTypeToGrades.get(type).add(awarded);
                } else if(null != sa.getAwardedPoints() && null != sa.getAvailablePoints()) {
                    if(sa.getAwardedPoints().equals(0D)) {
                        assignmentTypeToGrades.get(type).add(0D);
                    } else {
                        assignmentTypeToGrades.get(type).add(((double)sa.getAwardedPoints() / (double)sa.getAvailablePoints()));
                    }
                }
            }
            //Now that we've got grades for each assignment category calculated, roll them up with the weighting
            Integer finalWeightsDivisor = 0;
            for(Map.Entry<AssignmentType, ArrayList<Double>> entry : assignmentTypeToGrades.entrySet()) {
                Double assignmentTypeGrade = 0D;
                for(Double d : entry.getValue()) {
                    assignmentTypeGrade += d;
                }
                if(assignmentTypeGrade > 0) {
                    assignmentTypeGrade = assignmentTypeGrade / entry.getValue().size();
                }
                Integer currentWeight = assignmentTypeWeights.get(entry.getKey());
                finalWeightsDivisor += currentWeight;
                newlyCalculatedGrade += assignmentTypeGrade * currentWeight;
            }
            newlyCalculatedGrade = newlyCalculatedGrade / (double) finalWeightsDivisor * (double) 100;
            
        }
        return newlyCalculatedGrade;
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
        final GradeFormula other = (GradeFormula) obj;
        return Objects.equals(this.assignmentTypeWeights, other.assignmentTypeWeights);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(assignmentTypeWeights);
    }

    @Override
    public String toString() {
        return "GradeFormula{" +
                "assignmentTypeWeights=" + assignmentTypeWeights +
                '}';
    }

    /**
     * Each class's Builder holds a copy of each attribute that the parent POJO has. We build up these properties using
     * a pattern of with[Attribute](Attribute attribute) and return the same instance of the Builder so that one can easily
     * chain setting attributes together.
     */
    public static class GradeFormulaBuilder{
        Map<AssignmentType, Integer> assignmentTypeWeights;

        public GradeFormulaBuilder(){
            assignmentTypeWeights = new HashMap<>();
        }

        /**
         * Add a single assignmentType to a map
         * This will override the assignmentType's weight if it already exists in the map
         * @param type the AssignmentType as key
         * @param weight the weight to assign this AssignmentType
         * @return this builder object
         */
        public GradeFormulaBuilder withAssignmentTypeWeight(final AssignmentType type, final int weight){
            assignmentTypeWeights.put(type, weight);
            return this;
        }

        /**
         * Put all of the items in the passed map into this map - this method is additive
         * @param assignmentTypeWeights the weights for each assignment type in the map
         * @return this builder
         */
        public GradeFormulaBuilder withAssignmentTypeWeights(final Map<AssignmentType, Integer> assignmentTypeWeights){
            this.assignmentTypeWeights.putAll(assignmentTypeWeights);
            return this;
        }

        public GradeFormula build(){
            GradeFormula formula = new GradeFormula();
            formula.setAssignmentTypeWeights(assignmentTypeWeights);
            return formula;
        }

    }
}
