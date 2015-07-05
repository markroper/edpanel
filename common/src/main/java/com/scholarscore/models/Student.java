package com.scholarscore.models;

import java.io.Serializable;
import java.util.HashSet;
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
    public static final DimensionField STUDENT_FREE_LUNCH = new DimensionField(Dimension.STUDENT, "Free Lunch");
    public static final DimensionField STUDENT_GRADE_REPEATER = new DimensionField(Dimension.STUDENT, "Grade Repeater");
    public static final DimensionField STUDENT_ETHNICITY = new DimensionField(Dimension.STUDENT, "Ethnicity");
    public static final DimensionField STUDENT_RACE = new DimensionField(Dimension.STUDENT, "Race");
    public static final DimensionField STUDENT_ELL = new DimensionField(Dimension.STUDENT, "ELL");
    public static final DimensionField STUDENT_SPECIAL_ED = new DimensionField(Dimension.STUDENT, "Special Ed");
    public static final DimensionField STUDENT_CITY_OF_RESIDENCE = new DimensionField(Dimension.STUDENT, "City of Residence");
    
    public static final Set<DimensionField> DIMENSION_FIELDS = new HashSet<DimensionField>() {{
        add(STUDENT_NAME);
        add(STUDENT_ID);
        add(STUDENT_GENDER);
        add(STUDENT_FREE_LUNCH);
        add(STUDENT_GRADE_REPEATER);
        add(STUDENT_ETHNICITY);
        add(STUDENT_RACE);
        add(STUDENT_ELL);
        add(STUDENT_SPECIAL_ED);
        add(STUDENT_CITY_OF_RESIDENCE);
        add(STUDENT_AGE);
    }};
    
    public Student() {
        
    }
    
    public Student(Student student) {
        super(student);
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
    
    @Override
    public void mergePropertiesIfNull(Student mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);     
        if (null == getUsername()) {
        	setUsername(mergeFrom.getUsername());
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((login == null) ? 0 : login.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Student other = (Student) obj;
		if (login == null) {
			if (other.login != null)
				return false;
		} else if (!login.equals(other.login))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
}
