package com.scholarscore.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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
@Table(name = HibernateConsts.STUDENT_TABLE)
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Student extends Identity implements Serializable, IApiModel<Student>{
    
    public Student() {
        
    }
    
    public Student(Student student) {
        super(student);
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

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = HibernateConsts.STUDENT_ID)
    public Long getId() {
        return super.getId();
    }
    
    public Student(String race, String ethnicity, Long currentSchoolId, Gender gender, String name, Long expectedGraduationYear) {
        this.federalRace = race;
        this.federalEthnicity = ethnicity;
        this.currentSchoolId = currentSchoolId;
        this.gender = gender;
        this.name = name;
        this.projectedGraduationYear = expectedGraduationYear;
    }
    
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
    
    @Override
    public void mergePropertiesIfNull(Student mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);     
        if (null == getUsername()) {
        	setUsername(mergeFrom.getUsername());
        }
        if (null == getUser()) {
            setUser(mergeFrom.getUser());
        }
        if (null == getSourceSystemId()) {
            setSourceSystemId(mergeFrom.getSourceSystemId());
        }
        if (null == getMailingAddress()) {
            setMailingAddress(mergeFrom.getMailingAddress());
        }
        if (null == getHomeAddress()) {
            setHomeAddress(mergeFrom.getHomeAddress());
        }
        if (null == getGender()) {
            setGender(mergeFrom.getGender());
        }
        if (null == getBirthDate()) {
            setBirthDate(mergeFrom.getBirthDate());
        }
        if (null == getDistrictEntryDate()) {
            setDistrictEntryDate(mergeFrom.getDistrictEntryDate());
        }
        if (null == getProjectedGraduationYear()) {
            setProjectedGraduationYear(mergeFrom.getProjectedGraduationYear());
        }
        if (null == getSocialSecurityNumber()) {
            setSocialSecurityNumber(mergeFrom.getSocialSecurityNumber());
        }
        if (null == getFederalRace()) {
            setFederalRace(mergeFrom.getFederalRace());
        }
        if (null == getFederalEthnicity()) {
            setFederalEthnicity(mergeFrom.getFederalEthnicity());
        }
        if(null == getCurrentSchoolId()) {
            setCurrentSchoolId(mergeFrom.getCurrentSchoolId());
        }
    }

//    @Transient
    @Override
    @OneToOne(optional = true)
    @Cascade(CascadeType.ALL)
    @JoinColumn(name=HibernateConsts.STUDENT_USER_FK)
    public User getUser() {
        return super.getUser();
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

//    @Column(name = HibernateConsts.USER_NAME)
//    public String getUsername() {
//        return super.getUsername();
//    }

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
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        final Student other = (Student) obj;
        return Objects.equals(this.username, other.username)
                && Objects.equals(this.user, other.user)
                && Objects.equals(this.sourceSystemId, other.sourceSystemId)
                && Objects.equals(this.mailingAddress, other.mailingAddress)
                && Objects.equals(this.homeAddress, other.homeAddress)
                && Objects.equals(this.gender, other.gender)
                && Objects.equals(this.birthDate, other.birthDate)
                && Objects.equals(this.districtEntryDate, other.districtEntryDate)
                && Objects.equals(this.projectedGraduationYear, other.projectedGraduationYear)
                && Objects.equals(this.socialSecurityNumber, other.socialSecurityNumber)
                && Objects.equals(this.federalRace, other.federalRace)
                && Objects.equals(this.federalEthnicity, other.federalEthnicity)
                && Objects.equals(this.currentSchoolId, other.currentSchoolId);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode()
                + Objects.hash(username, user, sourceSystemId, mailingAddress, homeAddress, gender, birthDate,
                        districtEntryDate, projectedGraduationYear, socialSecurityNumber, federalRace, federalEthnicity, currentSchoolId);
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", user=" + user +
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
                '}';
    }
}
