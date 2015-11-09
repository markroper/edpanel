package com.scholarscore.models.gradeformula;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.assignment.AssignmentType;
import com.scholarscore.models.assignment.StudentAssignment;
import com.scholarscore.util.GradeUtil;
import org.apache.commons.lang3.tuple.MutablePair;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Created by markroper on 11/9/15.
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GradeFormula implements Serializable {
    Long id;
    String name;
    Date startDate;
    Date endDate;
    Long parentId;
    Set<GradeFormula> children;
    //TODO: missing low score to discard?
    Map<AssignmentType, Double> assignmentTypeWeights = new HashMap<>();
    //keyed on sourceSystemId
    Map<Long, Double> assignmentWeights = new HashMap<>();
    Map<Long, Double> childWeights = new HashMap<>();

    public GradeFormula() {

    }
    public GradeFormula(Map<AssignmentType, Double> weight) {
        this.assignmentTypeWeights = weight;
    }
    public Double calculateGrade(Set<StudentAssignment> studentAssignments) {
        Double numerator = 0D;
        Double denominator = 0D;
        if(!childWeights.isEmpty()) {
            //If there are child formulas & we have weights defined, delegate to those child formulas
            //And calculate the grade based ona weighted average of those.
            for(GradeFormula child: children) {
                if(childWeights.containsKey(child.getId())) {
                    numerator += child.calculateGrade(studentAssignments) * childWeights.get(child.getId());
                    denominator += childWeights.get(child.getId());
                }
            }
        } else if(!assignmentTypeWeights.isEmpty() || !assignmentWeights.isEmpty()) {
            //There are weightings defined for assignments or assignment types, so we use those!
            Map<AssignmentType, MutablePair<Double, Double>> typeToAwardedAndAvailPoints = new HashMap<>();
            Map<Long, MutablePair<Double, Double>> assignmentIdToPoints = new HashMap<>();
            for(StudentAssignment sa : studentAssignments) {
                Date dueDate = sa.getAssignment().getDueDate();
                //If the assignment is within the term:
                if(dueDate.compareTo(startDate) >= 0 && dueDate.compareTo(endDate) <= 0) {
                    if(assignmentWeights.containsKey(sa.getAssignment().getId())) {
                        Double weight = sa.getAssignment().getWeight();
                        assignmentIdToPoints.put(sa.getAssignment().getId(), new MutablePair<Double,Double>(
                                sa.getAwardedPoints() * weight,
                                sa.getAssignment().getAvailablePoints() * weight));
                    }

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
            for(Map.Entry<AssignmentType, MutablePair<Double, Double>> entry : typeToAwardedAndAvailPoints.entrySet()) {
                AssignmentType type = entry.getKey();
                MutablePair<Double, Double> values = entry.getValue();
                numerator += values.getLeft() / values.getRight() * assignmentTypeWeights.get(type);
                denominator += assignmentTypeWeights.get(type);
            }
            for(Map.Entry<Long, MutablePair<Double,Double>> entry : assignmentIdToPoints.entrySet()) {
                numerator += entry.getValue().getLeft() / entry.getValue().getRight() * assignmentWeights.get(entry.getKey());
                denominator += assignmentWeights.get(entry.getKey());
            }
        } else {
            //Straight average every assignment in the period.
            return GradeUtil.calculateAverageGrade(studentAssignments);
        }
        return numerator / denominator;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Set<GradeFormula> getChildren() {
        return children;
    }

    public void setChildren(Set<GradeFormula> children) {
        this.children = children;
    }

    public Map<AssignmentType, Double> getAssignmentTypeWeights() {
        return assignmentTypeWeights;
    }

    public void setAssignmentTypeWeights(Map<AssignmentType, Double> assignmentTypeWeights) {
        this.assignmentTypeWeights = assignmentTypeWeights;
    }

    public Map<Long, Double> getAssignmentWeights() {
        return assignmentWeights;
    }

    public void setAssignmentWeights(Map<Long, Double> assignmentWeights) {
        this.assignmentWeights = assignmentWeights;
    }

    public Map<Long, Double> getChildWeights() {
        return childWeights;
    }

    public void setChildWeights(Map<Long, Double> childWeights) {
        this.childWeights = childWeights;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, startDate, endDate, parentId, children, assignmentTypeWeights,
                assignmentWeights, childWeights);
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
        return Objects.equals(this.id, other.id)
                && Objects.equals(this.name, other.name)
                && Objects.equals(this.startDate, other.startDate)
                && Objects.equals(this.endDate, other.endDate)
                && Objects.equals(this.parentId, other.parentId)
                && Objects.equals(this.children, other.children)
                && Objects.equals(this.assignmentTypeWeights, other.assignmentTypeWeights)
                && Objects.equals(this.assignmentWeights, other.assignmentWeights)
                && Objects.equals(this.childWeights, other.childWeights);
    }
}
