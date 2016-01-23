package com.scholarscore.models.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.Address;
import com.scholarscore.models.Gender;
import com.scholarscore.models.HibernateConsts;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Transient;
import java.io.Serializable;
import java.time.LocalDate;
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
public class Student extends Person implements Serializable {
    private Address mailingAddress;
    //Demographics
    private Gender gender;
    private LocalDate birthDate;
    private LocalDate districtEntryDate;
    private Long projectedGraduationYear;
    private String socialSecurityNumber;
    //EthnicityRace
    private String federalRace;
    private String federalEthnicity;
    
    public Student() {
        
    }
    
    public Student(Student student) {
        super(student);
        this.mailingAddress = student.mailingAddress;
        this.gender = student.gender;
        this.birthDate = student.birthDate;
        this.districtEntryDate = student.districtEntryDate;
        this.projectedGraduationYear = student.projectedGraduationYear;
        this.socialSecurityNumber = student.socialSecurityNumber;
        this.federalRace = student.federalRace;
        this.federalEthnicity = student.federalEthnicity;
    }
    
    public Student(String race, String ethnicity, Long currentSchoolId, Gender gender, String name, Long expectedGraduationYear) {
        this.federalRace = race;
        this.federalEthnicity = ethnicity;
        this.gender = gender;
        this.name = name;
        this.projectedGraduationYear = expectedGraduationYear;
        this.currentSchoolId = currentSchoolId;
    }
    
    @Override
    public void mergePropertiesIfNull(User mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);     
        if(mergeFrom instanceof Student) {
            Student merge = (Student) mergeFrom;
            if (null == getMailingAddress()) {
                setMailingAddress(merge.getMailingAddress());
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
    @Fetch(FetchMode.JOIN)
    @Cascade(CascadeType.ALL)
    @JoinColumn(name=HibernateConsts.STUDENT_MAILING_FK)
    public Address getMailingAddress() {
        return mailingAddress;
    }

    public void setMailingAddress(Address mailingAddress) {
        this.mailingAddress = mailingAddress;
    }

    @OneToOne(optional = true)
    @Cascade(CascadeType.ALL)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name=HibernateConsts.STUDENT_HOME_FK)
    public Address getHomeAddress() {
        return homeAddress;
    }

    @Column(name = HibernateConsts.STUDENT_GENDER)
    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Column(name = HibernateConsts.STUDENT_BIRTH_DATE)
    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    @Column(name = HibernateConsts.STUDENT_DISTRICT_ENTRY_DATE)
    public LocalDate getDistrictEntryDate() {
        return districtEntryDate;
    }

    public void setDistrictEntryDate(LocalDate districtEntryDate) {
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
    
    @Override
    @Transient
    public UserType getType() {
        return UserType.STUDENT;
    }

    @Column(name = HibernateConsts.STUDENT_USER_FK, insertable = false, updatable = false)
    public Long getUserId() {
        return getId();
    }

    public void setUserId(Long userId) {
        setId(userId);
    }

    @Override
    @Column(name = HibernateConsts.STUDENT_SOURCE_SYSTEM_USER_ID)
    public String getSourceSystemUserId() {
        return sourceSystemUserId;
    }

    /**
     * TODO: Students don't actually have this field persisted yet. Add to model & enable
     */
    @Override
    @Transient
    @JsonIgnore
    public String getHomePhone() {
        return homePhone;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        final Student other = (Student) obj;
        return Objects.equals(this.mailingAddress, other.mailingAddress)
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
                + Objects.hash(mailingAddress, gender, birthDate,
                        districtEntryDate, projectedGraduationYear, socialSecurityNumber, 
                        federalRace, federalEthnicity, currentSchoolId);
    }

    @Override
    public String toString() {
        return "Student{super(" + super.toString() + ")" +
                "mailingAddress=" + mailingAddress +
                ", gender=" + gender +
                ", birthDate=" + birthDate +
                ", districtEntryDate=" + districtEntryDate +
                ", projectedGraduationYear=" + projectedGraduationYear +
                ", socialSecurityNumber='" + socialSecurityNumber + '\'' +
                ", federalRace='" + federalRace + '\'' +
                ", federalEthnicity='" + federalEthnicity + '\'' +
                '}';
    }

    /**
     * Each class's Builder holds a copy of each attribute that the parent POJO has. We build up these properties using
     * a pattern of with[Attribute](Attribute attribute) and return the same instance of the Builder so that one can easily
     * chain setting attributes together.
     */
    public static class StudentBuilder extends PersonBuilder<StudentBuilder, Student> {

        //Source system identifier. E.g. powerschool ID
        private String sourceSystemId;
        //Addresses
        private Address mailingAddress;
        private Address homeAddress;
        //Demographics
        private Gender gender;
        private LocalDate birthDate;
        private LocalDate districtEntryDate;
        private Long projectedGraduationYear;
        private String socialSecurityNumber;
        //EthnicityRace
        private String federalRace;
        private String federalEthnicity;
        private Long currentSchoolId;

        public StudentBuilder withSourceSystemId(final String sourceSystemId){
            this.sourceSystemId = sourceSystemId;
            return this;
        }

        public StudentBuilder withMailingAddress(final Address mailingAddress){
            this.mailingAddress = mailingAddress;
            return this;
        }

        public StudentBuilder withHomeAddress(final Address homeAddress){
            this.homeAddress = homeAddress;
            return this;
        }

        public StudentBuilder withGender(final Gender gender){
            this.gender = gender;
            return this;
        }

        public StudentBuilder withBirthDate(final LocalDate birthDate){
            this.birthDate = birthDate;
            return this;
        }

        public StudentBuilder withDistrictEntryDate(final LocalDate districtEntryDate){
            this.districtEntryDate = districtEntryDate;
            return this;
        }

        public StudentBuilder withProjectedGraduationYear(final Long projectedGraduationYear){
            this.projectedGraduationYear = projectedGraduationYear;
            return this;
        }

        public StudentBuilder withSocialSecurityNumber(final String socialSecurityNumber){
            this.socialSecurityNumber = socialSecurityNumber;
            return this;
        }

        public StudentBuilder withFederalRace(final String federalRace){
            this.federalRace = federalRace;
            return this;
        }

        public StudentBuilder withFederalEthnicity(final String federalEthnicity){
            this.federalEthnicity = federalEthnicity;
            return this;
        }

        public StudentBuilder withCurrentSchoolId(final Long currentSchoolId){
            this.currentSchoolId = currentSchoolId;
            return this;
        }

        public Student build(){
            Student student = super.build();
            student.setSourceSystemId(sourceSystemId);
            student.setMailingAddress(mailingAddress);
            student.setHomeAddress(homeAddress);
            student.setGender(gender);
            student.setBirthDate(birthDate);
            student.setDistrictEntryDate(districtEntryDate);
            student.setProjectedGraduationYear(projectedGraduationYear);
            student.setSocialSecurityNumber(socialSecurityNumber);
            student.setFederalRace(federalRace);
            student.setFederalEthnicity(federalEthnicity);
            student.setCurrentSchoolId(currentSchoolId);
            return student;
        }

        @Override
        protected StudentBuilder me() {
            return this;
        }

        public Student getInstance(){
            return new Student();
        }
    }
}
