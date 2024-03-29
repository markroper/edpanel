package com.scholarscore.models.dashboard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.scholarscore.models.HibernateConsts;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by markroper on 2/15/16.
 */
@Entity(name = HibernateConsts.DASHBOARD_ROW_TABLE)
public class DashboardRow implements Serializable {
    protected Long id;
    protected Long dashboardFk;
    protected Long position;
    @Size(min = 1, max = 3)
    protected List<ReportBase> reports;

    public DashboardRow() {

    }

    @SuppressWarnings("unchecked")
    public DashboardRow(DashboardRow r) {
        this.id = r.id;
        this.dashboardFk = r.dashboardFk;
        this.position = r.position;
        if(null != r.getReports()) {
            this.reports = new ArrayList<>();
            for(ReportBase rpt: r.getReports()) {
                if(rpt instanceof Report) {
                    this.reports.add(new Report((Report)rpt));
                } else if(rpt instanceof ReportAssignmentAnalysis) {
                    this.reports.add(new ReportAssignmentAnalysis((ReportAssignmentAnalysis)rpt));
                }
            }
        }
    }

    @OneToMany
    @JoinColumn(name=HibernateConsts.DASHBOARD_ROW_FK, referencedColumnName=HibernateConsts.DASHBOARD_ROW_ID)
    @Fetch(FetchMode.JOIN)
    @Cascade(CascadeType.ALL)
    @OrderColumn(name = HibernateConsts.DASHBOARD_REPORT_POSITION)
    public List<ReportBase> getReports() {
        return reports;
    }

    public void setReports(List<ReportBase> reports) {
        this.reports = reports;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = HibernateConsts.DASHBOARD_ROW_ID)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name=HibernateConsts.DASHBOARD_ROW_POSITION)
    @JsonIgnore
    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    @Column(name = HibernateConsts.DASHBOARD_FK)
    @JsonIgnore
    public Long getDashboardFk() {
        return dashboardFk;
    }

    public void setDashboardFk(Long dashboardFk) {
        this.dashboardFk = dashboardFk;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, position, reports, dashboardFk);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final DashboardRow other = (DashboardRow) obj;
        return Objects.equals(this.id, other.id)
                && Objects.equals(this.position, other.position)
                && Objects.equals(this.dashboardFk, other.dashboardFk)
                && Objects.equals(this.reports, other.reports);
    }

    @Override
    public String toString() {
        return "DashboardRow{ (" + super.toString() + ") " +
                "id=" + id +
                ", dashboardFk=" + dashboardFk +
                ", position=" + position +
                ", reports=" + reports +
                '}';
    }
}
