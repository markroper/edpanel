package com.scholarscore.models;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.scholarscore.util.ObjectNodeConverter;

@Entity(name=HibernateConsts.UI_ATTRIBUTES_TABLE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UiAttributes implements Serializable {
    private static final long serialVersionUID = 1L;
    protected Long id;
    protected School school;
    protected ObjectNode attributes;
    
    public UiAttributes() {
        
    }
    
    @OneToOne(optional = false)
    @JoinColumn(name=HibernateConsts.SCHOOL_FK, nullable = false)
    public School getSchool() {
        return school;
    }
    public void setSchool(School school) {
        this.school = school;
    }
    
    @Column(name = HibernateConsts.UI_ATTRIBUTES)
    @Convert(converter = ObjectNodeConverter.class)
    public ObjectNode getAttributes() {
        return attributes;
    }
    public void setAttributes(ObjectNode attributes) {
        this.attributes = attributes;
    }
    public static long getSerialversionuid() {
        return serialVersionUID;
    }
    
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = HibernateConsts.UI_ATTRIBUTES_ID)
    @JsonIgnore
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
