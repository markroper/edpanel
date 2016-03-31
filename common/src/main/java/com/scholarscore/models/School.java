package com.scholarscore.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

/**
 * The class represents a single school within a school district.
 * 
 * @author markroper
 *
 */
@Entity(name = "school")
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class School extends ApiModel implements Serializable, IApiModel<School> {
    private List<SchoolYear> years;
    private String sourceSystemId;
    private Long number;
    private String principalName;
    private String principalEmail;
    private Address address;
    private String mainPhone;
    private Boolean disableGpa;
    private Boolean disableBehavior;
    
    public School() {
        super();
        years = Lists.newArrayList();
    }
    
    public School(School clone) {
        super(clone);
        this.years = clone.years;
        this.principalEmail = clone.principalEmail;
        this.principalName = clone.principalName;
        this.sourceSystemId = clone.sourceSystemId;
        this.address = clone.address;
        this.mainPhone = clone.mainPhone;
        this.number = clone.number;
        this.disableBehavior = clone.disableBehavior;
        this.disableGpa = clone.disableGpa;
    }

    @Column(name = HibernateConsts.SCHOOL_DISABLE_GPA)
    public Boolean getDisableGpa() {
        return disableGpa;
    }

    public void setDisableGpa(Boolean disableGpa) {
        this.disableGpa = disableGpa;
    }

    @Column(name = HibernateConsts.SCHOOL_DISABLE_BEHAVIOR)
    public Boolean getDisableBehavior() {
        return disableBehavior;
    }

    public void setDisableBehavior(Boolean disableBehavior) {
        this.disableBehavior = disableBehavior;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = HibernateConsts.SCHOOL_ID)
    public Long getId() {
        return super.getId();
    }

    @Column(name = HibernateConsts.SCHOOL_NAME)
    public String getName() { return super.getName(); }

    @Transient
    public List<SchoolYear> getYears() {
        return years;
    }

    public void setYears(List<SchoolYear> years) {
        this.years = years;
    }

    public void addYear(SchoolYear year){
        years.add(year);
    }

    @Column(name = HibernateConsts.SCHOOL_NUMBER)
    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
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
        if (null == number) {
            this.number = mergeFrom.number;
        }
        if (null == disableGpa) {
            this.disableGpa = mergeFrom.disableGpa;
        }
        if (null == disableBehavior) {
            this.disableBehavior = mergeFrom.disableBehavior;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        School school = (School) o;

        if (years != null ? !years.equals(school.years) : school.years != null) return false;
        if (sourceSystemId != null ? !sourceSystemId.equals(school.sourceSystemId) : school.sourceSystemId != null)
            return false;
        if (number != null ? !number.equals(school.number) : school.number != null) return false;
        if (principalName != null ? !principalName.equals(school.principalName) : school.principalName != null)
            return false;
        if (principalEmail != null ? !principalEmail.equals(school.principalEmail) : school.principalEmail != null)
            return false;
        if (address != null ? !address.equals(school.address) : school.address != null) return false;
        if (mainPhone != null ? !mainPhone.equals(school.mainPhone) : school.mainPhone != null) return false;
        if (disableGpa != null ? !disableGpa.equals(school.disableGpa) : school.disableGpa != null) return false;
        return !(disableBehavior != null ? !disableBehavior.equals(school.disableBehavior) : school.disableBehavior != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (years != null ? years.hashCode() : 0);
        result = 31 * result + (sourceSystemId != null ? sourceSystemId.hashCode() : 0);
        result = 31 * result + (number != null ? number.hashCode() : 0);
        result = 31 * result + (principalName != null ? principalName.hashCode() : 0);
        result = 31 * result + (principalEmail != null ? principalEmail.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (mainPhone != null ? mainPhone.hashCode() : 0);
        result = 31 * result + (disableGpa != null ? disableGpa.hashCode() : 0);
        result = 31 * result + (disableBehavior != null ? disableBehavior.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "School{" +
                "years=" + years +
                ", sourceSystemId='" + sourceSystemId + '\'' +
                ", number=" + number +
                ", principalName='" + principalName + '\'' +
                ", principalEmail='" + principalEmail + '\'' +
                ", address=" + address +
                ", mainPhone='" + mainPhone + '\'' +
                ", disableGpa=" + disableGpa +
                ", disableBehavior=" + disableBehavior +
                '}';
    }

    @Column(name = HibernateConsts.SCHOOL_SOURCE_SYSTEM_ID)
    public String getSourceSystemId() {
        return sourceSystemId;
    }

    public void setSourceSystemId(String sourceSystemId) {
        this.sourceSystemId = sourceSystemId;
    }

    @Column(name = HibernateConsts.SCHOOL_PRINCIPAL_EMAIL)
    public String getPrincipalEmail() {
        return principalEmail;
    }

    public void setPrincipalEmail(String principalEmail) {
        this.principalEmail = principalEmail;
    }

    @Column(name = HibernateConsts.SCHOOL_PRINCIPAL_NAME)
    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    @Column(name = HibernateConsts.SCHOOL_MAIN_PHONE)
    public String getMainPhone() {
        return mainPhone;
    }

    public void setMainPhone(String mainPhone) {
        this.mainPhone = mainPhone;
    }


    @OneToOne(optional = true)
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name=HibernateConsts.SCHOOL_ADDRESS_FK)
    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    /**
     * Each class's Builder holds a copy of each attribute that the parent POJO has. We build up these properties using
     * a pattern of with[Attribute](Attribute attribute) and return the same instance of the Builder so that one can easily
     * chain setting attributes together.
     */
    public static class SchoolBuilder extends ApiModelBuilder<SchoolBuilder, School> {

        private List<SchoolYear> years;
        private String sourceSystemId;
        private String principalName;
        private String principalEmail;
        private Address address;
        private String mainPhone;
        private Long number;
        private Boolean disableGpa;
        private Boolean disableBehavior;


        public SchoolBuilder(){
            years = Lists.newArrayList();
        }

        public SchoolBuilder withYear(final SchoolYear year){
            years.add(year);
            return this;
        }
        public SchoolBuilder withDisableGpa(final Boolean disGpa){
            this.disableGpa = disGpa;
            return this;
        }
        public SchoolBuilder withDisableBehavior(final Boolean disBeh){
            this.disableBehavior = disBeh;
            return this;
        }
        public SchoolBuilder withNumber(final Long number){
            this.number = number;
            return this;
        }

        public SchoolBuilder withYears(final List<SchoolYear> years){
            this.years.addAll(years);
            return this;
        }

        public SchoolBuilder withSourceSystemId(final String sourceSystemId){
            this.sourceSystemId = sourceSystemId;
            return this;
        }

        public SchoolBuilder withPrincipalName(final String principalName){
            this.principalName = principalName;
            return this;
        }

        public SchoolBuilder withPrincipalEmail(final String principalEmail){
            this.principalEmail = principalEmail;
            return this;
        }

        public SchoolBuilder withAddress(final Address address){
            this.address = address;
            return this;
        }

        public SchoolBuilder withMainPhone(final String mainPhone){
            this.mainPhone = mainPhone;
            return this;
        }

        public School build(){
            School school = super.build();
            //make sure this is a reciprocal relationship
            school.setYears(years);
            for(SchoolYear year : years){
                year.setSchool(school);
            }
            school.setSourceSystemId(sourceSystemId);
            school.setPrincipalName(principalName);
            school.setPrincipalEmail(principalEmail);
            school.setNumber(number);
            school.setAddress(address);
            school.setMainPhone(mainPhone);
            school.setDisableBehavior(disableBehavior);
            school.setDisableGpa(disableGpa);
            return school;
        }

        @Override
        protected SchoolBuilder me() {
            return this;
        }

        @Override
        public School getInstance() {
            return new School();
        }
    }
}
