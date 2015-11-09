package com.scholarscore.models.gradeformula;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.assignment.AssignmentType;
import com.scholarscore.models.assignment.StudentAssignment;
import com.scholarscore.util.GradeUtil;
import org.apache.commons.lang3.tuple.MutablePair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssignmentGradeFormula implements Serializable {
    protected Long termId;
    protected Date termStartDate;
    protected Date termEndDate;
    //The weight that this term's formula has in the overall section grade
    protected Double sectionGradeWeight;
    //The weight that assignment types have within the term grade
    protected Map<AssignmentType, Integer> assignmentTypeWeights;
    
    public AssignmentGradeFormula() {
        assignmentTypeWeights = new HashMap<>();
    }
    
    public AssignmentGradeFormula(Map<AssignmentType, Integer> weights) {
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
            Map<AssignmentType, MutablePair<Double, Double>> typeToAwardedAndAvailPoints = new HashMap<>();
            for(StudentAssignment sa : studentAssignments) {
                Date dueDate = sa.getAssignment().getDueDate();
                //If the assignment is within the term:
                if(dueDate.compareTo(termStartDate) >= 0 && dueDate.compareTo(termEndDate) <= 0) {
                    AssignmentType type = sa.getAssignment().getType();
                    //Don't count assignments that are not being included in the grade
                    if(!assignmentTypeWeights.containsKey(type)) {
                        continue;
                    }
                    //Don't count exempt assignments
                    if(sa.getExempt()) {
                        continue;
                    }
                    if(!typeToAwardedAndAvailPoints.containsKey(type)) {
                        typeToAwardedAndAvailPoints.put(type, new MutablePair<Double, Double>(0D, 0D));
                    }
                    //TODO: handle min default score here or is it in the data?
                    //CALCULATE AND INCREMENT AWARDED POINTS
                    Double awardedPoints = sa.getAwardedPoints();
                    if(type.equals(AssignmentType.ATTENDANCE) && sa.getCompleted()) {
                        awardedPoints = sa.getAvailablePoints().doubleValue();
                    }
                    if(null == awardedPoints) {
                        awardedPoints = 0D;
                    }
                    if(null != sa.getAssignment().getWeight()) {
                        awardedPoints = awardedPoints * sa.getAssignment().getWeight();
                    }
                    Double left = typeToAwardedAndAvailPoints.get(type).getLeft();
                    typeToAwardedAndAvailPoints.get(type).setLeft(left + awardedPoints);

                    //CALCULATE AND INCREMENT AVAILABLE POINTS
                    Double availablePoints = sa.getAssignment().getAvailablePoints().doubleValue();
                    if(null != sa.getAssignment().getWeight()) {
                        availablePoints = availablePoints * sa.getAssignment().getWeight();
                    }
                    Double right = typeToAwardedAndAvailPoints.get(type).getRight();
                    typeToAwardedAndAvailPoints.get(type).setRight(right + availablePoints);
                }
            }

            Double numerator = 0D;
            Double denominator = 0D;
            for(Map.Entry<AssignmentType, MutablePair<Double, Double>> entry : typeToAwardedAndAvailPoints.entrySet()) {
                AssignmentType type = entry.getKey();
                MutablePair<Double, Double> values = entry.getValue();
                numerator += values.getLeft() / values.getRight() * assignmentTypeWeights.get(type);
                denominator += assignmentTypeWeights.get(type);
            }
            newlyCalculatedGrade = numerator / denominator;
        }
        return newlyCalculatedGrade;
    }

    public Long getTermId() {
        return termId;
    }

    public void setTermId(Long termId) {
        this.termId = termId;
    }

    public Double getSectionGradeWeight() {
        return sectionGradeWeight;
    }

    public void setSectionGradeWeight(Double sectionGradeWeight) {
        this.sectionGradeWeight = sectionGradeWeight;
    }

    public Map<AssignmentType, Integer> getAssignmentTypeWeights() {
        return assignmentTypeWeights;
    }

    public void setAssignmentTypeWeights(
            Map<AssignmentType, Integer> assignmentTypeWeights) {
        this.assignmentTypeWeights = assignmentTypeWeights;
    }

    public Date getTermStartDate() {
        return termStartDate;
    }

    public void setTermStartDate(Date termStartDate) {
        this.termStartDate = termStartDate;
    }

    public Date getTermEndDate() {
        return termEndDate;
    }

    public void setTermEndDate(Date termEndDate) {
        this.termEndDate = termEndDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final AssignmentGradeFormula other = (AssignmentGradeFormula) obj;
        return Objects.equals(this.assignmentTypeWeights, other.assignmentTypeWeights) &&
                Objects.equals(this.sectionGradeWeight, other.sectionGradeWeight) &&
                Objects.equals(this.termId, other.termId);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(assignmentTypeWeights, termId, sectionGradeWeight);
    }

    @Override
    public String toString() {
        return "AssignmentGradeFormula{" +
                "termId=" + termId +
                "sectionGradeWeight=" + sectionGradeWeight +
                "assignmentTypeWeights=" + assignmentTypeWeights +
                '}';
    }

    /**
     * Each class's Builder holds a copy of each attribute that the parent POJO has. We build up these properties using
     * a pattern of with[Attribute](Attribute attribute) and return the same instance of the Builder so that one can easily
     * chain setting attributes together.
     */
    public static class GradeFormulaBuilder{
        protected Map<AssignmentType, Integer> assignmentTypeWeights;
        protected Long termId;
        protected Double sectionGradeWeight;
        protected Date termStartDate;
        protected Date termEndDate;

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

        public GradeFormulaBuilder withSectionGradeWeight(final Double sectionGradeWeight){
            this.sectionGradeWeight = sectionGradeWeight;
            return this;
        }

        public GradeFormulaBuilder withTermId(final Long termId){
            this.termId = termId;
            return this;
        }

        public GradeFormulaBuilder withStartDate(final Date startDate){
            this.termStartDate = startDate;
            return this;
        }

        public GradeFormulaBuilder withEndDate(final Date endDate){
            this.termEndDate = endDate;
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

        public AssignmentGradeFormula build(){
            AssignmentGradeFormula formula = new AssignmentGradeFormula();
            formula.setAssignmentTypeWeights(assignmentTypeWeights);
            formula.setSectionGradeWeight(sectionGradeWeight);
            formula.setTermId(termId);
            return formula;
        }

    }
}
