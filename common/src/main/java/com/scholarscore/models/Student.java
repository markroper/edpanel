package com.scholarscore.models;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.DimensionField;

/**
 * The student class expresses a single student with a unique ID per school district.
 * 
 * Every student has a unique ID within a school district, but also may have unique IDs 
 * within a state (e.g. SSID) or a country (e.g. SSN).
 * 
 * @author markroper
 *
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Student extends ApiModel implements Serializable, IApiModel<Student>{
    public static final DimensionField STUDENT_ID = new DimensionField(Dimension.STUDENT, "ID");
    public static final DimensionField STUDENT_NAME = new DimensionField(Dimension.STUDENT, "Name");
    public static final DimensionField STUDENT_GENDER = new DimensionField(Dimension.STUDENT, "Gender");
    public static final DimensionField STUDENT_AGE = new DimensionField(Dimension.STUDENT, "Age");
//    public static final DimensionField STUDENT_FREE_LUNCH = new DimensionField(Dimension.STUDENT, "Free Lunch");
//    public static final DimensionField STUDENT_GRADE_REPEATER = new DimensionField(Dimension.STUDENT, "Grade Repeater");
    public static final DimensionField STUDENT_ETHNICITY = new DimensionField(Dimension.STUDENT, "Ethnicity");
    public static final DimensionField STUDENT_RACE = new DimensionField(Dimension.STUDENT, "Race");
//    public static final DimensionField STUDENT_ELL = new DimensionField(Dimension.STUDENT, "ELL");
//    public static final DimensionField STUDENT_SPECIAL_ED = new DimensionField(Dimension.STUDENT, "Special Ed");
    public static final DimensionField STUDENT_CITY_OF_RESIDENCE = new DimensionField(Dimension.STUDENT, "City of Residence");
    
    public static final Set<DimensionField> DIMENSION_FIELDS = new HashSet<DimensionField>() {{
        add(STUDENT_NAME);
        add(STUDENT_ID);
        add(STUDENT_GENDER);
//        add(STUDENT_FREE_LUNCH);
//        add(STUDENT_GRADE_REPEATER);
        add(STUDENT_ETHNICITY);
        add(STUDENT_RACE);
//        add(STUDENT_ELL);
//        add(STUDENT_SPECIAL_ED);
        add(STUDENT_CITY_OF_RESIDENCE);
        add(STUDENT_AGE);
    }};
    
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
    private Long sourceSystemId;
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
    }

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public User getLogin() {
		return login;
	}

	public void setLogin(User login) {
		this.login = login;
	}

	public Long getSourceSystemId() {
        return sourceSystemId;
    }

    public void setSourceSystemId(Long sourceSystemId) {
        this.sourceSystemId = sourceSystemId;
    }

    public Address getMailingAddress() {
        return mailingAddress;
    }

    public void setMailingAddress(Address mailingAddress) {
        this.mailingAddress = mailingAddress;
    }

    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Date getDistrictEntryDate() {
        return districtEntryDate;
    }

    public void setDistrictEntryDate(Date districtEntryDate) {
        this.districtEntryDate = districtEntryDate;
    }

    public Long getProjectedGraduationYear() {
        return projectedGraduationYear;
    }

    public void setProjectedGraduationYear(Long projectedGraduationYear) {
        this.projectedGraduationYear = projectedGraduationYear;
    }

    public String getSocialSecurityNumber() {
        return socialSecurityNumber;
    }

    public void setSocialSecurityNumber(String socialSecurityNumber) {
        this.socialSecurityNumber = socialSecurityNumber;
    }

    public String getFederalRace() {
        return federalRace;
    }

    public void setFederalRace(String federalRace) {
        this.federalRace = federalRace;
    }

    public String getFederalEthnicity() {
        return federalEthnicity;
    }

    public void setFederalEthnicity(String federalEthnicity) {
        this.federalEthnicity = federalEthnicity;
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
                && Objects.equals(this.federalEthnicity, other.federalEthnicity);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode()
                + Objects.hash(username, login, sourceSystemId, mailingAddress, homeAddress, gender, birthDate, 
                        districtEntryDate, projectedGraduationYear, socialSecurityNumber, federalRace, federalEthnicity);
    }
}
