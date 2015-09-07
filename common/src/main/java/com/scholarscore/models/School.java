package com.scholarscore.models;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;

/**
 * The class represents a single school within a school district.
 * 
 * @author markroper
 *
 */
@Entity(name = "school")
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class School extends ApiModel implements Serializable, IApiModel<School>{
    
    List<SchoolYear> years;
    private String sourceSystemId;
    private String principalName;
    private String principalEmail;
    private Address address;
    private String mainPhone;
    
    public School() {
        super();
    }
    
    public School(School clone) {
        super(clone);
        this.years = clone.years;
        this.principalEmail = clone.principalEmail;
        this.principalName = clone.principalName;
        this.sourceSystemId = clone.sourceSystemId;
        this.address = clone.address;
        this.mainPhone = clone.mainPhone;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = ColumnConsts.SCHOOL_ID)
    public Long getId() {
        return super.getId();
    }

    @Column(name = ColumnConsts.SCHOOL_NAME)
    public String getName() { return super.getName(); }

    @Transient
    public List<SchoolYear> getYears() {
        return years;
    }

    public void setYears(List<SchoolYear> years) {
        this.years = years;
    }

    @Override
    public void mergePropertiesIfNull(School mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);
        if (mergeFrom == null) { return; }
        if(null == years) {
            this.years = mergeFrom.years;
        }
        if (null == sourceSystemId) {
            this.sourceSystemId = mergeFrom.sourceSystemId;
        }
        if (null == principalEmail) {
            this.principalEmail = mergeFrom.principalEmail;
        }
        if (null == principalName) {
            this.principalName = mergeFrom.principalName;
        }
        if (null == address) {
            this.address = mergeFrom.address;
        }
        if (null == mainPhone) {
            this.mainPhone = mergeFrom.mainPhone;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof School)) return false;
        if (!super.equals(o)) return false;

        School school = (School) o;

        if (getYears() != null ? !getYears().equals(school.getYears()) : school.getYears() != null) return false;
        if (getSourceSystemId() != null ? !getSourceSystemId().equals(school.getSourceSystemId()) : school.getSourceSystemId() != null)
            return false;
        if (getPrincipalName() != null ? !getPrincipalName().equals(school.getPrincipalName()) : school.getPrincipalName() != null)
            return false;
        if (getPrincipalEmail() != null ? !getPrincipalEmail().equals(school.getPrincipalEmail()) : school.getPrincipalEmail() != null)
            return false;
        if (getAddress() != null ? !getAddress().equals(school.getAddress()) : school.getAddress() != null)
            return false;
        return !(getMainPhone() != null ? !getMainPhone().equals(school.getMainPhone()) : school.getMainPhone() != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getYears() != null ? getYears().hashCode() : 0);
        result = 31 * result + (getSourceSystemId() != null ? getSourceSystemId().hashCode() : 0);
        result = 31 * result + (getPrincipalName() != null ? getPrincipalName().hashCode() : 0);
        result = 31 * result + (getPrincipalEmail() != null ? getPrincipalEmail().hashCode() : 0);
        result = 31 * result + (getAddress() != null ? getAddress().hashCode() : 0);
        result = 31 * result + (getMainPhone() != null ? getMainPhone().hashCode() : 0);
        return result;
    }

    @Column(name = ColumnConsts.SCHOOL_SOURCE_SYSTEM_ID)
    public String getSourceSystemId() {
        return sourceSystemId;
    }

    public void setSourceSystemId(String sourceSystemId) {
        this.sourceSystemId = sourceSystemId;
    }

    @Column(name = ColumnConsts.SCHOOL_PRINCIPAL_EMAIL)
    public String getPrincipalEmail() {
        return principalEmail;
    }

    public void setPrincipalEmail(String principalEmail) {
        this.principalEmail = principalEmail;
    }

    @Column(name = ColumnConsts.SCHOOL_PRINCIPAL_NAME)
    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    @Column(name = ColumnConsts.SCHOOL_MAIN_PHONE)
    public String getMainPhone() {
        return mainPhone;
    }

    public void setMainPhone(String mainPhone) {
        this.mainPhone = mainPhone;
    }


    @OneToOne(optional = true)
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name=ColumnConsts.SCHOOL_ADDRESS_FK)
    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
