package com.scholarscore.models.dashboard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.util.EdPanelObjectMapper;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by markroper on 4/15/16.
 */
@Entity(name = HibernateConsts.DASHBOARD_REPORT_TABLE)
@DiscriminatorValue("ASSIGNMENT_ANALYSIS")
public class ReportAssignmentAnalysis extends ReportBase {
    List<Long> assignmentIds;

    public ReportAssignmentAnalysis() {
        this.type = ReportType.ASSIGNMENT_ANALYSIS;
    }

    public ReportAssignmentAnalysis(ReportAssignmentAnalysis r) {
        super(r);
        this.assignmentIds = r.assignmentIds;
    }

    @Column(name = HibernateConsts.DASHBOARD_REPORT_ASSIGNMENT_IDS,  columnDefinition = "blob")
    @JsonIgnore
    public String getAssignmentIdString() {
        try {
            if(null == assignmentIds) {
                return null;
            } else {
                return EdPanelObjectMapper.MAPPER.writeValueAsString(assignmentIds);
            }
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @JsonIgnore
    public void setAssignmentIdString(String input) {
        try {
            if(null != assignmentIds) {
                this.assignmentIds = EdPanelObjectMapper.MAPPER.readValue(
                        input, new TypeReference<ArrayList<Long>>(){});
            }
        } catch (IOException e) {

        }
    }

    @Transient
    public List<Long> getAssignmentIds() {
        return assignmentIds;
    }

    @Transient
    public void setAssignmentIds(List<Long> assignmentIds) {
        this.assignmentIds = assignmentIds;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(assignmentIds);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final ReportAssignmentAnalysis other = (ReportAssignmentAnalysis) obj;
        return Objects.equals(this.assignmentIds, other.assignmentIds);
    }
}
