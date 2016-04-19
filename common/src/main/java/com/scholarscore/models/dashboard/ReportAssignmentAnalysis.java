package com.scholarscore.models.dashboard;

import com.scholarscore.models.HibernateConsts;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Converter;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by markroper on 4/15/16.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("ASSIGNMENT_ANALYSIS")
public class ReportAssignmentAnalysis extends ReportBase {
    @Column(name = HibernateConsts.DASHBOARD_REPORT_ASSIGNMENT_IDS,  columnDefinition = "blob")
    protected List<Long> assignmentIds;
//    String assignmentIdsString;

    public ReportAssignmentAnalysis() {
        this.type = ReportType.ASSIGNMENT_ANALYSIS;
    }

    public ReportAssignmentAnalysis(ReportAssignmentAnalysis r) {
        super(r);
        this.assignmentIds = r.assignmentIds;
    }

    @Override
    @Column(name = HibernateConsts.DASHBOARD_REPORT_NAME)
    public String getName() {
        return super.getName();
    }

//    @JsonIgnore
//    @Column(name = HibernateConsts.DASHBOARD_REPORT_ASSIGNMENT_IDS,  columnDefinition = "blob")
//    public String getAssignmentIdsString() {
//        try {
//            if(null == assignmentIds) {
//                return null;
//            } else {
//                return EdPanelObjectMapper.MAPPER.writeValueAsString(assignmentIds);
//            }
//        } catch (JsonProcessingException | NullPointerException e) {
//            return null;
//        }
//    }

//    @JsonIgnore
//    public void setAssignmentIdsString(String input) {
//        try {
//            if(null != assignmentIds) {
//                this.assignmentIds = EdPanelObjectMapper.MAPPER.readValue(
//                        input, new TypeReference<ArrayList<Long>>(){});
//            } else {
//                assignmentIds = null;
//            }
//        } catch (IOException | NullPointerException e) {
//            e.printStackTrace();
//        }
//    }
//

    @Column(name = HibernateConsts.DASHBOARD_REPORT_ASSIGNMENT_IDS,  columnDefinition = "blob")
    @Convert(converter = ReportAssignmentAnalysis.LongArrayToStringConverter.class)
    public List<Long> getAssignmentIds() {
        return assignmentIds;
    }

    public void setAssignmentIds(List<Long> assignmentIds) {
        this.assignmentIds = assignmentIds;
    }

    @Converter
    public static class LongArrayToStringConverter implements AttributeConverter<List<Long>,String> {
        public LongArrayToStringConverter() {

        }
        @Override
        public String convertToDatabaseColumn(List<Long> attribute) {
            return attribute == null ? null : StringUtils.join(attribute, ",");
        }

        @Override
        public List<Long> convertToEntityAttribute(String dbData) {
            if (StringUtils.isBlank(dbData))
                return new ArrayList<>();

            try (Stream<String> stream = Arrays.stream(dbData.split(","))) {
                return stream.map(Long::parseLong).collect(Collectors.toList());
            }
        }
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
