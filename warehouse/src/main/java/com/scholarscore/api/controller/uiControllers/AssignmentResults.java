package com.scholarscore.api.controller.uiControllers;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by markroper on 4/15/16.
 */
public class AssignmentResults {
    private Double min;
    private Double max;
    private Double quartile1;
    private Double quartile3;
    private Double median;
    private List<Long> assignmentIds;
    private Map<Long, String> schoolIdToName;
    private List<AssignmentResult> results;

    public void calculateAndSetQuartiles() {
        if(null != results) {
            Collections.sort(results, (AssignmentResult p1, AssignmentResult p2) -> p1.getScore().compareTo(p2.getScore()));
        }
        int medianPos = (results.size() - 1) / 2;
        median = results.get(medianPos).getScore();
        min = results.get(0).getScore();
        max = results.get(results.size() - 1).getScore();
        quartile1 = results.get(medianPos/2).getScore();
        quartile3 = results.get(medianPos + (medianPos/2)).getScore();
    }

    public List<Long> getAssignmentIds() {
        return assignmentIds;
    }

    public void setAssignmentIds(List<Long> assignmentIds) {
        this.assignmentIds = assignmentIds;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public Double getQuartile1() {
        return quartile1;
    }

    public void setQuartile1(Double quartile1) {
        this.quartile1 = quartile1;
    }

    public Double getQuartile3() {
        return quartile3;
    }

    public void setQuartile3(Double quartile2) {
        this.quartile3 = quartile2;
    }

    public Double getMedian() {
        return median;
    }

    public void setMedian(Double median) {
        this.median = median;
    }

    public Map<Long, String> getSchoolIdToName() {
        return schoolIdToName;
    }

    public void setSchoolIdToName(Map<Long, String> schoolIdToName) {
        this.schoolIdToName = schoolIdToName;
    }

    public List<AssignmentResult> getResults() {
        return results;
    }

    public void setResults(List<AssignmentResult> results) {
        this.results = results;
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, max, quartile1, quartile3, median, schoolIdToName, results, assignmentIds);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final AssignmentResults other = (AssignmentResults) obj;
        return Objects.equals(this.min, other.min)
                && Objects.equals(this.max, other.max)
                && Objects.equals(this.quartile1, other.quartile1)
                && Objects.equals(this.quartile3, other.quartile3)
                && Objects.equals(this.median, other.median)
                && Objects.equals(this.schoolIdToName, other.schoolIdToName)
                && Objects.equals(this.assignmentIds, other.assignmentIds)
                && Objects.equals(this.results, other.results);
    }
}
