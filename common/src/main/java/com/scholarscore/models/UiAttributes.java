package com.scholarscore.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

@Entity(name=HibernateConsts.UI_ATTRIBUTES_TABLE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UiAttributes implements Serializable {
    private static final long serialVersionUID = 1L;
    protected Long id;
    protected School school;
    protected JsonAttributes attributes;
    
    public UiAttributes() {
        
    }
    
    public UiAttributes(UiAttributes uiAttributes) {
        this.id = uiAttributes.id;
        if(null != uiAttributes.school) {
            this.school = new School(uiAttributes.school);
        }
        this.attributes = uiAttributes.attributes;
    }

    public static UiAttributes resolveDefaults(School s) {
        //create UI attributes for school
        UiAttributes attrs = new UiAttributes();
        attrs.setSchool(s);
        try {
            attrs.setAttributes(new JsonAttributes("{" +
                    "\"attendance\":{" +
                    "\"name\":\"Attendance\"," +
                    "\"isTemporal\":true," +
                    "\"thresholdChar\":\"#\"," +
                    "\"green\":1," +
                    "\"yellow\":4," +
                    "\"period\":\"year\"" +
                    "}," +
                    "\"behavior\":{" +
                    "\"name\":\"Attendance\"," +
                    "\"isTemporal\":true," +
                    "\"thresholdChar\":\"#\"," +
                    "\"period\":\"week\"," +
                    "\"green\":85," +
                    "\"yellow\":75" +
                    "}," +
                    "\"homework\":{" +
                    "\"name\":\"Attendance\"," +
                    "\"isTemporal\":false," +
                    "\"thresholdChar\":\"%\"" +
                    "}," +
                    "\"gpa\":{" +
                    "\"name\":\"Attendance\"," +
                    "\"isTemporal\":false," +
                    "\"thresholdChar\":\"#\"," +
                    "\"green\":3.3," +
                    "\"yellow\":3" +
                    "}" +
                    "}"));
        } catch (IOException e) {
        }
        return attrs;
    }

    @OneToOne(optional = false)
    @JoinColumn(name=HibernateConsts.SCHOOL_FK, nullable = false)
    public School getSchool() {
        return school;
    }
    public void setSchool(School school) {
        this.school = school;
    }
    
    @Column(name = HibernateConsts.UI_ATTRIBUTES, columnDefinition="blob")
    @Convert(converter = JsonAttributes.JsonAttributesConverter.class)
    public JsonAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(JsonAttributes attributes) {
        this.attributes = attributes;
    }
    
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = HibernateConsts.UI_ATTRIBUTES_ID)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    @Override
    public boolean equals(Object obj) {
    if (this == obj) {
        return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
        return false;
    }
    final UiAttributes other = (UiAttributes) obj;
    return Objects.equals(this.school, other.school)
            && Objects.equals(this.attributes, other.attributes)
            && Objects.equals(this.id, other.id);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(school, attributes, id);
    }
}
