package com.scholarscore.models;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.DimensionField;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;

@Entity(name = "teacher")
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Teacher extends ApiModel implements Serializable, IStaff<Teacher> {
    
    public Teacher() {
    }
    
    public Teacher(Teacher t) {
        super(t);
        this.setLogin(t.getLogin());
        this.setSourceSystemId(t.getSourceSystemId());
        this.setHomeAddress(t.getHomeAddress());
        this.setHomePhone(t.getHomePhone());
        this.setUsername(t.getUsername());
    }
    
    // FK to the Users table entry
    @JsonIgnore
    private String username;
    
    @JsonInclude
    private transient User login;

    private String sourceSystemId;
    private Address homeAddress;
    private String homePhone;

    @Override
    public void mergePropertiesIfNull(Teacher mergeFrom) {
        // MJG: do we merge address properties if null too?
        if (null == this.getHomeAddress()) {
            this.setHomeAddress(mergeFrom.getHomeAddress());
        }
        if (null == this.getHomePhone()) {
            this.setHomePhone(mergeFrom.getHomePhone());
        }
        if (null == this.getSourceSystemId()) {
            this.setSourceSystemId(mergeFrom.getSourceSystemId());
        }
        if (null == this.getUsername()) {
            this.setUsername(mergeFrom.getUsername());
        }
        super.mergePropertiesIfNull(mergeFrom);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Teacher)) return false;
        if (!super.equals(o)) return false;

        Teacher teacher = (Teacher) o;

        if (getUsername() != null ? !getUsername().equals(teacher.getUsername()) : teacher.getUsername() != null)
            return false;
        if (getLogin() != null ? !getLogin().equals(teacher.getLogin()) : teacher.getLogin() != null) return false;
        if (getSourceSystemId() != null ? !getSourceSystemId().equals(teacher.getSourceSystemId()) : teacher.getSourceSystemId() != null)
            return false;
        if (getHomeAddress() != null ? !getHomeAddress().equals(teacher.getHomeAddress()) : teacher.getHomeAddress() != null)
            return false;
        return !(getHomePhone() != null ? !getHomePhone().equals(teacher.getHomePhone()) : teacher.getHomePhone() != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getUsername() != null ? getUsername().hashCode() : 0);
        result = 31 * result + (getLogin() != null ? getLogin().hashCode() : 0);
        result = 31 * result + (getSourceSystemId() != null ? getSourceSystemId().hashCode() : 0);
        result = 31 * result + (getHomeAddress() != null ? getHomeAddress().hashCode() : 0);
        result = 31 * result + (getHomePhone() != null ? getHomePhone().hashCode() : 0);
        return result;
    }

    @Transient
    public User getLogin() {
        return login;
    }

    @Column(name = "teacher_name")
    public String getName() {
        return super.getName();
    }

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "teacher_id")
    public Long getId() {
        return super.getId();
    }

    @OneToOne(optional = true)
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name="teacher_homeAddress_fk")
    public Address getHomeAddress() {
        return homeAddress;
    }

    @Column(name = "teacher_home_phone")
    public String getHomePhone() {
        return homePhone;
    }

    @Column(name = "teacher_source_system_id")
    public String getSourceSystemId() {
        return sourceSystemId;
    }

    @Column(name = "teacher_username")
    public String getUsername() {
        return username;
    }

    public void setSourceSystemId(String sourceSystemId) {
        this.sourceSystemId = sourceSystemId;
    }

    public void setLogin(User login) {
        this.login = login;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
