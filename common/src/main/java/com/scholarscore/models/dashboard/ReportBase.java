package com.scholarscore.models.dashboard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.scholarscore.models.ApiModel;
import com.scholarscore.models.HibernateConsts;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.util.Objects;

/**
 * Created by markroper on 4/15/16.
 */
@Entity(name = HibernateConsts.DASHBOARD_REPORT_TABLE)
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name="discriminator",
        discriminatorType= DiscriminatorType.STRING
)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Report.class, name="BAR"),
        @JsonSubTypes.Type(value = Report.class, name = "SPLINE"),
        @JsonSubTypes.Type(value = Report.class, name = "PIE"),
        @JsonSubTypes.Type(value = ReportAssignmentAnalysis.class, name = "ASSIGNMENT_ANALYSIS")
})
public abstract class ReportBase extends ApiModel {
    protected Long rowFk;
    protected ReportType type;
    protected Long position;

    public ReportBase() {
    }

    public ReportBase(ReportBase b) {
        super(b);
        this.rowFk = b.rowFk;
        this.position = b.position;
        this.type = b.getType();
    }

    @Column(name = HibernateConsts.DASHBOARD_REPORT_POSITION)
    @JsonIgnore
    public Long getPosition() {
        return position;
    }

    public void setPosition(Long pos) {
        this.position = pos;
    }

    @Column(name = HibernateConsts.DASHBOARD_ROW_FK, nullable = false)
    @JsonIgnore
    public Long getRowFk() {
        return rowFk;
    }

    public void setRowFk(Long rowFk) {
        this.rowFk = rowFk;
    }

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = HibernateConsts.DASHBOARD_REPORT_ID)
    public Long getId() {
        return super.getId();
    }

    @Column(name = HibernateConsts.DASHBOARD_REPORT_TYPE)
    @Enumerated(EnumType.STRING)
    public ReportType getType() {
        return type;
    }

    public void setType(ReportType type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(rowFk, type, position);
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
        final ReportBase other = (ReportBase) obj;
        return Objects.equals(this.rowFk, other.rowFk)
                && Objects.equals(this.position, other.position)
                && Objects.equals(this.type, other.type);
    }
}
