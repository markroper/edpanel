package com.scholarscore.models.dashboard;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.ApiModel;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by markroper on 2/15/16.
 */
@Entity(name = HibernateConsts.DASHBOARD_TABLE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Dashboard extends ApiModel {
    @NotNull
    protected Long schoolId;
    protected Long userId;
    @Size(min = 0, max = 5)
    protected List<DashboardRow> rows;

    public Dashboard() {

    }

    public Dashboard(Dashboard d) {
        this.schoolId = d.schoolId;
        this.userId = d.userId;
        if(null != d.getRows()) {
            this.rows = new ArrayList<>();
            for(DashboardRow r: d.getRows()) {
                this.rows.add(new DashboardRow(r));
            }
        }

    }
    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = HibernateConsts.DASHBOARD_ID)
    public Long getId() {
        return super.getId();
    }

    @Override
    @Column(name = HibernateConsts.DASHBOARD_NAME)
    public String getName() {
        return super.getName();
    }

    @Column(name = HibernateConsts.SCHOOL_FK)
    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    @Column(name = HibernateConsts.USER_FK)
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @OneToMany
    @JoinColumn(name=HibernateConsts.DASHBOARD_FK, referencedColumnName=HibernateConsts.DASHBOARD_ID)
    @Fetch(FetchMode.JOIN)
    @Cascade(CascadeType.ALL)
    @OrderColumn(name=HibernateConsts.DASHBOARD_ROW_POSITION)
    public List<DashboardRow> getRows() {
        return rows;
    }

    public void setRows(List<DashboardRow> rows) {
        this.rows = rows;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(schoolId, userId, rows);
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
        final Dashboard other = (Dashboard) obj;
        return Objects.equals(this.schoolId, other.schoolId)
                && Objects.equals(this.userId, other.userId)
                && Objects.equals(this.rows, other.rows);
    }
}
