package com.scholarscore.models;

import java.io.Serializable;
import java.util.Objects;

import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;

@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ApiModel implements Serializable {
    private Long id;
    @Size(min=1, max=256)
    private String name;
    
    public ApiModel() {
        
    }
    
    public ApiModel(ApiModel model) {
        this.id = model.id;
        this.name = model.name;
    }
    
    public void mergePropertiesIfNull(ApiModel mergeFrom) {
        if(null == mergeFrom) {
            return;
        }
        if(null == this.id) {
            this.id = mergeFrom.id;
        }
        if(null == this.name) {
            this.name = mergeFrom.name;
        }      
    }
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ApiModel other = (ApiModel) obj;
        return Objects.equals(this.id, other.id) && Objects.equals(this.name, other.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
    
}

