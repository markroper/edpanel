package com.scholarscore.models;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;

/**
 * The student class expresses a single student with a unique ID per school district.
 * 
 * Every student has a unique ID within a school district, but also may have unique IDs 
 * within a state (e.g. SSID) or a country (e.g. SSN).
 * 
 * @author markroper
 *
 */
@Entity(name = "student")
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Student extends ApiModel implements Serializable, IApiModel<Student>{
    
    public Student() {
        
    }
    
    public Student(Student student) {
        super(student);
        this.username = student.username;
        this.login = student.login;
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
    @Column(name = "student_id")
    public Long getId() {
        return super.getId();
    }

    // FK to the Users table, this is optional as a 1:1 relationship does not need to exist between
    // a user and a student.  A student can exist without a login.  Currently spring security requires
    // this as the PK of the table, this should be changed to an id column as usernames may be able to
    // change in the future? This should be hidden from the exported model
    @JsonIgnore
    private String username;
    // A loaded version of the user identity
    @JsonInclude
    private transient User login;
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
        if (null == getLogin()) {
            setLogin(mergeFrom.getLogin());
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

	public String getUsername() {
		return username;
	}

    @Transient
    public User getLogin() {
        return login;
    }
    @Column(name = "source_system_id")
    public String getSourceSystemId() {
        return sourceSystemId;
    }

    @Column(name = "student_name")
    public String getName() {
        return super.getName();
    }

    @OneToOne(optional = true)
    @Cascade(CascadeType.ALL)
    @JoinColumn(name="mailing_fk")
    public Address getMailingAddress() {
        return mailingAddress;
    }

	public void setUsername(String username) {
		this.username = username;
	}

	public void setLogin(User login) {
		this.login = login;
	}

    public void setSourceSystemId(String sourceSystemId) {
        this.sourceSystemId = sourceSystemId;
    }

    public void setMailingAddress(Address mailingAddress) {
        this.mailingAddress = mailingAddress;
    }

    @OneToOne(optional = true)
    @Cascade(CascadeType.ALL)
    @JoinColumn(name="home_fk")
    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }

    @Column(name = "gender")
    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Column(name = "birth_date")
    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    @Column(name = "district_entry_date")
    public Date getDistrictEntryDate() {
        return districtEntryDate;
    }

    public void setDistrictEntryDate(Date districtEntryDate) {
        this.districtEntryDate = districtEntryDate;
    }

    @Column(name = "projected_graduation_year")
    public Long getProjectedGraduationYear() {
        return projectedGraduationYear;
    }

    public void setProjectedGraduationYear(Long projectedGraduationYear) {
        this.projectedGraduationYear = projectedGraduationYear;
    }

    @Column(name = "social_security_number")
    public String getSocialSecurityNumber() {
        return socialSecurityNumber;
    }

    public void setSocialSecurityNumber(String socialSecurityNumber) {
        this.socialSecurityNumber = socialSecurityNumber;
    }

    @Column(name = "federal_race")
    public String getFederalRace() {
        return federalRace;
    }

    public void setFederalRace(String federalRace) {
        this.federalRace = federalRace;
    }

    @Column(name = "federal_ethnicity")
    public String getFederalEthnicity() {
        return federalEthnicity;
    }

    public void setFederalEthnicity(String federalEthnicity) {
        this.federalEthnicity = federalEthnicity;
    }

    //@Column(name = "school_fk", nullable = true)
    @Transient
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
                && Objects.equals(this.login, other.login)
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
                + Objects.hash(username, login, sourceSystemId, mailingAddress, homeAddress, gender, birthDate, 
                        districtEntryDate, projectedGraduationYear, socialSecurityNumber, federalRace, federalEthnicity, currentSchoolId);
    }

    @Override
    public String toString() {
        return "Student{" +
                "username='" + username + '\'' +
                ", login=" + login +
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
