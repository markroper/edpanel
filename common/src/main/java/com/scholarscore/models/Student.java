package com.scholarscore.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

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
