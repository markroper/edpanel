package com.scholarscore.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * All entities in the system are assigned a unique ID within an individual 
 * tenant of the system, for example, a school district.  Some API calls,
 * including POST calls to create entities, return the ID of the entity created.
 * This ID must be of the form below, which is enabled by this class:<p/>
 * 
 * { "id": 123 }
 * 
 * @author markroper
 *
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntityId implements Serializable {
    public Long id;
    
    public EntityId () {
        
    }
    
    public EntityId(Long id) {
        this.id = id;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
}
