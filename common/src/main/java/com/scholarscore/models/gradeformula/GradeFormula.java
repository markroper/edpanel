package com.scholarscore.models.gradeformula;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.assignment.AssignmentType;
import com.scholarscore.models.assignment.StudentAssignment;
import org.apache.commons.lang3.tuple.MutablePair;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * PowerSchool breaks the logical concept of a Grade Formula into three objects, each of which
 * has a one to many relationship to the next. GradeSetup < GradeFormula < GradeFormulaWeighting.
 * All three of these concepts are expressed in this class.
 *
 * In PowerSchool, GradeSetup can be a rooted graph, as in the following example:
 *
 *                          school year
 *                         /           \
 *                   semester 1     semester 2
 *                  /          \   /          \
 *                 Q1         Q2  Q3          Q4
 *
 * Each of these grade setups may have its own GradeFormula, which in turn have their own weights.  A setup
 * does not need to have a formula, and in these cases a strait sum(awarded_points)/sum(available_points) formula
 * is used to resolve a grade for a section over a reporting period.
 *
 * In the case where a grade formula is supplied, weightings can be specified by either assignment type,
 * asignment ID, or child grade formula scores.
 *
 * Created by markroper on 11/9/15.
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GradeFormula implements Serializable {
    //Will be set to the source system grade formula reporting term ID
    Long id;
    //Set to the grade setup name
    String name;
    String sourceSystemDescription;
    //Start date of the reporting term
    Date startDate;
    //End date of the reporting term
    Date endDate;
    //Parent grade formula (e.g. a Quarter's grade formula may have a parent semester grade formula)
    Long parentId;
    //TODO: migrated but not yet in use in calculateGrade(..)
    Long lowScoreToDiscard;
    Set<GradeFormula> children = new HashSet<>();
    //User defined type string to weighting
    Map<String, Double> assignmentTypeWeights = new HashMap<>();
    Map<String, Double> assignmentTypeDefaultPoints = new HashMap<>();
    //keyed on sourceSystemId
    Map<Long, Double> assignmentWeights = new HashMap<>();
    Map<Long, Double> childWeights = new HashMap<>();

    public GradeFormula() {

    }
    public GradeFormula(Map<String, Double> weight) {
        this.assignmentTypeWeights = weight;
    }

    /**
     * Ok, here goes.  Exempt any assignments outside the startDate and endDate bounds for the formula.
     * Also exempt any assignments marked as 'exempt' from calculations, regardless of formula type.
     * Ensure that if a students score is below the assignment minmum default score that the minimum default
     * score is used.
     * <p/>
     * 1) If the GradeFormula instance has assignmentTypeWeights and/or assignmentWeights, use those to
     * calculate the grade.  If there is a specific assignment weight, use that in the calculation, but
     * exempts the assignment in question from inclusion in its assignment type bucket.
     * <p/>
     * 2) If the GradeFormula has childWieghts, calculate the grade for each child according to its
     * formula, and create a weighted average from those children according to the weighting.
     * <p/>
     * 3) If the GradeFormula instance has no weightings, but does have children, straight average output
     * of the child GradeFormula's grades.
     * <p/>
     * 4) If the GradeFormula instance has no weightings and no children, calculate the grade by the
     * following formuls:
     * sum(awarded points * weight)/sum(available points * weight)
     *
     * @param studentAssignments
     * @return
     */
    public Double calculateGrade(Set<StudentAssignment> studentAssignments) {
        Double numerator = 0D;
        Double denominator = 0D;
        //TODO: what if there are both childWeights and assignmentTypeWeights?
        if(!assignmentTypeWeights.isEmpty() || !assignmentWeights.isEmpty()) {
            AssignmentAndTypeGrades assignmentAndTypeGrades = this.calculateAssignmentTypeGrades(studentAssignments);
            //Now that the buckets' awarded and available points are calculated, perform the bucket weighting
            for(Map.Entry<String, MutablePair<Double, Double>> entry : assignmentAndTypeGrades.getTypeToAwardedAndAvailPoints().entrySet()) {
                String type = entry.getKey();
                MutablePair<Double, Double> values = entry.getValue();
                numerator += values.getLeft() / values.getRight() * assignmentTypeWeights.get(type);
                denominator += assignmentTypeWeights.get(type);
            }
            for(Map.Entry<Long, MutablePair<Double,Double>> entry : assignmentAndTypeGrades.getAssignmentIdToPoints().entrySet()) {
                numerator += entry.getValue().getLeft() / entry.getValue().getRight() * assignmentWeights.get(entry.getKey());
                denominator += assignmentWeights.get(entry.getKey());
            }
        } else if(!children.isEmpty()) {
            //If there are child formulas & we have weights defined, delegate to those child formulas
            //And calculate the grade based ona weighted average of those.
            boolean weightChildren = !childWeights.isEmpty();
            for(GradeFormula child: children) {
                if(weightChildren) {
                    //There are child weights, only include weighted children in the calculation, and include the
                    //weight for those children in the calculation
                    if (childWeights.containsKey(child.getId())) {
                        numerator += child.calculateGrade(studentAssignments) * childWeights.get(child.getId());
                        denominator += childWeights.get(child.getId());
                    }
                } else {
                    //There are children, but no weighting for them.  Straight average the children scores.
                    numerator += child.calculateGrade(studentAssignments);
                    denominator += 1D;
                }
            }
        } else {
            //Straight average every assignment in the period.
            //TODO: replace me with a calculation that respects startDate & endDate
            //return GradeUtil.calculateAverageGrade(studentAssignments);
            return straightAverageAllAssignmentsRespectingAssignmentWeights(studentAssignments);
        }
        if(null != denominator && !denominator.equals(0D)) {
            return numerator / denominator;
        }
        return 1D;
    }

    public Map<String, Double> calculateCategoryGrades(Set<StudentAssignment> studentAssignments) {
        Double numerator = 0D;
        Double denominator = 0D;
        AssignmentAndTypeGrades assignmentAndTypeGrades = this.calculateAssignmentTypeGrades(studentAssignments);
        //Now that the buckets' awarded and available points are calculated, perform the bucket weighting
        Map<String, Double> typeToGrade = new HashMap<>();
        for(Map.Entry<String, Double> entry : assignmentTypeWeights.entrySet()) {
            MutablePair<Double, Double> typePoints = assignmentAndTypeGrades.getTypeToAwardedAndAvailPoints().get(entry.getKey());
            if(null == typePoints) {
                typeToGrade.put(entry.getKey(), null);
            } else {
                typeToGrade.put(
                        entry.getKey(),
                        (double)(long)(typePoints.getLeft() /  typePoints.getRight() * 100D));
            }
        }
        return typeToGrade;
    }

    private AssignmentAndTypeGrades calculateAssignmentTypeGrades(Set<StudentAssignment> studentAssignments) {
        //TODO: what if there are both childWeights and assignmentTypeWeights?
        if(!assignmentTypeWeights.isEmpty() || !assignmentWeights.isEmpty()) {
            //There are weightings defined for assignments or assignment types, so we use those!
            Map<String, MutablePair<Double, Double>> typeToAwardedAndAvailPoints = new HashMap<>();
            Map<Long, MutablePair<Double, Double>> assignmentIdToPoints = new HashMap<>();
            for(StudentAssignment sa : studentAssignments) {
                Date dueDate = sa.getAssignment().getDueDate();
                //If the assignment is within the term:
                if((null == startDate || dueDate.compareTo(startDate) >= 0 || new Long(0L).equals(getDateDiff(startDate, dueDate, TimeUnit.DAYS)))
                        && (null == endDate || dueDate.compareTo(endDate) <= 0 || new Long(0L).equals(getDateDiff(endDate, dueDate, TimeUnit.DAYS)))) {
                    //If there is a weight for the particular assignment, calculate the values and move on
                    //to the next assignment:
                    if(assignmentWeights.containsKey(sa.getAssignment().getId())) {
                        Double weight = sa.getAssignment().getWeight();
                        assignmentIdToPoints.put(sa.getAssignment().getId(), new MutablePair<>(
                                sa.getAwardedPoints() * weight,
                                sa.getAssignment().getAvailablePoints() * weight));
                        continue;
                    }

                    String type = sa.getAssignment().getUserDefinedType();
                    if(null == type) {
                        type = sa.getAssignment().getType().name();
                    }
                    //Don't count assignments that are not being included in the grade
                    if(!assignmentTypeWeights.containsKey(type)) {
                        continue;
                    }
                    //Don't count exempt assignments
                    if((null != sa.getExempt() && sa.getExempt()) ||
                            (null != sa.getAssignment().getIncludeInFinalGrades() && !sa.getAssignment().getIncludeInFinalGrades())) {
                        continue;
                    }
                    //CALCULATE AND INCREMENT AWARDED POINTS
                    Double awardedPoints = sa.getAwardedPoints();
                    if(type.equals(AssignmentType.ATTENDANCE)) {
                        awardedPoints = sa.getAvailablePoints().doubleValue();
                    }
                    //Assignments that are not exempted, are included in the section grade calculation,
                    //but have a null awarded points should have full points. So as not to penalize the student?
                    if(null == awardedPoints) {
                        awardedPoints = 0D;
                    }
                    if(!typeToAwardedAndAvailPoints.containsKey(type)) {
                        typeToAwardedAndAvailPoints.put(type, new MutablePair<Double, Double>(0D, 0D));
                    }
                    //If there is a default point value for the category and the awarded points is less than it,
                    //ise the default points
                    Double defaultPoints = assignmentTypeDefaultPoints.get(type);
                    if(null != defaultPoints &&
                            awardedPoints < defaultPoints) {
                        awardedPoints = defaultPoints;
                    }
                    //Multiply by the weight!
                    if(null != sa.getAssignment().getWeight()) {
                        awardedPoints = awardedPoints * sa.getAssignment().getWeight();
                    }
                    Double left = typeToAwardedAndAvailPoints.get(type).getLeft();

                    //CALCULATE AND INCREMENT AVAILABLE POINTS
                    Double availablePoints = sa.getAssignment().getAvailablePoints().doubleValue();
                    if(null == availablePoints) {
                        continue;
                    }
                    if(null != sa.getAssignment().getWeight()) {
                        availablePoints = availablePoints * sa.getAssignment().getWeight();
                    }
                    //Update numerator and denominator!
                    typeToAwardedAndAvailPoints.get(type).setLeft(left + awardedPoints);
                    Double right = typeToAwardedAndAvailPoints.get(type).getRight();
                    typeToAwardedAndAvailPoints.get(type).setRight(right + availablePoints);
                }
            }
            return new AssignmentAndTypeGrades(typeToAwardedAndAvailPoints, assignmentIdToPoints);
        }
        return new AssignmentAndTypeGrades();
    }

    private Double straightAverageAllAssignmentsRespectingAssignmentWeights(Set<StudentAssignment> studentAssignments) {
        Double numerator = 0D;
        Double denominator = 0D;
        for(StudentAssignment sa : studentAssignments) {
            Date dueDate = sa.getAssignment().getDueDate();
            //If the assignment is within the term:
            if ((dueDate.compareTo(startDate) >= 0 || new Long(0L).equals(getDateDiff(startDate, dueDate, TimeUnit.DAYS)))
                    && (dueDate.compareTo(endDate) <= 0 || new Long(0L).equals(getDateDiff(endDate, dueDate, TimeUnit.DAYS)))) {
                //Don't count exempt assignments
                if ((null != sa.getExempt() && sa.getExempt()) ||
                        (null != sa.getAssignment().getIncludeInFinalGrades() && !sa.getAssignment().getIncludeInFinalGrades())) {
                    continue;
                }
                Double awardedPoints = sa.getAwardedPoints();
                //Assignments that are not exempted, are included in the section grade calculation,
                //but have a null awarded points should have full points. So as not to penalize the student?
                if (null == awardedPoints) {
                    continue;
                    //awardedPoints = sa.getAvailablePoints().doubleValue();
                }
                //Multiply by the weight!
                if (null != sa.getAssignment().getWeight()) {
                    awardedPoints = awardedPoints * sa.getAssignment().getWeight();
                }
                numerator += awardedPoints;
                Double availablePoints = sa.getAssignment().getAvailablePoints().doubleValue();
                if (null != sa.getAssignment().getWeight()) {
                    availablePoints = availablePoints * sa.getAssignment().getWeight();
                }
                denominator += availablePoints;
            }
        }
        if(null != denominator && !denominator.equals(0D)) {
            return numerator / denominator;
        }
        return 1D;
    }

    /**
     * Depth first recursive search to find the formula matching the reporting dates requested.
     * Will throw NPE if null startDate or endDate are provided as arguments
     * @param startDate Non null date
     * @param endDate   Non-null date
     * @return GradeFormula matching the start and end date or else null
     */
    public GradeFormula resolveFormulaMatchingDates(Date startDate, Date endDate) {
        //Soft match the dates since terms and reporting terms as migrated may be off by some number of minutes or days
        if(null != this.startDate && getDateDiff(this.startDate, startDate, TimeUnit.DAYS) < 8 &&
                null != this.endDate && getDateDiff(this.endDate, endDate, TimeUnit.DAYS) < 8) {
            return this;
        }
        for(GradeFormula formula: children) {
            GradeFormula potential = formula.resolveFormulaMatchingDates(startDate, endDate);
            if(null != potential) {
                return potential;
            }
        }
        return null;
    }

    private static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = Math.abs(date2.getTime() - date1.getTime());
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
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

    public Map<String, Double> getAssignmentTypeWeights() {
        return assignmentTypeWeights;
    }

    public void setAssignmentTypeWeights(Map<String, Double> assignmentTypeWeights) {
        this.assignmentTypeWeights = assignmentTypeWeights;
    }

    public Map<Long, Double> getAssignmentWeights() {
        return assignmentWeights;
    }

    public void setAssignmentWeights(Map<Long, Double> assignmentWeights) {
        this.assignmentWeights = assignmentWeights;
    }

    public String getSourceSystemDescription() {
        return sourceSystemDescription;
    }

    public void setSourceSystemDescription(String sourceSystemDescription) {
        this.sourceSystemDescription = sourceSystemDescription;
    }

    public Map<Long, Double> getChildWeights() {
        return childWeights;
    }

    public void setChildWeights(Map<Long, Double> childWeights) {
        this.childWeights = childWeights;
    }

    public Long getLowScoreToDiscard() {
        return lowScoreToDiscard;
    }

    public void setLowScoreToDiscard(Long lowScoreToDiscard) {
        this.lowScoreToDiscard = lowScoreToDiscard;
    }

    public Map<String, Double> getAssignmentTypeDefaultPoints() {
        return assignmentTypeDefaultPoints;
    }

    public void setAssignmentTypeDefaultPoints(Map<String, Double> assignmentTypeDefaultPoints) {
        this.assignmentTypeDefaultPoints = assignmentTypeDefaultPoints;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, startDate, endDate, parentId, children,
                assignmentTypeWeights, assignmentWeights, childWeights, sourceSystemDescription,
                lowScoreToDiscard, assignmentTypeDefaultPoints);
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
                && Objects.equals(this.sourceSystemDescription, other.sourceSystemDescription)
                && Objects.equals(this.assignmentTypeWeights, other.assignmentTypeWeights)
                && Objects.equals(this.assignmentWeights, other.assignmentWeights)
                && Objects.equals(this.lowScoreToDiscard, other.lowScoreToDiscard)
                && Objects.equals(this.assignmentTypeDefaultPoints, other.assignmentTypeDefaultPoints)
                && Objects.equals(this.childWeights, other.childWeights);
    }
}
