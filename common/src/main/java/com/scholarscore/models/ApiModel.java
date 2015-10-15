package com.scholarscore.models;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;

@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ApiModel implements Serializable {
    protected Long id;

    @Size(min=1, max=256)
    protected String name;
    
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

    @Id
    @Column(name = "id")
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

    @Override
    public String toString() {
        return "ApiModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    /**
     * Each class's Builder holds a copy of each attribute that the parent POJO has. We build up these properties using
     * a pattern of with[Attribute](Attribute attribute) and return the same instance of the Builder so that one can easily
     * chain setting attributes together.
     */
    public static abstract class ApiModelBuilder<U extends ApiModelBuilder<U,T>, T extends ApiModel> {

        private Long id;
        protected String name;

        public abstract U me();

        public abstract T getInstance();

        public U withId(final Long id){
            this.id = id;
            return me();
        }

        public U withName(final String name){
            this.name = name;
            return me();
        }

        public T build(){
            T apiModel = getInstance();
            apiModel.setId(id);
            apiModel.setName(name);
            return apiModel;
        }

    }
}

