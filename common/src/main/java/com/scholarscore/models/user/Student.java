package com.scholarscore.models.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.Address;
import com.scholarscore.models.Gender;
import com.scholarscore.models.HibernateConsts;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;
import javax.persistence.Transient;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * The student class expresses a single student with a unique ID per school district.
 * 
 * Every student has a unique ID within a school district, but also may have unique IDs 
 * within a state (e.g. SSID) or a country (e.g. SSN).
 * 
 * @author markroper
 *
 */
@Entity(name = HibernateConsts.STUDENT_TABLE)
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
@PrimaryKeyJoinColumn(name=HibernateConsts.STUDENT_USER_FK, referencedColumnName = HibernateConsts.USER_ID)
public class Student extends User implements Serializable {
    //Source system identifier. E.g. powerschool ID
    private String sourceSystemId;
    //Addresses
    private Address mailingAddress;
    private Address homeAddress;
    //Demographics
    private Gender gender;
    private Date birthDate;
    private Date districtEntryDate;
    private Long projectedGraduationYear;
    private String socialSecurityNumber;
    //EthnicityRace
    private String federalRace;
    private String federalEthnicity;
    private Long currentSchoolId;
    private Long userId;
    
    public Student() {
        
    }
    
    public Student(Student student) {
        super(student);
        this.userId = student.userId;
        this.sourceSystemId = student.sourceSystemId;
        this.mailingAddress = student.mailingAddress;
        this.homeAddress = student.homeAddress;
        this.gender = student.gender;
        this.birthDate = student.birthDate;
        this.districtEntryDate = student.districtEntryDate;
        this.projectedGraduationYear = student.projectedGraduationYear;
        this.socialSecurityNumber = student.socialSecurityNumber;
        this.federalRace = student.federalRace;
        this.federalEthnicity = student.federalEthnicity;
        this.currentSchoolId = student.currentSchoolId;
    }
    
    public Student(String race, String ethnicity, Long currentSchoolId, Gender gender, String name, Long expectedGraduationYear) {
        this.federalRace = race;
        this.federalEthnicity = ethnicity;
        this.currentSchoolId = currentSchoolId;
        this.gender = gender;
        this.name = name;
        this.projectedGraduationYear = expectedGraduationYear;
    }
    
    @Override
    public void mergePropertiesIfNull(User mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);     
        if(mergeFrom instanceof Student) {
            Student merge = (Student) mergeFrom;
            if (null == getSourceSystemId()) {
                setSourceSystemId(merge.getSourceSystemId());
            }
            if (null == getMailingAddress()) {
                setMailingAddress(merge.getMailingAddress());
            }
            if (null == getHomeAddress()) {
                setHomeAddress(merge.getHomeAddress());
            }
            if (null == getGender()) {
                setGender(merge.getGender());
            }
            if (null == getBirthDate()) {
                setBirthDate(merge.getBirthDate());
            }
            if (null == getDistrictEntryDate()) {
                setDistrictEntryDate(merge.getDistrictEntryDate());
            }
            if (null == getProjectedGraduationYear()) {
                setProjectedGraduationYear(merge.getProjectedGraduationYear());
            }
            if (null == getSocialSecurityNumber()) {
                setSocialSecurityNumber(merge.getSocialSecurityNumber());
            }
            if (null == getFederalRace()) {
                setFederalRace(merge.getFederalRace());
            }
            if (null == getFederalEthnicity()) {
                setFederalEthnicity(merge.getFederalEthnicity());
            }
            if(null == getCurrentSchoolId()) {
                setCurrentSchoolId(merge.getCurrentSchoolId());
            }
            if(null == getUserId()) {
                setUserId(merge.getUserId());
            }
        }
    }

    @Column(name = HibernateConsts.STUDENT_SOURCE_SYSTEM_ID)
    public String getSourceSystemId() {
        return sourceSystemId;
    }

    @Column(name = HibernateConsts.STUDENT_NAME)
    public String getName() {
        return super.getName();
    }

    @OneToOne(optional = true)
    @Cascade(CascadeType.ALL)
    @JoinColumn(name=HibernateConsts.STUDENT_MAILING_FK)
    public Address getMailingAddress() {
        return mailingAddress;
    }

    public void setSourceSystemId(String sourceSystemId) {
        this.sourceSystemId = sourceSystemId;
    }

    public void setMailingAddress(Address mailingAddress) {
        this.mailingAddress = mailingAddress;
    }

    @OneToOne(optional = true)
    @Cascade(CascadeType.ALL)
    @JoinColumn(name=HibernateConsts.STUDENT_HOME_FK)
    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }

    @Column(name = HibernateConsts.STUDENT_GENDER)
    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Column(name = HibernateConsts.STUDENT_BIRTH_DATE)
    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    @Column(name = HibernateConsts.STUDENT_DISTRICT_ENTRY_DATE)
    public Date getDistrictEntryDate() {
        return districtEntryDate;
    }

    public void setDistrictEntryDate(Date districtEntryDate) {
        this.districtEntryDate = districtEntryDate;
    }

    @Column(name = HibernateConsts.STUDENT_PROJECTED_GRADUATION_YEAR)
    public Long getProjectedGraduationYear() {
        return projectedGraduationYear;
    }

    public void setProjectedGraduationYear(Long projectedGraduationYear) {
        this.projectedGraduationYear = projectedGraduationYear;
    }

    @Column(name = HibernateConsts.STUDENT_SOCIAL_SECURITY_NUM)
    public String getSocialSecurityNumber() {
        return socialSecurityNumber;
    }

    public void setSocialSecurityNumber(String socialSecurityNumber) {
        this.socialSecurityNumber = socialSecurityNumber;
    }

    @Column(name = HibernateConsts.STUDENT_FEDERAL_RACE)
    public String getFederalRace() {
        return federalRace;
    }

    public void setFederalRace(String federalRace) {
        this.federalRace = federalRace;
    }

    @Column(name = HibernateConsts.STUDENT_FEDERAL_ETHNICITY)
    public String getFederalEthnicity() {
        return federalEthnicity;
    }

    public void setFederalEthnicity(String federalEthnicity) {
        this.federalEthnicity = federalEthnicity;
    }

    @Column(name = HibernateConsts.SCHOOL_FK, nullable = true)
    public Long getCurrentSchoolId() {
        return currentSchoolId;
    }

    public void setCurrentSchoolId(Long currentSchoolId) {
        this.currentSchoolId = currentSchoolId;
    }
    
    @Override
    @Transient
    public UserType getType() {
        return UserType.STUDENT;
    }

    @Column(name = HibernateConsts.STUDENT_USER_FK, insertable = false, updatable = false)
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        final Student other = (Student) obj;
        return Objects.equals(this.sourceSystemId, other.sourceSystemId)
                && Objects.equals(this.mailingAddress, other.mailingAddress)
                && Objects.equals(this.homeAddress, other.homeAddress)
                && Objects.equals(this.gender, other.gender)
                && Objects.equals(this.birthDate, other.birthDate)
                && Objects.equals(this.districtEntryDate, other.districtEntryDate)
                && Objects.equals(this.projectedGraduationYear, other.projectedGraduationYear)
                && Objects.equals(this.socialSecurityNumber, other.socialSecurityNumber)
                && Objects.equals(this.federalRace, other.federalRace)
                && Objects.equals(this.federalEthnicity, other.federalEthnicity)
                && Objects.equals(this.currentSchoolId, other.currentSchoolId)
                && Objects.equals(this.userId, other.userId);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode()
                + Objects.hash(sourceSystemId, mailingAddress, homeAddress, gender, birthDate,
                        districtEntryDate, projectedGraduationYear, socialSecurityNumber, 
                        federalRace, federalEthnicity, currentSchoolId, userId);
    }

    @Override
    public String toString() {
        return "Student{" +
                ", sourceSystemId='" + sourceSystemId + '\'' +
                ", mailingAddress=" + mailingAddress +
                ", homeAddress=" + homeAddress +
                ", gender=" + gender +
                ", birthDate=" + birthDate +
                ", districtEntryDate=" + districtEntryDate +
                ", projectedGraduationYear=" + projectedGraduationYear +
                ", socialSecurityNumber='" + socialSecurityNumber + '\'' +
                ", federalRace='" + federalRace + '\'' +
                ", federalEthnicity='" + federalEthnicity + '\'' +
                ", currentSchoolId=" + currentSchoolId +
                ", userId=" + userId +
                '}';
    }
}
