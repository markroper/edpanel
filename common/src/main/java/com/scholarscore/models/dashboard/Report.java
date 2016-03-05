package com.scholarscore.models.dashboard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.models.ApiModel;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.query.Query;
import com.scholarscore.util.EdPanelObjectMapper;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Created by markroper on 2/15/16.
 */
@Entity(name = HibernateConsts.DASHBOARD_REPORT_TABLE)
public class Report extends ApiModel {
    //Required.  The query that returns the data constituting the chart
    @NotNull
    protected Query chartQuery;
    //Optional. When a user clicks on a report section, this is the query to execute to resolve the
    //Detailed results for display
    protected Query clickTableQuery;
    //Optional.  If the click through query is defined, this defines which columns in the result
    //set to map into the user-visible table of the detailed data
    protected List<ColumnDef> columnDefs;
    protected Long position;
    protected Long rowFk;
    protected Boolean supportDateFilter;
    protected Boolean supportDemographicFilter;
    protected ReportType type;

    public Report() {

    }

    public Report(Report r) {
        super(r);
        if(null != r.chartQuery) {
            this.chartQuery = new Query(r.chartQuery);
        }
        if(null != r.getClickTableQuery()) {
            this.clickTableQuery = new Query(r.getClickTableQuery());
        }
        this.rowFk = r.rowFk;
        this.columnDefs = r.getColumnDefs();
        this.position = r.getPosition();
        this.supportDateFilter = r.getSupportDateFilter();
        this.supportDemographicFilter = r.getSupportDemographicFilter();
        this.type = r.getType();
    }

    @Column(name = HibernateConsts.DASHBOARD_REPORT_TYPE)
    @Enumerated(EnumType.STRING)
    public ReportType getType() {
        return type;
    }

    public void setType(ReportType type) {
        this.type = type;
    }

    @Column(name = HibernateConsts.DASHBOARD_REPORT_SUPPORT_DATE_FILTER)
    public Boolean getSupportDateFilter() {
        return supportDateFilter;
    }

    public void setSupportDateFilter(Boolean supportDateFilter) {
        this.supportDateFilter = supportDateFilter;
    }

    @Column(name = HibernateConsts.DASHBOARD_REPORT_SUPPORT_DEMOGRAPHIC_FILTER)
    public Boolean getSupportDemographicFilter() {
        return supportDemographicFilter;
    }

    public void setSupportDemographicFilter(Boolean supportDemographicFilter) {
        this.supportDemographicFilter = supportDemographicFilter;
    }

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = HibernateConsts.DASHBOARD_REPORT_ID)
    public Long getId() {
        return super.getId();
    }

    @Override
    @Column(name = HibernateConsts.DASHBOARD_REPORT_NAME)
    public String getName() {
        return super.getName();
    }

    @OneToOne
    @JoinColumn(name=HibernateConsts.REPORT_FK, nullable = false)
    @Fetch(FetchMode.JOIN)
    @Cascade(CascadeType.ALL)
    public Query getChartQuery() {
        return chartQuery;
    }

    public void setChartQuery(Query chartQuery) {
        this.chartQuery = chartQuery;
    }

    @OneToOne
    @JoinColumn(name=HibernateConsts.DASHBOARD_REPORT_CLICK_REPORT_FK, nullable = true)
    @Fetch(FetchMode.JOIN)
    @Cascade(CascadeType.ALL)
    public Query getClickTableQuery() {
        return clickTableQuery;
    }

    public void setClickTableQuery(Query clickTableQuery) {
        this.clickTableQuery = clickTableQuery;
    }

    @Column(name = HibernateConsts.DASHBOARD_ROW_FK, nullable = false)
    @JsonIgnore
    public Long getRowFk() {
        return rowFk;
    }

    public void setRowFk(Long rowFk) {
        this.rowFk = rowFk;
    }

    @Transient
    public List<ColumnDef> getColumnDefs() {
        return columnDefs;
    }

    public void setColumnDefs(List<ColumnDef> columnDefs) {
        this.columnDefs = columnDefs;
    }

    @Column(name=HibernateConsts.DASHBOARD_REPORT_COLUMN_DEFS, columnDefinition="blob")
    @JsonIgnore
    public String getColumnDefsString() {
        try {
            return EdPanelObjectMapper.MAPPER.writeValueAsString(this.columnDefs);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public void setColumnDefsString(String defs) {
        try {
            this.columnDefs = EdPanelObjectMapper.MAPPER.readValue( defs, new TypeReference<List<ColumnDef>>(){});
        } catch (IOException e) {
            this.columnDefs = null;
        }
    }

    @Column(name = HibernateConsts.DASHBOARD_REPORT_POSITION)
    @JsonIgnore
    public Long getPosition() {
        return position;
    }

    public void setPosition(Long pos) {
        this.position = pos;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(chartQuery, clickTableQuery, columnDefs,
                position, rowFk, supportDateFilter, supportDemographicFilter, type);
    }

    @Override
    public boolean equals(Object obj) {
        if(!super.equals(obj)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final Report other = (Report) obj;
        return Objects.equals(this.chartQuery, other.chartQuery)
                && Objects.equals(this.clickTableQuery, other.clickTableQuery)
                && Objects.equals(this.columnDefs, other.columnDefs)
                && Objects.equals(this.rowFk, other.rowFk)
                && Objects.equals(this.supportDateFilter, other.supportDateFilter)
                && Objects.equals(this.supportDemographicFilter, other.supportDemographicFilter)
                && Objects.equals(this.position, other.position)
                && Objects.equals(this.type, other.type);
    }
}
